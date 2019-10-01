package at.alladin.nettest.service.collector.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import at.alladin.nettest.service.collector.config.ExternalServicesProperties;
import at.alladin.nettest.service.collector.config.ExternalServicesProperties.DeviceImportSettings;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Device;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.DeviceRepository;

/**
 *
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@Service
public class DeviceImportService {

	private static final Logger logger = LoggerFactory.getLogger(DeviceImportService.class);

	@Autowired
	ExternalServicesProperties properties;

	@Autowired
	TaskScheduler taskScheduler;

	@Autowired
	DeviceRepository deviceRepository; // TODO: use StorageService or equivalent service instead of using couchdb repository directly.

	Map<String, ScheduledFuture<?>> jobs = new HashMap<>();

	@PostConstruct
	public void init() {
		if (properties != null && properties.getDeviceImports() != null) {
			for (final DeviceImportSettings settings : properties.getDeviceImports()) {
				if (settings.isEnabled()) {
					logger.debug("Initializing device import for: {}, using cron: {}", settings.getName(), settings.getCron());
					final ScheduledFuture<?> task = taskScheduler.schedule(new ImportRunnable(settings), new CronTrigger(settings.getCron()));
					jobs.put(settings.getName(), task);
				}
			}
		}
	}

	public class ImportRunnable implements Runnable {

		private DeviceImportSettings settings;

		private HttpClient httpClient;

		public ImportRunnable(final DeviceImportSettings settings) {
			this.settings = settings;
			this.httpClient = HttpClients.createDefault();
		}

		@Override
		public void run() {
			//bulkDelete();

			logger.debug("Executing device import: {}", settings.getName());
			final Path path = downloadToTempFile(settings.getUrl(), "temp_" + settings.getName());

			final Map<String, Device> newDevices = new HashMap<>();
			final CsvMapper csvMapper = new CsvMapper();
			csvMapper.enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE);
			final CsvSchema schema = CsvSchema.builder()
					.addColumn("skip1")
					.addColumn("full_name")
					.addColumn("code_name")
					.build();

			try {
				File csvFile = path.toFile();
				MappingIterator<Device> it = csvMapper.readerFor(Device.class).with(schema).readValues(csvFile);
				while (it.hasNext()) {
					final Device device = it.next();
					newDevices.put(device.getCodename(), device);
				}
			}
			catch (final Exception e) {
				e.printStackTrace();
			}

			List<Device> updatedDevices = new ArrayList<Device>();
			Pageable pageable = PageRequest.of(0, 200);
			boolean isLastPage = false;

			int updates = 0;
			int inserts = 0;
			int notUpdated = 0;

			logger.debug("Found {} total devices. Starting update process.", newDevices.size());

			while (!isLastPage) {
				Page<Device> page = deviceRepository.getAllDevices(pageable);
				pageable = page.nextPageable();

				if (page.hasContent()) {
					for (final Device device: page.getContent()) {
						final Device d = newDevices.remove(device.getCodename());
						notUpdated++;
						if (d != null && d.getCodename() != null && d.getFullname() != null) {
							if (!d.getFullname().equals(device.getFullname())) {
								device.setCodename(d.getCodename());
								updatedDevices.add(device);
							}
						}
					}
				}

				if (updatedDevices.size() > 0) {
					deviceRepository.saveAll(updatedDevices);
					updates += updatedDevices.size();
					updatedDevices.clear();
				}

				isLastPage = !page.hasNext();
			}

			for (Device device : newDevices.values()) {
				updatedDevices.add(device);
				inserts += bulkUpdate(updatedDevices, 200);
			}


			logger.debug("Device import finished. New devices: {}, updated: {}, not updated: {}", inserts, updates, notUpdated);
		}

		/*
		private void bulkDelete() {
			Pageable pageable = PageRequest.of(0, 200);
			boolean isLastPage = false;

			while (!isLastPage) {
				Page<Device> page = deviceRepository.getAllDevices(pageable);
				pageable = page.nextPageable();

				if (page.hasContent()) {
					for (Device d : page.getContent())
					deviceRepository.delete(d);
				}

				isLastPage = !page.hasNext();
			}
		}
		*/

		private int bulkUpdate(final List<Device> devices, final int threshold) {
			int i = 0;
			if (devices.size() >= threshold) {
				i = devices.size();
				deviceRepository.saveAll(devices);
				devices.clear();
			}

			return i;
		}

		private Path downloadToTempFile(String url, String tempFileName) {
			final HttpGet request = new HttpGet(url);

			try {
				logger.info("downloading: {}", url);

				final HttpResponse response = httpClient.execute(request);
				final HttpEntity entity = response.getEntity();

				logger.info("contentType: {}", entity.getContentType().getValue());

				String extension = ".tmp";

				switch (entity.getContentType().getValue()) {
					case "application/zip":
					case "application/x-zip":
					case "application/x-zip-compressed":
					case "application/octet-stream":
					case "application/x-compress":
					case "application/x-compressed":
					case "multipart/x-zip":
						extension = ".zip";
						break;

					case "text/comma-separated-values":
					case "text/csv":
					case "application/csv":
					case "application/excel":
					case "application/vnd.ms-excel":
					case "application/vnd.msexcel":
						extension = ".csv";
						break;

					default:
						final int pos = url.lastIndexOf(".");
						if (pos > 0 && ((url.length() - pos) < 5)) {
							extension = url.substring(pos);
						}
				}

				final Path tmpFilePath = Files.createTempFile(tempFileName, extension);

				Files.copy(entity.getContent(), tmpFilePath, StandardCopyOption.REPLACE_EXISTING);

				logger.info("Download completed: {}", tempFileName);

				return tmpFilePath;
			} catch (Exception ex) {
				logger.error("failed to download file from url " + url, ex);
				return null;
			}
		}
	}
}
