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

import com.google.gson.GsonBuilder;

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
	
	@Injectable
	GsonBuilder builder;
	
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
		
		builder = new GsonBuilder();
	}
	
	@Test
	public void basicTcpEvaluationTest () {
		
		final QoSMeasurementObjective objective = new QoSMeasurementObjective();
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
		
		final QoSMeasurementObjective objective = new QoSMeasurementObjective();
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
	
	@Test
	public void voipTestJavascriptEvaluationWithFailedAndSucceededEvaluationsTest() {
		
		QoSResult res = new QoSResult();
		final List<QoSResult> resultList = qosMeasurement.getResults();
		resultList.clear();
		resultList.add(res);
		res.setObjectiveId(76L);
		res.setType(QoSMeasurementType.VOIP);
		res.setImplausible(false);
		Map<String, Object> resMap = new HashMap<>();
		res.setResults(resMap);
		
		resMap.put("voip_result_out_short_seq", "100");
		resMap.put("voip_result_in_sequence_error", "OK");
		resMap.put("voip_objective_delay", "20000000");
		resMap.put("voip_objective_sample_rate", "8000");
		resMap.put("voip_result_out_num_packets", "100");
		resMap.put("voip_result_out_long_seq", "100");
		resMap.put("voip_objective_bits_per_sample", "8");
		resMap.put("duration_ns", "2429313000");
		resMap.put("voip_result_out_max_jitter", "5752866");
		resMap.put("voip_result_out_mean_jitter", "2655557");
		resMap.put("voip_result_status", "OK");
		resMap.put("voip_result_in_num_packets", "100");
		resMap.put("voip_result_out_sequence_error", "0");
		resMap.put("voip_result_in_max_jitter", "3235718");
		resMap.put("voip_result_in_skew", "-182397000");
		resMap.put("voip_result_out_max_delta", "39931068");
		resMap.put("voip_objective_call_duration", "2000000000");
		resMap.put("voip_result_in_short_seq", "100");
		resMap.put("start_time_ns", "18772068000");
		resMap.put("voip_result_in_max_delta", "19748000");
		resMap.put("voip_objective_payload", "8");
		resMap.put("voip_objective_out_port", "5060");
		resMap.put("voip_result_in_mean_jitter", "1733572999");	//the incoming mean jitter is high enough to fail its test
		resMap.put("voip_result_in_long_seq", "100");
		resMap.put("voip_result_out_skew", "-202100058");
        
		System.out.println("res in test method: " + res.toString());
		
		final QoSMeasurementObjective objective = new QoSMeasurementObjective();
		objectiveList.add(objective);
		
		objective.setEnabled(true);
		objective.setType(QoSMeasurementType.VOIP);
		objective.setObjectiveId("76");
		final QoSTranslationKeys keys = new QoSTranslationKeys();
		keys.setDescription(DESCRIPTION);
		keys.setSummary(SUMMARY);
		objective.setTranslationKeys(keys);
		final List<Map<String, String>> evaluationList = new ArrayList<>();
		objective.setEvaluations(evaluationList);
		
		Map<String, String> evaluationMap = new HashMap<>();
		evaluationMap.put("evaluate", "%EVAL if (nn.coalesce(voip_result_out_mean_jitter, 50000000) < 50000000) result=true; else result=false;%");
		evaluationMap.put("on_failure", "voip.jitter.outgoing.failure");
		evaluationMap.put("on_success", "voip.jitter.outgoing.success");
		evaluationList.add(evaluationMap);
		
		evaluationMap = new HashMap<>();
		evaluationMap.put("evaluate", "%EVAL if (nn.coalesce(voip_result_in_mean_jitter, 50000000) < 50000000) result=true; else result=false;%");
		evaluationMap.put("on_failure", "voip.jitter.incoming.failure");
		evaluationMap.put("on_success", "voip.jitter.incoming.success");
		evaluationList.add(evaluationMap);
		
		evaluationMap = new HashMap<>();
		evaluationMap.put("evaluate", "%EVAL if (nn.coalesce(voip_result_out_num_packets, 0) > 0) result=true; else result=false;%");
		evaluationMap.put("on_failure", "voip.outgoing.packet.failure");
		evaluationMap.put("on_success", "voip.outgoing.packet.success");
		evaluationList.add(evaluationMap);
		
		evaluationMap = new HashMap<>();
		evaluationMap.put("evaluate", "%EVAL if (nn.coalesce(voip_result_in_num_packets, 0) > 0) result=true; else result=false;%");
		evaluationMap.put("on_failure", "voip.incoming.packet.failure");
		evaluationMap.put("on_success", "voip.incoming.packet.success");
		evaluationList.add(evaluationMap);
		
		evaluationMap = new HashMap<>();
		evaluationMap.put("evaluate", "%EVAL if(voip_result_status=='TIMEOUT') result={type:  'failure', key: 'voip.timeout'}%");
		evaluationList.add(evaluationMap);
		
		new Expectations() {{
			objectiveRepo.findAllByEnabled(anyBoolean);
			result = objectiveList;
		}};
		
		final FullQoSMeasurement fullMeasurement = evaluationService.evaluateQoSMeasurement(qosMeasurement);
		final List<EvaluatedQoSResult> evalList = fullMeasurement.getResults();
		assertEquals("unexpected result size", 1, evalList.size());
		final EvaluatedQoSResult evalRes = evalList.get(0);
		assertEquals("Unexpected qos type", QoSMeasurementTypeDto.VOIP, evalRes.getType());
		assertEquals("Unexpected evaluation count", 4, (int) evalRes.getEvaluationCount());
		assertEquals("Unexpected success count", 3, (int) evalRes.getSuccessCount());
		assertEquals("Unexpected failure count", 1, (int) evalRes.getFailureCount());
		assertEquals("unexpected description", DESCRIPTION, evalRes.getDescription());
		assertEquals("unexpected summary", SUMMARY, evalRes.getSummary());
		assertEquals("unexpected implausible flag", false, evalRes.isImplausible());
		
		assertTrue("wrong message found in evaluation", evalRes.getEvaluationKeyMap().containsKey("voip.jitter.incoming.failure"));
		assertFalse("wrong message found in evaluation", evalRes.getEvaluationKeyMap().containsKey("voip.jitter.incoming.success"));
		assertEquals("wrong message found in evaluation", "fail", evalRes.getEvaluationKeyMap().get("voip.jitter.incoming.failure"));
		
		assertFalse("wrong message found in evaluation", evalRes.getEvaluationKeyMap().containsKey("voip.jitter.outgoing.failure"));
		assertTrue("wrong message found in evaluation", evalRes.getEvaluationKeyMap().containsKey("voip.jitter.outgoing.success"));
		assertEquals("wrong message found in evaluation", "ok", evalRes.getEvaluationKeyMap().get("voip.jitter.outgoing.success"));
		
		assertTrue("wrong message found in evaluation", evalRes.getEvaluationKeyMap().containsKey("voip.incoming.packet.success"));
		assertFalse("wrong message found in evaluation", evalRes.getEvaluationKeyMap().containsKey("voip.incoming.packet.failure"));
		assertEquals("wrong message found in evaluation", "ok", evalRes.getEvaluationKeyMap().get("voip.incoming.packet.success"));
		
		assertTrue("wrong message found in evaluation", evalRes.getEvaluationKeyMap().containsKey("voip.outgoing.packet.success"));
		assertFalse("wrong message found in evaluation", evalRes.getEvaluationKeyMap().containsKey("voip.outgoing.packet.failure"));
		assertEquals("wrong message found in evaluation", "ok", evalRes.getEvaluationKeyMap().get("voip.outgoing.packet.success"));
	}
	
}
