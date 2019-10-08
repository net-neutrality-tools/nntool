package at.alladin.nettest.service.loadbalancer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import at.alladin.nettest.service.loadbalancer.dto.LoadApiRequest;
import at.alladin.nettest.service.loadbalancer.dto.LoadApiResponse;
import at.alladin.nettest.shared.berec.loadbalancer.api.v1.dto.MeasurementServerDto;
import at.alladin.nettest.shared.server.helper.ResponseHelper;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;

/**
 * 
 * @author lb@alladin.at
 *
 */
@Service
public class MeasurementServerLoadService {

	private final Logger logger = LoggerFactory.getLogger(MeasurementServerLoadService.class);

	@Autowired
	StorageService storageService;

	public void getLoadForMeasurementServer(final DeferredResult<ResponseEntity<?>> deferred, final String identifier) {
		final MeasurementServerDto server = storageService.getSpeedMeasurementServerByPublicIdentifier(identifier);
		if (deferred != null && server != null) {
			ForkJoinPool.commonPool().submit(() -> {
				try {
					final LoadCallable runnable = new LoadCallable(server);
					final LoadApiResponse response = runnable.call();
					deferred.setResult(ResponseHelper.ok(response));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					deferred.setErrorResult(e);
				}
			});
		}
	}

	public static class LoadCallable implements Callable<LoadApiResponse> {

		final MeasurementServerDto peer;
		
		public LoadCallable(final MeasurementServerDto peer) {
			this.peer = peer;
		}
		
		public MeasurementServerDto getPeer() {
			return peer;
		}

		@Override
		public LoadApiResponse call() throws Exception {
			final RestTemplate restTemplate = new RestTemplate();		
			
			final List<MediaType> acceptableMediaTypes = new ArrayList<>();
			acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
			acceptableMediaTypes.add(MediaType.TEXT_PLAIN);
			acceptableMediaTypes.add(new MediaType("text", "javascript"));
			final HttpHeaders headers = new HttpHeaders();
			headers.setAccept(acceptableMediaTypes);
			
			final LoadApiRequest requestBody = new LoadApiRequest();
			requestBody.setSecret(peer.getLoadApiSecretKey());
						
			final HttpEntity<?> httpEntity = new HttpEntity<>(requestBody, headers);
			
			ResponseEntity<LoadApiResponse> response = restTemplate.exchange(peer.getLoadApiUrl(), 
					HttpMethod.POST, httpEntity, LoadApiResponse.class);
			
			if (response.getStatusCode() == HttpStatus.OK) {
				return response.getBody();
			} 

			return null;
		}
		
	}

}
