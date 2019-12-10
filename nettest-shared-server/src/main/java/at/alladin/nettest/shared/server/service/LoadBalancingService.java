package at.alladin.nettest.shared.server.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.loadbalancer.api.v1.dto.LoadBalancingSettingsDto;
import at.alladin.nettest.shared.berec.loadbalancer.api.v1.dto.MeasurementServerDto;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;

/**
 * 
 * @author lb@alladin.at
 *
 */
@Service
public class LoadBalancingService {

	private final static String QUERY_VAR_PREFERRED_ID = "preferredId";
	
	@Autowired
	StorageService storageService;
	
	LoadBalancingSettingsDto settings;
	
	/**
	 * 
	 * @param settingsUuid
	 * @param preferredId
	 * @return
	 */
	public MeasurementServerDto getNextAvailableMeasurementServer(final String settingsUuid, final String preferredId) {
		LoadBalancingSettingsDto settings = fetchSettings(settingsUuid);
		if (settings == null) {
				return null;
		}
		
		if (settings.getNextFreeUrl() == null) {
			//TODO: if no load balancer is configured, return default server????
			//return storageService.getTaskDto(type, capability, settingsUuid)
			return null;
		}
			
		final RestTemplate restTemplate = new RestTemplate();
		
		final List<MediaType> acceptableMediaTypes = new ArrayList<>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		acceptableMediaTypes.add(MediaType.TEXT_PLAIN);
		acceptableMediaTypes.add(new MediaType("text", "javascript"));
		final HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		
		final HttpEntity<?> httpEntity = new HttpEntity<>(headers);
		
		ResponseEntity<ApiResponse<MeasurementServerDto>> responseEntity = restTemplate.exchange(settings.getNextFreeUrl(), 
				HttpMethod.GET, httpEntity, new ParameterizedTypeReference<ApiResponse<MeasurementServerDto>>() { });
		
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			final ApiResponse<MeasurementServerDto> response = responseEntity.getBody();
			return response != null ? response.getData() : null;
		} 

		return null;

	}
	
	private LoadBalancingSettingsDto fetchSettings(final String settingsUuid) {
		return storageService.getLoadBalancingSettings(settingsUuid);
	}
}
