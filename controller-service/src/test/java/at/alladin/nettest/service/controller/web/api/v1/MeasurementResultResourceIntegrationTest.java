package at.alladin.nettest.service.controller.web.api.v1;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RunWith(SpringRunner.class)
@AutoConfigureJsonTesters
public class MeasurementResultResourceIntegrationTest {

//    private MockMvc mockMvc;
//	
//    @Autowired
//    private ObjectMapper objectMapper;
//    
//	@MockBean
//	private StorageService storageService;
//	
//	@Before
//	public void setup() {
//		final MeasurementResultResource controller = new MeasurementResultResource();
//		ReflectionTestUtils.setField(controller, "storageService", storageService);
//		
//		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
//	}
//	
//	@Test
//	public void testPostLmapReportModelReturnsValidMeasurementResultResponse() throws Exception {
//		final MeasurementResultResponse resultResponse = new MeasurementResultResponse();
//		resultResponse.setUuid(UUID.randomUUID().toString());
//		resultResponse.setOpenDataUuid(UUID.randomUUID().toString());
//		
//		when(storageService.save(any(LmapReportDto.class))).thenReturn(resultResponse);
//		
//		mockMvc
//			.perform(
//				post("/api/v1/measurements")
//					.contentType(MediaType.APPLICATION_JSON_UTF8)
//					.content(objectMapper.writeValueAsString(new LmapReportDto()))
//			)
//			.andDo(print())
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("data.uuid", is(resultResponse.getUuid())))
//			.andExpect(jsonPath("data.open_data_uuid", is(resultResponse.getOpenDataUuid())));
//		
//		verify(storageService, times(1)).save(any(LmapReportDto.class));
//	}
	
	// TODO: test if storageService throws exception
}
