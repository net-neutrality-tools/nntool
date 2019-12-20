/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

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
