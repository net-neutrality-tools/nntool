package at.alladin.nettest.service.map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import at.alladin.nettest.service.map.domain.repository.SqlSettingsRepository;
import at.alladin.nettest.service.map.service.ClassificationService;
import at.alladin.nettest.service.map.service.ColorMapperService;
import at.alladin.nettest.service.map.service.HeatmapTileService;
import at.alladin.nettest.service.map.service.InfoService;
import at.alladin.nettest.service.map.service.MapOptionsService;
import at.alladin.nettest.service.map.service.MarkerService;
import at.alladin.nettest.service.map.service.PointTileService;
import at.alladin.nettest.service.map.service.ThresholdsPerTechnologyHelperService;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MapServiceApplicationTests {
	
	@MockBean
	private ClassificationService classificationService;
	
	@MockBean
	private ColorMapperService colorMapperService;
	
	@MockBean
	private HeatmapTileService heatmapTileService;
	
	@MockBean
	private InfoService infoService;
	
	@MockBean
	private MapOptionsService mapOptionsService;
	
	@MockBean
	private MarkerService markerService;
	
	@MockBean
	private PointTileService pointTileService;
	
	@MockBean
	private SqlSettingsRepository sqlSettingsRepository;
	
	@MockBean
	private ThresholdsPerTechnologyHelperService thresholdsHelperService;
	
	@MockBean
	private StorageService storageService;
	
	/**
	 * 
	 */
	@Test
	public void contextLoads() {
		
	}
}
