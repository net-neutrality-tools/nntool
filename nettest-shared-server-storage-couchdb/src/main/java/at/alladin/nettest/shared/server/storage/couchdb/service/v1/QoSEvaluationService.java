package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.EvaluatedQoSResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.EvaluatedQoSResult.QoSResultOutcome;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullQoSMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.QoSTypeDescription;
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
import at.alladin.nntool.shared.qos.testscript.TestScriptInterpreter;

@Service
public class QoSEvaluationService {

	private final static String TRANSLATION_DESCRIPTION_SUFFIX = "_description";

	private final static String TRANSLATION_NAME_SUFFIX = "_title";
	
	private final static String TRANSLATION_ICON_FONT_SUFFIX = "_icon";

	@Autowired
	public QoSMeasurementObjectiveRepository qosMeasurementObjectiveRepository;

	@Autowired
	private FullMeasurementResponseMapper fullMeasurementResponseMapper;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private MessageSource messageSource;
	
	private Map<QoSMeasurementTypeDto, Map<String, Field>> qosTypeToFieldMap = new HashMap<>();

	private Map<Long, QoSMeasurementObjective> objectiveIdToMeasurementObjectiveMap;

	@PostConstruct
	private void init() {
		for (final QoSMeasurementTypeDto type : QoSMeasurementTypeDto.values()) {
			final QosMeasurementType qosType = QosMeasurementType.fromQosTypeDto(type);
			if (qosType != null) {
				qosTypeToFieldMap.put(type, obtainFields(qosType.getResultClass()));
			}
		}
	}

	private void initMeasurementObjectiveMap() {
		//if this were a post-construct, all servers would need to request QoSMeasurementObjective in their application.yml (as they all know the storage service)
		//initialize the qos objective map (recalculate it everytime, if no server restart on qos definition change is desired)
		final List<QoSMeasurementObjective> objectiveList = qosMeasurementObjectiveRepository.findAllByEnabled(true);
		objectiveIdToMeasurementObjectiveMap = new HashMap<>();
		objectiveList.forEach( (o) -> objectiveIdToMeasurementObjectiveMap.put(Long.parseLong(o.getObjectiveId()), o));
	}
	
	public FullQoSMeasurement evaluateQoSMeasurement (final QoSMeasurement measurement, final Locale locale) {
		if (objectiveIdToMeasurementObjectiveMap == null) {
			initMeasurementObjectiveMap();
		}
		
		final FullQoSMeasurement ret = fullMeasurementResponseMapper.map(measurement);
		ret.setResults(new ArrayList<>());
		
		//start copying
		final Map<QoSMeasurementType, TreeSet<ResultDesc>> resultKeys = new HashMap<>();
		
		final Set<String> resultTranslationKeys = new HashSet<>();
		
		final List<EvaluatedQoSResultHolder> resultHolderList = new ArrayList<>();
		
		for (QoSResult qosResult : measurement.getResults()) {
			final QoSMeasurementObjective objective = objectiveIdToMeasurementObjectiveMap.get(qosResult.getObjectiveId());
			if (objective == null) {
				continue;
			}
			
			final EvaluatedQoSResultHolder evaluatedQoSResultHolder = compareTestResults(qosResult, objective, resultKeys);
			if (evaluatedQoSResultHolder != null) {
				final EvaluatedQoSResult evaluatedQoSResult = evaluatedQoSResultHolder.getEvalResult();
				evaluatedQoSResult.getResultKeyMap().forEach((key, outcome) -> resultTranslationKeys.add(key));
				resultTranslationKeys.add(evaluatedQoSResult.getSummary());
				resultTranslationKeys.add(evaluatedQoSResult.getDescription());
				resultHolderList.add(evaluatedQoSResultHolder);
			}
		}

		ret.setKeyToTranslationMap(getKeyToTranslationMapForKeys(resultTranslationKeys, locale));
		ret.setQosTypeToDescriptionMap(getQosMeasurementDecriptionMap(locale));
		
		final ResultOptions options = new ResultOptions(locale);
		
		resultHolderList.forEach((resultHolder) -> {
			final EvaluatedQoSResult result = resultHolder.getEvalResult();
			final AbstractResult abstractResult = resultHolder.getAbstractResult();
			
			result.setSummary(ret.getKeyToTranslationMap().get(result.getSummary()));
			result.setDescription(ret.getKeyToTranslationMap().get(result.getDescription()));
			
			final QoSMeasurementTypeDto typeDto = result.getType();
			final Object summary = TestScriptInterpreter.interpret(result.getSummary(), qosTypeToFieldMap.get(typeDto), abstractResult, true, options);
			final Object description = TestScriptInterpreter.interpret(result.getDescription(), qosTypeToFieldMap.get(typeDto), abstractResult, true, options);
			
			if (summary != null) {
				result.setSummary(summary.toString());
			}
			if (description != null) {
				result.setDescription(description.toString());
			}
			
			ret.getResults().add(result);
		});
		
		return ret;
	}

	private Map<String, String> getKeyToTranslationMapForKeys(final Set<String> resultTranslationKeys, final Locale locale) {
		return resultTranslationKeys
			.stream()
			.collect(
				Collectors.toMap(
					Function.identity(),
					key -> {
						return messageSource.getMessage(key, null, locale);
					}
				)
			);
	}

	private Map<QoSMeasurementTypeDto, QoSTypeDescription> getQosMeasurementDecriptionMap (final Locale locale) {
		final Map<QoSMeasurementTypeDto, QoSTypeDescription> ret = new HashMap<>();

		for (QoSMeasurementTypeDto type : QoSMeasurementTypeDto.values()) {
      final String typeString = type.toString();

			final QoSTypeDescription description = new QoSTypeDescription();

			description.setDescription(messageSource.getMessage(typeString + TRANSLATION_DESCRIPTION_SUFFIX, null, locale));
			description.setName(messageSource.getMessage(typeString + TRANSLATION_NAME_SUFFIX, null, locale));
			description.setIcon(messageSource.getMessage(typeString + TRANSLATION_ICON_FONT_SUFFIX, null, locale));

			ret.put(type, description);
		}

		return ret;
	}
	
	private EvaluatedQoSResultHolder compareTestResults (final QoSResult qosResult, final QoSMeasurementObjective objective, Map<QoSMeasurementType, TreeSet<ResultDesc>> resultKeys) {
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

		//final String json = objectMapper.writeValueAsString(qosResult.getResults());
		//result = objectMapper.readValue(json, resultClass);	// == testResult
		AbstractResult result = objectMapper.convertValue(qosResult.getResults(), resultClass);
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
			for (Map<String, Object> evaluation : objective.getEvaluations()) {
				final AbstractResult res = objectMapper.convertValue(evaluation, resultClass);

				if (res.getPriority() != null && res.getPriority() == Integer.MAX_VALUE) {
					res.setPriority(maxPriority--);
				}
				
				expResultSet.add(res);
			}

			final Map<String, EvaluatedQoSResult.QoSResultOutcome> resultKeyMap = new HashMap<>();

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

					resultKeyMap.put(desc.getKey(), resultHolder.resultKeyType);
					
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
			ret.setResultKeyMap(resultKeyMap);
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
			return new EvaluatedQoSResultHolder(ret, result);
		}
		
		return null;
	}
	
	private Class<? extends AbstractResult> getResultClass(final QoSMeasurementType type) {
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
			if (f.isAnnotationPresent(JsonProperty.class)) {
				String fieldName = f.getAnnotation(JsonProperty.class).value();
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
		QoSResultOutcome resultKeyType = null;
		String event = AbstractResult.BEHAVIOUR_NOTHING;
		
		//increase the failure or success counter of this result object
		if (resultDesc.getStatusCode().equals(ResultDesc.STATUS_CODE_SUCCESS)) {
			if (expResult.getOnSuccess() != null) {
				qosResult.setSuccessCount(qosResult.getSuccessCount() + 1);
				if (AbstractResult.RESULT_TYPE_DEFAULT.equals(expResult.getSuccessType())) {
					resultKeyType = QoSResultOutcome.OK;
				}
				else {
					resultKeyType = QoSResultOutcome.INFO;
				}
				
				event = expResult.getOnSuccessBehaivour();
			}
		}
		else {
			if (expResult.getOnFailure() != null) {
				qosResult.setFailureCount(qosResult.getFailureCount()+1);
				if (AbstractResult.RESULT_TYPE_DEFAULT.equals(expResult.getFailureType())) {
					resultKeyType = QoSResultOutcome.FAIL;
				}
				else {
					resultKeyType = QoSResultOutcome.INFO;
				}
				
				event = expResult.getOnFailureBehaivour();
			}
		}
		
		return resultKeyType != null ? new ResultHolder(resultKeyType, event) : null;
	}
	
	public static class EvaluatedQoSResultHolder {
		final EvaluatedQoSResult evalResult;
		final AbstractResult abstractResult;
		
		public EvaluatedQoSResultHolder(final EvaluatedQoSResult evalResult, final AbstractResult abstractResult) {
			this.evalResult = evalResult;
			this.abstractResult = abstractResult;
		}

		public EvaluatedQoSResult getEvalResult() {
			return evalResult;
		}

		public AbstractResult getAbstractResult() {
			return abstractResult;
		}
	}
	
	public static class ResultHolder {
		final QoSResultOutcome resultKeyType;
		final String event;
		
		public ResultHolder(final QoSResultOutcome resultKeyType, final String event) {
			this.resultKeyType = resultKeyType;
			this.event = event;
		}

		public QoSResultOutcome getResultKeyType() {
			return resultKeyType;
		}

		public String getEvent() {
			return event;
		}
	}
}
