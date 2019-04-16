package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.EvaluatedQoSResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullQoSMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurementObjective;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurementType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSResult;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.QoSMeasurementObjectiveRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurementObjective.QoSTranslationKeys;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;

public class QoSEvaluationServiceTest {

	@Tested
	private QoSEvaluationService evaluationService;
	
	@Injectable 
	@Mocked 
	QoSMeasurementObjectiveRepository objectiveRepo;
	
	private QoSMeasurement qosMeasurement;
	
	private List<QoSMeasurementObjective> objectiveList;
	
	private final static String DESCRIPTION = "test_description";
	
	private final static String SUMMARY = "test_summary";
	
	@Before
	public void init () {
		
		qosMeasurement = new QoSMeasurement();
		final List<QoSResult> resultList = new ArrayList<>();
		qosMeasurement.setResults(resultList);
		
		QoSResult res = new QoSResult();
		resultList.add(res);
		res.setObjectiveId(200L);
		res.setType(QoSMeasurementType.TCP);
		res.setImplausible(false);
		Map<String, Object> resMap = new HashMap<>();
		res.setResults(resMap);
		
		resMap.put("tcp_result_out_response", "PING");
		resMap.put("tcp_result_out", "OK");
		
		
		objectiveList = new ArrayList<>();		
	}
	
	@Test
	public void basicTcpEvaluationTest () {
		
		QoSMeasurementObjective objective = new QoSMeasurementObjective();
		objectiveList.add(objective);
		
		objective.setEnabled(true);
		objective.setType(QoSMeasurementType.TCP);
		objective.setObjectiveId("200");
		final QoSTranslationKeys keys = new QoSTranslationKeys();
		keys.setDescription(DESCRIPTION);
		keys.setSummary(SUMMARY);
		objective.setTranslationKeys(keys);
		final List<Map<String, String>> evaluationList = new ArrayList<>();
		objective.setEvaluations(evaluationList);
		
		Map<String, String> evaluationMap = new HashMap<>();
		evaluationMap.put("operator", "eq");
		evaluationMap.put("on_failure", "tcp.failure");
		evaluationMap.put("tcp_result_out", "OK");
		evaluationMap.put("on_success", "tcp.success");
		evaluationList.add(evaluationMap);
		
		evaluationMap = new HashMap<>();
		evaluationMap.put("operator", "eq");
		evaluationMap.put("on_failure", "tcp.failure");
		evaluationMap.put("tcp_result_out_response", "PING");
		evaluationMap.put("on_success", "tcp.success");
		evaluationList.add(evaluationMap);
		
		new Expectations() {{
			objectiveRepo.findAllByEnabled(anyBoolean);
			result = objectiveList;
		}};
		
		final FullQoSMeasurement res = evaluationService.evaluateQoSMeasurement(qosMeasurement);
		final List<EvaluatedQoSResult> evalList = res.getResults();
		assertEquals("unexpected result size", 1, evalList.size());
		final EvaluatedQoSResult evalRes = evalList.get(0);
		assertEquals("Unexpected qos type", QoSMeasurementTypeDto.TCP, evalRes.getType());
		assertEquals("unexpected description", DESCRIPTION, evalRes.getDescription());
		assertEquals("unexpected summary", SUMMARY, evalRes.getSummary());
		assertEquals("Unexpected success count", 2, (int) evalRes.getSuccessCount());
		assertEquals("Unexpected failure count", 0, (int) evalRes.getFailureCount());
		assertEquals("unexpected evaluation count", 2, (int) evalRes.getEvaluationCount());
		assertFalse("unexpected implausible flag", evalRes.isImplausible());
		assertFalse("unexpected tcp failure found in results", evalRes.getEvaluationKeyMap().containsKey("tcp.failure"));
		assertTrue("no tcp success found in results", evalRes.getEvaluationKeyMap().containsKey("tcp.success"));
		assertEquals("tcp success carrying wrong message", "ok", evalRes.getEvaluationKeyMap().get("tcp.success"));
	}
	
	@Test
	public void basicTcpEvaluationTestFailingEvaluations () {
		
		QoSMeasurementObjective objective = new QoSMeasurementObjective();
		objectiveList.add(objective);
		
		objective.setEnabled(true);
		objective.setType(QoSMeasurementType.TCP);
		objective.setObjectiveId("200");
		final QoSTranslationKeys keys = new QoSTranslationKeys();
		keys.setDescription(DESCRIPTION);
		keys.setSummary(SUMMARY);
		objective.setTranslationKeys(keys);
		final List<Map<String, String>> evaluationList = new ArrayList<>();
		objective.setEvaluations(evaluationList);
		
		Map<String, String> evaluationMap = new HashMap<>();
		evaluationMap.put("operator", "eq");
		evaluationMap.put("on_failure", "tcp.failure");
		evaluationMap.put("tcp_result_out", "NOT_OK");
		evaluationMap.put("on_success", "tcp.success");
		evaluationList.add(evaluationMap);
		
		evaluationMap = new HashMap<>();
		evaluationMap.put("operator", "eq");
		evaluationMap.put("on_failure", "tcp.failure");
		evaluationMap.put("tcp_result_out_response", "NOT_PING");
		evaluationMap.put("on_success", "tcp.success");
		evaluationList.add(evaluationMap);
		
		new Expectations() {{
			objectiveRepo.findAllByEnabled(anyBoolean);
			result = objectiveList;
		}};
		
		final FullQoSMeasurement res = evaluationService.evaluateQoSMeasurement(qosMeasurement);
		final List<EvaluatedQoSResult> evalList = res.getResults();
		assertEquals("unexpected result size", 1, evalList.size());
		final EvaluatedQoSResult evalRes = evalList.get(0);
		assertEquals("Unexpected qos type", QoSMeasurementTypeDto.TCP, evalRes.getType());
		assertEquals("unexpected description", DESCRIPTION, evalRes.getDescription());
		assertEquals("unexpected summary", SUMMARY, evalRes.getSummary());
		assertEquals("Unexpected success count", 0, (int) evalRes.getSuccessCount());
		assertEquals("Unexpected failure count", 2, (int) evalRes.getFailureCount());
		assertEquals("unexpected evaluation count", 2, (int) evalRes.getEvaluationCount());
		assertFalse("unexpected implausible flag", evalRes.isImplausible());
		assertFalse("tcp success found in results", evalRes.getEvaluationKeyMap().containsKey("tcp.success"));
		assertTrue("no tcp failure found in results", evalRes.getEvaluationKeyMap().containsKey("tcp.failure"));
		assertEquals("tcp failure carrying wrong message", "fail", evalRes.getEvaluationKeyMap().get("tcp.failure"));
	}
}
