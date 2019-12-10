package at.alladin.nettest.service.loadbalancer.web.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.service.loadbalancer.dto.LoadApiAllServersReport;
import at.alladin.nettest.service.loadbalancer.dto.LoadApiReport;
import at.alladin.nettest.service.loadbalancer.service.MeasurementServerLoadMonitorService;
import at.alladin.nettest.service.loadbalancer.service.MeasurementServerLoadService;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.loadbalancer.api.v1.dto.MeasurementServerDto;
import at.alladin.nettest.shared.server.helper.ResponseHelper;

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
	
	@Autowired
	MeasurementServerLoadMonitorService loadMonitorService;
	
	//TODO: do we need a resource for real time load?
	/*
	@GetMapping("{serverId}/load/live")
	public DeferredResult<ResponseEntity<?>> getLoadLive(@PathVariable String serverId) {
		final DeferredResult<ResponseEntity<?>> deferred = new DeferredResult<>();
		measurementServerLoadService.getLoadForMeasurementServer(deferred, serverId);
		return deferred;
	}
	*/
	
	@GetMapping("/load/{serverId}")
	public ResponseEntity<ApiResponse<LoadApiReport>> getLoad(@PathVariable String serverId) {
		return ResponseHelper.ok(loadMonitorService.getReportForMeasurementServer(serverId));
	}

	@GetMapping("/free")
	public ResponseEntity<ApiResponse<MeasurementServerDto>> getNextFreeMeasurementServer(
			@RequestParam(name="preferredId", required=false) String preferredId) {
		return ResponseHelper.ok(loadMonitorService.getNextAvailableMeasurementPeer(preferredId));
	}

	@GetMapping("/load")
	public ResponseEntity<ApiResponse<LoadApiAllServersReport>> getLoadForAllServers() {
		final LoadApiAllServersReport report = new LoadApiAllServersReport();
		report.setReportList(loadMonitorService.getReportList());
		return ResponseHelper.ok(report);
	}
}
