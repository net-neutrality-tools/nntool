package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.EvaluatedQoSResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullQoSMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurementObjective;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurementType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSResult;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.QoSMeasurementObjectiveRepository;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.FullMeasurementResponseMapper;
import at.alladin.nntool.shared.db.QoSTestResult.TestType;
import at.alladin.nntool.shared.qos.AbstractResult;
import at.alladin.nntool.shared.qos.ResultComparer;
import at.alladin.nntool.shared.qos.ResultDesc;
import at.alladin.nntool.shared.qos.ResultOptions;

@Service
public class QoSEvaluationService {
	
	@Autowired
	public QoSMeasurementObjectiveRepository qosMeasurementObjectiveRepository;
	
	@Autowired
	private FullMeasurementResponseMapper fullMeasurementResponseMapper;

	@Autowired
	private GsonBuilder gsonBuilder;
	
	private Map<Long, QoSMeasurementObjective> objectiveIdToMeasurementObjectiveMap;
	
	private void initMeasurementObjectiveMap() {
		//if this were a post-construct, all servers would need to request QoSMeasurementObjective in their application.yml (as they all know the storage service)
		//initialize the qos objective map (recalculate it everytime, if no server restart on qos definition change is desired)
		final List<QoSMeasurementObjective> objectiveList = qosMeasurementObjectiveRepository.findAllByEnabled(true);
		objectiveIdToMeasurementObjectiveMap = new HashMap<>();
		objectiveList.forEach( (o) -> objectiveIdToMeasurementObjectiveMap.put(Long.parseLong(o.getObjectiveId()), o));
	}
	
	public FullQoSMeasurement evaluateQoSMeasurement (final QoSMeasurement measurement) {
		if (objectiveIdToMeasurementObjectiveMap == null) {
			initMeasurementObjectiveMap();
		}
		
		final FullQoSMeasurement ret = fullMeasurementResponseMapper.map(measurement);
		ret.setResults(new ArrayList<>());
		
		//start copying
		final Map<QoSMeasurementType, TreeSet<ResultDesc>> resultKeys = new HashMap<>();
		
		for (QoSResult qosResult : measurement.getResults()) {
			final QoSMeasurementObjective objective = objectiveIdToMeasurementObjectiveMap.get(qosResult.getObjectiveId());
			if (objective == null) {
				continue;
			}
			
			ret.getResults().add(compareTestResults(qosResult, objective, resultKeys));
			
		}
		
		return ret;
	}
	
	private EvaluatedQoSResult compareTestResults (final QoSResult qosResult, final QoSMeasurementObjective objective, Map<QoSMeasurementType, TreeSet<ResultDesc>> resultKeys) {
		final Class<? extends AbstractResult> resultClass = getResultClass(qosResult.getType());
		if (resultClass == null) {
			return null;
		}
		
		//pre-init counts
		qosResult.setSuccessCount(0);
		qosResult.setFailureCount(0);
		//exclude testTypeKeys()
		//remove testtype and qos uid from res (TODO: do we still need this?)
		qosResult.setType(null);
		qosResult.setObjectiveId(null);
		
		final Gson gson = gsonBuilder.create();
		
		//fix double gson call
		final AbstractResult result = gson.fromJson(gson.toJson(qosResult.getResults()), resultClass);	// == testResult
		result.setResultMap(qosResult.getResults()); //and add the map (needed for evaluations (e.g. %EVAL xxxxx%))
		
		//create a parsed abstract result set sorted by priority
		final Set<AbstractResult> expResultSet = new TreeSet<>(new Comparator<AbstractResult>() {
			@Override
			public int compare(final AbstractResult o1, final AbstractResult o2) {
				return o1.getPriority().compareTo(o2.getPriority());
			}
		});
		
		int maxPriority = Integer.MAX_VALUE;
		
		if (objective != null && objective.getEvaluations() != null) {
			for (Map<String, String> evaluation : objective.getEvaluations()) {
				//TODO: fix double gson call!
				AbstractResult res = gson.fromJson(gson.toJson(evaluation), resultClass);
				if (res.getPriority() != null && res.getPriority() == Integer.MAX_VALUE) {
					res.setPriority(maxPriority--);
				}
				expResultSet.add(res);
			
			}
			
			for (AbstractResult objectiveResult : expResultSet) {
				final ResultOptions resultOptions = new ResultOptions(Locale.getDefault());
				final ResultDesc desc;
				try {
					desc = ResultComparer.compare(result, objectiveResult, obtainFields(resultClass), resultOptions);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
					continue;
				}
				try {
					desc.setTestType(TestType.valueOf(objective.getType().toString()));
					desc.addTestResultUid(Long.parseLong(objective.getObjectiveId()));
				} catch (IllegalArgumentException ex) {
					ex.printStackTrace();
				}
				
				final ResultHolder resultHolder = calculateResultCounter(qosResult, objectiveResult, desc);
				
				//check if there is a result message
				if (resultHolder != null) {
	    			TreeSet<ResultDesc> resultDescSet;
	    			if (resultKeys.containsKey(objective.getType())) {
	    				resultDescSet = resultKeys.get(objective.getType());
	    			}
	    			else {
	    				resultDescSet = new TreeSet<>();
	    				resultKeys.put(objective.getType(), resultDescSet);
	    			}
	
					resultDescSet.add(desc);
					
					qosResult.getResults().put(desc.getKey(), resultHolder.resultKeyType);
					
	    			if (AbstractResult.BEHAVIOUR_ABORT.equals(resultHolder.event)) {
	    				break;
	    			}
				}   
			}
			
			final EvaluatedQoSResult ret = new EvaluatedQoSResult();
			try {
				ret.setType(QoSMeasurementTypeDto.valueOf(objective.getType().toString()));
			} catch (IllegalArgumentException ex) {
				ex.printStackTrace();
			}
			ret.setFailureCount(qosResult.getFailureCount());
			ret.setSuccessCount(qosResult.getSuccessCount());
			ret.setEvaluationCount(ret.getFailureCount() + ret.getSuccessCount());
			ret.setImplausible(qosResult.isImplausible());
			ret.setDescription(objective.getTranslationKeys().getDescription());
			ret.setSummary(objective.getTranslationKeys().getSummary());
			final Map<String, String> evaluationKeyMap = new HashMap<>();
			qosResult.getResults().forEach((string, obj) -> {
				if (obj != null) {
					//do some ugly parsing
					if (obj instanceof Double) {
						final Double doubleObj = (Double) obj;
						if (doubleObj.doubleValue() == doubleObj.intValue()) {
							evaluationKeyMap.put(string, Integer.toString(doubleObj.intValue()));
						} else {
							evaluationKeyMap.put(string, Double.toString(doubleObj));
						}
					} else {
						evaluationKeyMap.put(string, obj.toString());
					}
				} else {
					//TODO: do we add null values?
					evaluationKeyMap.put(string, "");
				}
			}); 
			ret.setEvaluationKeyMap(evaluationKeyMap);
			return ret;
		}
		
		return null;
	}
	
	private Class<? extends AbstractResult> getResultClass (final QoSMeasurementType type) {
		try {
			return QosMeasurementType.fromValue(type.toString().toLowerCase()).getResultClass();
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	private Map<String, Field> obtainFields(Class<?> clazz) {
		final Map<String, Field> ret = new HashMap<>();
		if (clazz.getSuperclass() != null) {
			ret.putAll(obtainFields(clazz.getSuperclass()));
		}
		
		for (Field f : clazz.getDeclaredFields()) {
			if (f.isAnnotationPresent(SerializedName.class)) {
				String fieldName = ((SerializedName) f.getAnnotation(SerializedName.class)).value();
				//check for duplicates:
				if (!ret.containsKey(fieldName)) {
					ret.put(fieldName, f);
				}
				else {
					//TODO: make custom exception (do we need this w/out the hstore thingy?
					throw new RuntimeException("Field already in use: " + fieldName);
				}
			}
			
		}
		return ret;
	}
	
	private ResultHolder calculateResultCounter(final QoSResult qosResult, final AbstractResult expResult, final ResultDesc resultDesc) {
		String resultKeyType = null;
		String event = AbstractResult.BEHAVIOUR_NOTHING;
		
		//increase the failure or success counter of this result object
		if (resultDesc.getStatusCode().equals(ResultDesc.STATUS_CODE_SUCCESS)) {
			if (expResult.getOnSuccess() != null) {
				qosResult.setSuccessCount(qosResult.getSuccessCount() + 1);
				if (AbstractResult.RESULT_TYPE_DEFAULT.equals(expResult.getSuccessType())) {
					resultKeyType = ResultDesc.STATUS_CODE_SUCCESS;
				}
				else {
					resultKeyType = ResultDesc.STATUS_CODE_INFO;
				}
				
				event = expResult.getOnSuccessBehaivour();
			}
		}
		else {
			if (expResult.getOnFailure() != null) {
				qosResult.setFailureCount(qosResult.getFailureCount()+1);
				if (AbstractResult.RESULT_TYPE_DEFAULT.equals(expResult.getFailureType())) {
					resultKeyType = ResultDesc.STATUS_CODE_FAILURE;
				}
				else {
					resultKeyType = ResultDesc.STATUS_CODE_INFO;
				}
				
				event = expResult.getOnFailureBehaivour();
			}
		}
		
		return resultKeyType != null ? new ResultHolder(resultKeyType, event) : null;
	}
	
	public static class ResultHolder {
		final String resultKeyType;
		final String event;
		
		public ResultHolder(final String resultKeyType, final String event) {
			this.resultKeyType = resultKeyType;
			this.event = event;
		}

		public String getResultKeyType() {
			return resultKeyType;
		}

		public String getEvent() {
			return event;
		}
	}
}
