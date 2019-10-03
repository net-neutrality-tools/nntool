package at.alladin.nettest.service.loadbalancer.web.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import at.alladin.nettest.service.loadbalancer.service.MeasurementServerLoadService;

/**
 * This controller is used to provide version information.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/measurement-servers")
public class MeasurementServerStatusResource {

	@Autowired
	MeasurementServerLoadService measurementServerLoadService;
	
	@GetMapping("{serverUuid}/status")
	public DeferredResult<ResponseEntity<?>> getStatus(@PathVariable String serverUuid) {
		final DeferredResult<ResponseEntity<?>> deferred = new DeferredResult<>();
		measurementServerLoadService.getLoadForMeasurementServer(deferred, serverUuid);
		return deferred;
	}
}
