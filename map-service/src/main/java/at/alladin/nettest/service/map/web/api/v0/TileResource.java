package at.alladin.nettest.service.map.web.api.v0;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

import at.alladin.nettest.service.map.service.HeatmapTileService;
import at.alladin.nettest.service.map.service.MapOptionsService;
import at.alladin.nettest.service.map.service.PointTileService;
import at.alladin.nntool.shared.map.HeatmapTileParameters;
import at.alladin.nntool.shared.map.MapTileParameters;
import at.alladin.nntool.shared.map.MapTileRequest;
import at.alladin.nntool.shared.map.PointTileParameters;

@RestController
@RequestMapping("/api/v0/tiles")
public class TileResource {

    @Autowired
    private MapOptionsService mapOptionsService;

    @Autowired
    private PointTileService pointTileService;
    
    @Autowired
    private HeatmapTileService heatmapTileService;
    
    @GetMapping(value="/points/{zoom}/{x}/{y}.png", produces=MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> getPointTilePath(@PathVariable Integer x, @PathVariable Integer y, @PathVariable Integer zoom, 
    		@RequestParam(required=false) String highlight, 
    		@RequestParam(name = "highlight_uuid", required = false) String highlightUuid, 
    		@RequestParam(name = "highlight_client_uuid", required=false) String clientUuid, 
    		@RequestParam(name = "statistical_method", required=false) Float quantile,
    		@RequestParam(required=false) Integer period,
    		@RequestParam(name = "map_options", required=false) String mapOptions, 
    		@RequestParam(required=false) String technology, 
    		@RequestParam(required=false) String provider,
    		@RequestParam(required=false) String operator) {
    	final MapTileRequest request = new MapTileRequest();
    	request.setParameters(new PointTileParameters());
        parseAndValidateRequest(x, y, zoom, highlight, highlightUuid, clientUuid, quantile, period, mapOptions, technology, provider, operator, request);
        return ResponseEntity.ok(pointTileService.generateTileData(request.getParameters()));
    }

    @PostMapping(value="/points", produces=MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> postPointTile(@RequestBody MapTileRequest request) {
        parseAndValidateRequest(request);
        return ResponseEntity.ok(pointTileService.generateTileData(request.getParameters()));
    }
    
    @GetMapping(value="/points", produces=MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getPointTile(
    		@RequestParam(required=true) String path,
    		@RequestParam(required=false) String highlight, 
    		@RequestParam(name = "highlight_uuid", required = false) String highlightUuid, 
    		@RequestParam(name = "highlight_client_uuid", required=false) String clientUuid, 
    		@RequestParam(name = "statistical_method", required=false) Float quantile,
    		@RequestParam(required=false) Integer period,
    		@RequestParam(name = "map_options", required=false) String mapOptions, 
    		@RequestParam(required=false) String technology, 
    		@RequestParam(required=false) String provider,
    		@RequestParam(required=false) String operator) {
    	final MapTileRequest request = new MapTileRequest();
    	request.setParameters(new PointTileParameters());
    	final String[] pathArray = path.split("/");
    	if (pathArray.length < 3) {
    		throw new IllegalArgumentException("Path does not contain the specfied values");
    	}
        parseAndValidateRequest(Integer.parseInt(pathArray[1]), Integer.parseInt(pathArray[2]), Integer.parseInt(pathArray[0]), highlight, highlightUuid, clientUuid, quantile, period, mapOptions, technology, provider, operator, request);
        return ResponseEntity.ok(pointTileService.generateTileData(request.getParameters()));//request.getParameters()));
    }

    @GetMapping(value="/heatmap/{zoom}/{x}/{y}.png", produces=MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> getHeatmapTilePath(@PathVariable Integer x, @PathVariable Integer y, @PathVariable Integer zoom, 
    		@RequestParam(required=false) String highlight, 
    		@RequestParam(name = "highlight_uuid", required = false) String highlightUuid, 
    		@RequestParam(name = "highlight_client_uuid", required=false) String clientUuid, 
    		@RequestParam(name = "statistical_method", required=false) Float quantile,
    		@RequestParam(required=false) Integer period,
    		@RequestParam(name = "map_options", required=false) String mapOptions, 
    		@RequestParam(required=false) String technology, 
    		@RequestParam(required=false) String provider,
    		@RequestParam(required=false) String operator) {
    	
    	final MapTileRequest request = new MapTileRequest();
    	request.setParameters(new HeatmapTileParameters()); //TODO: set default transparency
        parseAndValidateRequest(x, y, zoom, highlight, highlightUuid, clientUuid, quantile, period, mapOptions, technology, provider, operator, request);
        return ResponseEntity.ok(heatmapTileService.generateTileData(request.getParameters()));
    }

    @PostMapping(value="/heatmap", produces=MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getHeatmapTile(@RequestBody MapTileRequest request) {
    	parseAndValidateRequest(request);
        return ResponseEntity.ok(heatmapTileService.generateTileData(request.getParameters()));
    }
    
    @GetMapping(value="/heatmap", produces=MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> getHeatmapTilePath(
    		@RequestParam(required=true) String path,
    		@RequestParam(required=false) String highlight, 
    		@RequestParam(name = "highlight_uuid", required = false) String highlightUuid, 
    		@RequestParam(name = "highlight_client_uuid", required=false) String clientUuid, 
    		@RequestParam(name = "statistical_method", required=false) Float quantile,
    		@RequestParam(required=false) Integer period,
    		@RequestParam(name = "map_options", required=false) String mapOptions, 
    		@RequestParam(required=false) String technology, 
    		@RequestParam(required=false) String provider,
    		@RequestParam(required=false) String operator) {
    	
    	final String[] pathArray = path.split("/");
    	if (pathArray.length < 3) {
    		throw new IllegalArgumentException("Path does not contain the specfied values");
    	}
    	final MapTileRequest request = new MapTileRequest();
    	request.setParameters(new HeatmapTileParameters()); //TODO: set default transparency
        parseAndValidateRequest(Integer.parseInt(pathArray[1]), Integer.parseInt(pathArray[2]), Integer.parseInt(pathArray[0]), highlight, highlightUuid, clientUuid, quantile, period, mapOptions, technology, provider, operator, request);
        return ResponseEntity.ok(heatmapTileService.generateTileData(request.getParameters()));
    }

    private void parseAndValidateRequest (final Integer x, final Integer y, final Integer zoom, final String highlight,
    		final String highlightUuid, final String clientUuid, final Float quantile, 
    		final Integer period, final String mapOptions, final String technology, 
    		final String provider, final String operator, final MapTileRequest request) {
    	final MapTileParameters params = request.getParameters();
    	params.setX(x);
    	params.setY(y);
    	params.setZoom(zoom);
    	params.setMapOption(mapOptions);
    	params.setQuantile(quantile);
    	params.setPeriod(period);
    	params.setHighlight(highlight);
    	params.setHighlightUuid(highlightUuid);
    	params.setClientUuid(clientUuid);
    	params.setProvider(provider);
    	params.setOperator(operator);
    	params.setTechnology(technology);
    	parseAndValidateRequest(request);
    }
    
    /**
     * Method to provide default values and validate the request
     * (throws IllegalArgumentException on invalid params)
     * @param request
     */
    private void parseAndValidateRequest (final MapTileRequest request) {
    	//set defaults
    	//TODO: provide defaults as config
    	final MapTileParameters params = request.getParameters();
    	
    	if (params.getFilterMap() == null) {
    		params.setFilterMap(new HashMap<>());
    	}
    	
    	if (params.getMapOption() == null || params.getMapOption().equals("")) {
    		params.setMapOption("all/download");
    	}
    	
    	if (params.getQuantile() == null 
    			|| params.getQuantile() < 0 || params.getQuantile() > 1) {
    		params.setQuantile(0.5f);
    	}

    	//default transparency depends on the tile type, and is therefore set in the services
    	if (params.getTransparency() != null) {
    		params.setTransparency(Math.max(0, Math.min(1, params.getTransparency())));
    	}
    	
    	if (params.getSize() == null) {
    		params.setSize(0);
    	}
    	
    	//set the filters and their corresponding values (TODO: possibly move this to the services or elsewhere)
    	if (params.getPeriod() != null) {
    		params.getFilterMap().put("period", params.getPeriod().toString());
    	}
    	
    	if (params.getTechnology() != null) {
    		params.getFilterMap().put("technology", params.getTechnology().toString());
    	}
    	
    	if (params.getOperator() != null && params.getOperator().length() > 0) {
    		params.getFilterMap().put("operator", params.getOperator());
    	}
    	
    	if (params.getProvider() != null && params.getProvider().length() > 0) {
    		params.getFilterMap().put("provider", params.getProvider());
    	}
    	
    	
    	validateRequest(request.getParameters().getX(), request.getParameters().getY(), request.getParameters().getZoom());
    }
    

    private void validateRequest (final Integer x, final Integer y, final Integer zoom) {
        if (zoom == null || zoom < 0 || zoom > mapOptionsService.getMaxZoomLevel()) {
            throw new IllegalArgumentException();
        }
        if (x == null || y == null || x < 0 || y < 0) {
            throw new IllegalArgumentException();
        }
        int pow = 1 << zoom;
        if (x >= pow || y >= pow) {
            throw new IllegalArgumentException();
        }
    }
    
}
