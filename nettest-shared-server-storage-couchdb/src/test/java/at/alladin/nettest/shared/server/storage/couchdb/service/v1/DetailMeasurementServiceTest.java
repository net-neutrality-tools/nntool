package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementGroup;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementGroupItem;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SpeedtestDetailGroup;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SpeedtestDetailGroup.SpeedtestDetailGroupEntry;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SpeedtestDetailGroup.SpeedtestDetailGroupEntry.FormatEnum;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgentInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgentType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SpeedMeasurement;

public class DetailMeasurementServiceTest {

	private DetailMeasurementService detailMeasurementService;
	
	private Measurement measurement;
	
	private List<SpeedtestDetailGroup> groupStructure;
	
	private Locale locale;
	
	@Before
	public void init() {
		locale = Locale.ENGLISH;
		
		detailMeasurementService = new DetailMeasurementService();
		
		measurement = new Measurement();
		measurement.setTag("tag");
		final MeasurementAgentInfo info = new MeasurementAgentInfo();
		measurement.setAgentInfo(info);
		info.setType(MeasurementAgentType.MOBILE);
		info.setUuid("uuid");
		info.setLanguage("en");
		
		measurement.setMeasurements(new HashMap<>());
		final SpeedMeasurement speed = new SpeedMeasurement();
		measurement.getMeasurements().put(MeasurementTypeDto.SPEED, speed);
		speed.setBytesDownload(2242434L);
		speed.setBytesUpload(3455454L);
		speed.setImplausible(false);
		
		groupStructure = new ArrayList<>();
		SpeedtestDetailGroup group = new SpeedtestDetailGroup();
		groupStructure.add(group);
		group.setKey("a_key");
		group.setIcon("a_icon");
		
		List<SpeedtestDetailGroupEntry> entryList = new ArrayList<>();
		group.setValues(entryList);
		SpeedtestDetailGroupEntry entry = new SpeedtestDetailGroupEntry();
		entry.setKey("agentInfo.uuid");
		entry.setTranslationKey("agent_uuid_translate");
		entryList.add(entry);
		
		entry = new SpeedtestDetailGroupEntry();
		entry.setKey("tag");
		entry.setTranslationKey("tag_translate");
		entryList.add(entry);
		
		
		group = new SpeedtestDetailGroup();
		groupStructure.add(group);
		group.setKey("b_key");
		group.setIcon("b_icon");
		
		entryList = new ArrayList<>();
		group.setValues(entryList);
		entry = new SpeedtestDetailGroupEntry();
		entry.setKey("speed.bytesDownload");
		entry.setFormat(FormatEnum.DIV_1E6);
		entry.setUnit("bytes");
		entry.setTranslationKey("bytes_download_translate");
		entryList.add(entry);
		
		entry = new SpeedtestDetailGroupEntry();
		entry.setKey("speed.implausible");
		entry.setTranslationKey("implausible_translate");
		entryList.add(entry);
		
		
	}
	
	@Test
	public void basicMeasurementGroupingTestAllValidValues() {
		
		final DetailMeasurementResponse response = detailMeasurementService.groupResult(measurement, groupStructure, locale, 1);
		assertEquals("unexpected group count", 2, response.getGroups().size());
		final DetailMeasurementGroup groupA = response.getGroups().get(0);
		assertEquals("unexpected items in group", 2, groupA.getItems().size());
		
		final DetailMeasurementGroupItem uuidItem = groupA.getItems().get(0);
		assertEquals("unexpected value in item", "uuid", uuidItem.getValue());
		assertEquals("unexpected value in item", "agent_uuid_translate", uuidItem.getTitle());
		assertNull("wrong unit in item", uuidItem.getUnit());
		
		final DetailMeasurementGroupItem tagItem = groupA.getItems().get(1);
		assertEquals("unexpected value in item", "tag", tagItem.getValue());
		assertEquals("unexpected value in item", "tag_translate", tagItem.getTitle());
		assertNull("wrong unit in item", tagItem.getUnit());
		
		final DetailMeasurementGroup groupB = response.getGroups().get(1);
		assertEquals("unexpected items in group", 2, groupB.getItems().size());
		final DetailMeasurementGroupItem bytesDownItem = groupB.getItems().get(0);
		assertEquals("unexpected value in item", "2.24", bytesDownItem.getValue());
		assertEquals("unexpected value in item", "bytes_download_translate", bytesDownItem.getTitle());
		assertEquals("unexpected value in item", "bytes", bytesDownItem.getUnit());
		
		final DetailMeasurementGroupItem implausibleItem = groupB.getItems().get(1);
		assertEquals("unexpected value in item", "false", implausibleItem.getValue());
		assertEquals("unexpected value in item", "implausible_translate", implausibleItem.getTitle());
		assertNull("wrong unit in item", implausibleItem.getUnit());
	}
	
	@Test
	public void groupWithNoEntriesIsNotAddedTest() {
		
		final SpeedtestDetailGroup invalidGroup = new SpeedtestDetailGroup();
		groupStructure.add(invalidGroup);
		invalidGroup.setKey("invalid_key");
		invalidGroup.setIcon("invalid_icon");
		final List<SpeedtestDetailGroupEntry> entryList = new ArrayList<>();
		invalidGroup.setValues(entryList);
		
		SpeedtestDetailGroupEntry entry = new SpeedtestDetailGroupEntry();
		entryList.add(entry);
		entry.setKey("speed.throughputAvgDownloadBps");	//existing field, but null
		entry.setTranslationKey("key_throughputAvgDownloadBps");
		
		entry = new SpeedtestDetailGroupEntry();
		entryList.add(entry);
		entry.setKey("agentInfo.invalidField");	//non-existing field
		entry.setTranslationKey("key_throughputAvgDownloadBps");
		
		final DetailMeasurementResponse response = detailMeasurementService.groupResult(measurement, groupStructure, locale, 1);
		assertEquals("unexpected group count", 2, response.getGroups().size());
		assertEquals("unexpected group in result", "a_icon", response.getGroups().get(0).getIconCharacter());
		assertEquals("unexpected group in result", "b_icon", response.getGroups().get(1).getIconCharacter());
	}
}
