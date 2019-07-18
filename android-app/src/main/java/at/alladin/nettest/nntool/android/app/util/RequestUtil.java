package at.alladin.nettest.nntool.android.app.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import at.alladin.nettest.nntool.android.app.BuildConfig;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.support.telephony.CellIdentityWrapper;
import at.alladin.nettest.nntool.android.app.support.telephony.CellInfoWrapper;
import at.alladin.nettest.nntool.android.app.support.telephony.CellSignalStrengthWrapper;
import at.alladin.nettest.nntool.android.app.util.info.InformationCollector;
import at.alladin.nettest.nntool.android.app.util.info.network.MobileOperator;
import at.alladin.nettest.nntool.android.app.util.info.network.WifiOperator;
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
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultNetworkPointInTimeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.QoSMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.SpeedMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.SubMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.TimeBasedResultDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.CellInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.MeasurementAgentTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.SignalDto;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class RequestUtil {

    private final static String TAG = RequestUtil.class.getSimpleName();

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

    public static LmapControlDto prepareMeasurementInitiationRequest (final String selectedMeasurementPeerIdentifier, final Context context) {
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
        if (selectedMeasurementPeerIdentifier != null) {
            capabilityTask.setSelectedMeasurementPeerIdentifier(selectedMeasurementPeerIdentifier);
        }
        capabilityTaskDtoList.add(capabilityTask);

        capabilityTask = new LmapCapabilityTaskDto();
        capabilityTask.setVersion(context.getResources().getString(R.string.default_qos_configuration_version));
        capabilityTask.setTaskName(MeasurementTypeDto.QOS.toString());
        capabilityTaskDtoList.add(capabilityTask);

        try {
            System.out.println(ObjectMapperUtil.createBasicObjectMapper().writeValueAsString(request));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return request;
    }

    public static LmapReportDto prepareLmapReportForMeasurement(final List<SubMeasurementResult> subMeasurementResultList, final InformationCollector informationCollector, final Context context) {
        final LmapReportDto report = new LmapReportDto();

        report.setAdditionalRequestInfo(prepareApiRequestInfo(context));
        report.setAgentId(report.getAdditionalRequestInfo().getAgentId());

        if (subMeasurementResultList != null && subMeasurementResultList.size() > 0) {
            for (SubMeasurementResult subMeasurementResult : subMeasurementResultList) {
                if (subMeasurementResult != null) {
                    LmapResultDto lmapResult = null;
                    if (subMeasurementResult instanceof SpeedMeasurementResult) {
                        lmapResult = new LmapResultDto();
                    } else if (subMeasurementResult instanceof QoSMeasurementResult) {
                        lmapResult = new LmapResultDto();
                    }

                    if (lmapResult != null) {
                        if (report.getResults() == null) {
                            report.setResults(new ArrayList<>());
                        }
                        report.getResults().add(lmapResult);
                        lmapResult.setResults(new ArrayList<>());
                        lmapResult.getResults().add(subMeasurementResult);
                    }
                }
            }
        }

        if (informationCollector != null) {
            Log.d(TAG, "Operator [illegal network state detected: "
                    + informationCollector.getIllegalNetworkStateDetected() +"] -> " + informationCollector.getOperatorInfo());

            final TimeBasedResultDto timeBasedResultDto = new TimeBasedResultDto();
            report.setTimeBasedResult(timeBasedResultDto);

            timeBasedResultDto.setGeoLocations(informationCollector.getGeoLocationList());

            if (informationCollector.getCellInfoList() != null && informationCollector.getCellInfoList().size() > 0) {
                final List<SignalDto> signalDtoList = new ArrayList<>();
                for (CellInfoWrapper ciWrap : informationCollector.getCellInfoList()) {
                    final SignalDto signalDto = cellInfoWrapperToSignalDto(ciWrap, informationCollector);
                    if (signalDto != null) {
                        signalDtoList.add(signalDto);
                    }
                }

                timeBasedResultDto.setSignals(signalDtoList);
            }

            final InformationCollector.OperatorInfoHolder operatorInfo = informationCollector.getOperatorInfo();
            if (operatorInfo != null && operatorInfo.getOperatorInfo() != null) {
                timeBasedResultDto.setNetworkPointsInTime(new ArrayList<>());

                final MeasurementResultNetworkPointInTimeDto networkInfoDto = new MeasurementResultNetworkPointInTimeDto();
                timeBasedResultDto.getNetworkPointsInTime().add(networkInfoDto);

                if (operatorInfo.getOperatorInfo() instanceof WifiOperator) {
                    final WifiOperator operator = (WifiOperator) operatorInfo.getOperatorInfo();
                    networkInfoDto.setBssid(operator.getBssid());
                    networkInfoDto.setSsid(operator.getSsid());
                }
                else if (operatorInfo.getOperatorInfo() instanceof MobileOperator) {
                    final MobileOperator operator = (MobileOperator) operatorInfo.getOperatorInfo();
                    networkInfoDto.setNetworkOperatorMccMnc(operator.getNetworkOperator());
                    networkInfoDto.setNetworkOperatorName(operator.getNetworkOperatorName());
                    networkInfoDto.setNetworkCountry(operator.getNetworkCountryCode());
                    networkInfoDto.setSimOperatorMccMnc(operator.getSimOperator());
                    networkInfoDto.setSimOperatorName(operator.getSimOpetatorName());
                    networkInfoDto.setSimCountry(operator.getSimCountryCode());
                }

                networkInfoDto.setTime(operatorInfo.getTime());
                networkInfoDto.setRelativeTimeNs(informationCollector.getStartTimeNs() - operatorInfo.getTimestampNs());
                networkInfoDto.setNetworkTypeId(operatorInfo.getNetworkId());

            }

        }

        return report;
    }

    public static ApiRequest<SpeedMeasurementPeerRequest> prepareApiSpeedMeasurementPeerRequest(final Context context) {
        final ApiRequest<SpeedMeasurementPeerRequest> apiRequest = new ApiRequest<>();
        apiRequest.setData(new SpeedMeasurementPeerRequest());
        apiRequest.setRequestInfo(prepareApiRequestInfo(context));
        return apiRequest;
    }

    private static SignalDto cellInfoWrapperToSignalDto(final CellInfoWrapper cellInfoWrapper, final InformationCollector informationCollector) {
        if (cellInfoWrapper == null || cellInfoWrapper.getCellSignalStrengthWrapper() == null) {
            return null;
        }

        final SignalDto signalDto = new SignalDto();
        final CellSignalStrengthWrapper sigWrap = cellInfoWrapper.getCellSignalStrengthWrapper();
        signalDto.setLteCqi(sigWrap.getLteCqi());
        signalDto.setLteRsrpDbm(sigWrap.getLteRsrp());
        signalDto.setLteRsrqDb(sigWrap.getLteRsrq());
        signalDto.setLteRssnrDb(sigWrap.getLteRssnr());
        signalDto.setNetworkTypeId(sigWrap.getNetworkId());
        signalDto.setSignalStrength2g3gDbm(sigWrap.getSignalStrength());
        signalDto.setWifiLinkSpeedBps(sigWrap.getWifiLinkSpeed());
        signalDto.setWifiRssiDbm(sigWrap.getWifiRssi());
        signalDto.setRelativeTimeNs(sigWrap.getTimeStampNano() - informationCollector.getStartTimeNs());

        if (cellInfoWrapper.getCellIdentityWrapper() != null) {
            final CellIdentityWrapper iWrap = cellInfoWrapper.getCellIdentityWrapper();
            signalDto.setWifiBssid(iWrap.getWifiBssid());
            signalDto.setWifiSsid(iWrap.getWifiSsid());

            final CellInfoDto cellInfoDto = new CellInfoDto();
            signalDto.setCellInfo(cellInfoDto);

            cellInfoDto.setAreaCode(iWrap.getAreaCode());
            cellInfoDto.setCellId(iWrap.getCellId());
            cellInfoDto.setPrimaryScramblingCode(iWrap.getScramblingCode());
            cellInfoDto.setFrequency(iWrap.getFrequency());
        }

        return signalDto;
    }
}
