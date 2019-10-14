package at.alladin.nettest.service.map.web.api.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nntool.shared.map.TileInfoResponse;
import at.alladin.nntool.shared.map.TileRequest;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/tiles/heatmaps/default")
public class DefaultHeatmapResource extends AbstractTileResource {
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.service.map.web.api.v2.AbstractTileResource#createTileByZoomXY(java.lang.Integer, java.lang.Integer, java.lang.Integer, at.alladin.nettest.shared.berec.api.v1.dto.ApiRequest)
	 */
	@Override
	public ResponseEntity<byte[]> createTileByZoomXY(Integer zoom, Integer x, Integer y,
			ApiRequest<TileRequest> tileApiRequest) {
		
		return ResponseEntity.ok(new byte[] {}); // TODO
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.service.map.web.api.v2.AbstractTileResource#getInfos()
	 */
	@Override
	public ResponseEntity<ApiResponse<TileInfoResponse>> getInfos() {
		return ResponseEntity.ok(null);
	}
}
