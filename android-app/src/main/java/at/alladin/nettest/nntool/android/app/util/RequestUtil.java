package at.alladin.nettest.nntool.android.app.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.text.TextUtils;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import at.alladin.nettest.nntool.android.app.BuildConfig;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.workflow.tc.TermsAndConditionsFragment;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequestInfo;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapAgentDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapCapabilityDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapCapabilityTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapControlDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapResultDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.QoSMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.MeasurementAgentTypeDto;
import at.alladin.nntool.client.v2.task.result.QoSResultCollector;
import at.alladin.nntool.client.v2.task.result.QoSTestResult;
import okhttp3.Request;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class RequestUtil {

    /**
     * prepares a {@link ApiRequestInfo} object already filled with available information
     * @param context
     * @return
     */
    public static ApiRequestInfo prepareApiRequestInfo(final Context context) {
        final ApiRequestInfo info = new ApiRequestInfo();
        info.setAgentType(MeasurementAgentTypeDto.MOBILE);
        info.setOsName("Android");
        info.setOsVersion(android.os.Build.VERSION.RELEASE + "(" + android.os.Build.VERSION.INCREMENTAL + ")");
        info.setAgentId(PreferencesUtil.getAgentUuid(context));
        info.setApiLevel(String.valueOf(Build.VERSION.SDK_INT));
        info.setModel(android.os.Build.MODEL);
        info.setCodeName(android.os.Build.DEVICE);
        info.setLanguage(Locale.getDefault().getLanguage());
        info.setTimezone(TimeZone.getDefault().getID());
        info.setAppVersionCode(BuildConfig.VERSION_CODE);
        info.setAppVersionName(BuildConfig.VERSION_NAME);
        return info;
    }

    public static RegistrationRequest prepareRegistrationRequest(final Context context) {
        final RegistrationRequest request = new RegistrationRequest();
        final String groupName = context.getResources().getString(R.string.default_agent_group_name);
        request.setGroupName(TextUtils.isEmpty(groupName) ? null : groupName);
        request.setTermsAndConditionsAcceptedVersion(
                PreferencesUtil.getTermsAndConditionsAcceptedVersion(context));
        request.setTermsAndConditionsAccepted(
                PreferencesUtil.isTermsAndConditionsAccepted(context,
                        TermsAndConditionsFragment.TERMS_AND_CONDITIONS_VERSION));

        System.out.println(request);

        return request;
    }

    public static ApiRequest<RegistrationRequest> prepareApiRegistrationRequest(final Context context) {
        final ApiRequest<RegistrationRequest> apiRequest = new ApiRequest<>();
        apiRequest.setData(prepareRegistrationRequest(context));
        apiRequest.setRequestInfo(prepareApiRequestInfo(context));
        return apiRequest;
    }

    public static LmapControlDto prepareMeasurementInitiationRequest (final Context context) {
        final LmapControlDto request = new LmapControlDto();
        final LmapAgentDto agentDto = new LmapAgentDto();
        agentDto.setAgentId(PreferencesUtil.getAgentUuid(context));
        request.setAgent(agentDto);
        request.setAdditionalRequestInfo(prepareApiRequestInfo(context));

        final LmapCapabilityDto capabilities = new LmapCapabilityDto();
        request.setCapabilities(capabilities);
        final List<LmapCapabilityTaskDto> capabilityTaskDtoList = new ArrayList<>();
        capabilities.setTasks(capabilityTaskDtoList);

        LmapCapabilityTaskDto capabilityTask = new LmapCapabilityTaskDto();
        capabilityTask.setVersion(context.getResources().getString(R.string.default_speed_configuration_version));
        capabilityTask.setTaskName(MeasurementTypeDto.SPEED.toString());
        capabilityTaskDtoList.add(capabilityTask);

        capabilityTask = new LmapCapabilityTaskDto();
        capabilityTask.setVersion(context.getResources().getString(R.string.default_qos_configuration_version));
        capabilityTask.setTaskName(MeasurementTypeDto.QOS.toString());
        capabilityTaskDtoList.add(capabilityTask);

        return request;
    }

    public static LmapReportDto prepareLmapReportForQosMeasurement(final QoSResultCollector qoSResultCollector, final Context context) {
        final LmapReportDto report = new LmapReportDto();

        report.setAdditionalRequestInfo(prepareApiRequestInfo(context));
        report.setAgentId(report.getAdditionalRequestInfo().getAgentId());
        if (qoSResultCollector != null && qoSResultCollector.getResults() != null) {
            final QoSMeasurementResult qoSMeasurementResult = new QoSMeasurementResult();
            final LmapResultDto<QoSMeasurementResult> lmapResult = new LmapResultDto<>();

            report.setResults(new ArrayList<>());
            report.getResults().add(lmapResult);

            lmapResult.setResults(new ArrayList<>());
            lmapResult.getResults().add(qoSMeasurementResult);
            qoSMeasurementResult.setObjectiveResults(new ArrayList<>());
            for (final QoSTestResult qosResult : qoSResultCollector.getResults()) {
                qoSMeasurementResult.getObjectiveResults().add(qosResult.getResultMap());
            }
        }

        return report;
    }
}
