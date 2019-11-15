package at.alladin.nettest.nntool.android.app.util;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.zafaco.speed.SpeedTaskDesc;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.common.LmapOptionDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapControlDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.QoSMeasurementTypeParameters;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.SpeedMeasurementTypeParameters;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.nntool.client.v2.task.TaskDesc;

/**
 * TODO: move this class to somewhere else (nettest-shared?)
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class LmapUtil {

    public enum LmapOption {
        SERVER_ADDR,
        SERVER_ADDR_IPV6,
        SERVER_PORT,
        ENCRYPTION,
        RESULT_COLLECTOR_BASE_URL
    }

    //TODO: remove (-> controller)
    private final static String fakeToken = "bbd1ee96-0779-4619-b993-bb4bf7089754_1528136454_3gr2gw9lVhtVONV0XO62Vamu/uw=";

    public static class LmapTaskWrapper {
        private List<TaskDesc> taskDescList;

        private SpeedTaskDesc speedTaskDesc;

        private String collectorUrl;

        private String speedCollectorUrl;

        public List<TaskDesc> getTaskDescList() {
            return taskDescList;
        }

        public void setTaskDescList(List<TaskDesc> taskDescList) {
            this.taskDescList = taskDescList;
        }

        public String getCollectorUrl() {
            return collectorUrl;
        }

        public void setCollectorUrl(String collectorUrl) {
            this.collectorUrl = collectorUrl;
        }

        public String getSpeedCollectorUrl() {
            return speedCollectorUrl;
        }

        public void setSpeedCollectorUrl(String speedCollectorUrl) {
            this.speedCollectorUrl = speedCollectorUrl;
        }

        public SpeedTaskDesc getSpeedTaskDesc() {
            return speedTaskDesc;
        }

        public void setSpeedTaskDesc(SpeedTaskDesc speedTaskDesc) {
            this.speedTaskDesc = speedTaskDesc;
        }
    }

    public static LmapTaskWrapper extractQosTaskDescList(final LmapControlDto controlDto) {
        final List<TaskDesc> taskDescList = new ArrayList<>();


        String serverAddr = null;
        String collectorUrl = null;
        Integer serverPort = null;
        Boolean encryption = false;

        String speedCollectorUrl = null;
        String speedServerAddrV4 = null;
        String speedServerAddrV6 = null;
        Integer speedServerPort = null;
        Boolean speedEncryption = false;
        SpeedTaskDesc speedTaskDesc = null;

        try {
            System.out.println(ObjectMapperUtil.createBasicObjectMapper().writeValueAsString(controlDto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (controlDto != null && controlDto.getTasks() != null) {
            for (final LmapTaskDto task : controlDto.getTasks()) {

                if (MeasurementTypeDto.QOS.toString().equals(task.getName())) {

                    if (task.getOptions() == null) {
                        break;
                    }

                    QoSMeasurementTypeParameters qosParams = null;

                    for (final LmapOptionDto option : task.getOptions()) {
                        if (option.getMeasurementParameters() != null) {
                            qosParams = (QoSMeasurementTypeParameters) option.getMeasurementParameters();
                        } else {
                            try {
                                final LmapOption qosOption = LmapOption.valueOf(option.getName().toUpperCase(Locale.US));
                                switch (qosOption) {
                                    case SERVER_ADDR:
                                        serverAddr = option.getValue();
                                        break;
                                    case SERVER_PORT:
                                        serverPort = Integer.valueOf(option.getValue());
                                        break;
                                    case ENCRYPTION:
                                        encryption = Boolean.valueOf(option.getValue());
                                        break;
                                    case RESULT_COLLECTOR_BASE_URL:
                                        collectorUrl = option.getValue();
                                        break;
                                }
                            } catch (Exception e) {
                                //bad option name or illegal cast
                                e.printStackTrace();
                            }
                        }
                    }

                    if (qosParams != null) {
                        for (final Map.Entry<QoSMeasurementTypeDto, List<Map<String, Object>>> e : qosParams.getObjectives().entrySet()) {
                            try {
                                final QosMeasurementType qosType = QosMeasurementType.fromQosTypeDto(e.getKey());
                                for (final Map<String, Object> qosTestParams : e.getValue()) {
                                    if (serverAddr != null && serverPort != null && !qosTestParams.containsKey("server_addr")) {
                                        qosTestParams.put("server_port", serverPort);
                                        qosTestParams.put("server_addr", serverAddr);
                                    }

                                    final TaskDesc taskDesc = new TaskDesc(serverAddr, serverPort != null ? serverPort : -1, encryption,
                                            fakeToken, 0, 1, 0,
                                            System.nanoTime(), qosTestParams, qosType.getValue());

                                    taskDescList.add(taskDesc);
                                }
                            } catch (final Exception ex) {
                                //unknown qos type
                                ex.printStackTrace();
                            }
                        }
                    }
                //speed params
                } else if (MeasurementTypeDto.SPEED.toString().equals(task.getName())) {

                    if (task.getOptions() == null) {
                        break;
                    }

                    for (final LmapOptionDto option : task.getOptions()) {
                        if (option.getMeasurementParameters() != null) {
                            speedTaskDesc = parseSpeedMeasurementTypeParameters((SpeedMeasurementTypeParameters) option.getMeasurementParameters());
                        } else {
                            try {
                                final LmapOption speedOption = LmapOption.valueOf(option.getName().toUpperCase(Locale.US));
                                switch (speedOption) {
                                    case RESULT_COLLECTOR_BASE_URL:
                                        speedCollectorUrl = option.getValue();
                                        break;
                                    case SERVER_ADDR:
                                        speedServerAddrV4 = option.getValue();
                                        break;
                                    case SERVER_ADDR_IPV6:
                                        speedServerAddrV6 = option.getValue();
                                        break;
                                    case ENCRYPTION:
                                        speedEncryption = Boolean.valueOf(option.getValue());
                                        break;
                                    case SERVER_PORT:
                                        speedServerPort = Integer.valueOf(option.getValue());
                                        break;
                                }
                            } catch (Exception e) {
                                //bad option name or illegal cast
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        final LmapTaskWrapper wrapper = new LmapTaskWrapper();
        wrapper.setTaskDescList(taskDescList);
        wrapper.setCollectorUrl(collectorUrl);
        wrapper.setSpeedCollectorUrl(speedCollectorUrl);

        if (speedTaskDesc != null) {
            speedTaskDesc.setSpeedServerAddrV4(speedServerAddrV4);
            speedTaskDesc.setSpeedServerAddrV6(speedServerAddrV6);
            speedTaskDesc.setSpeedServerPort(speedServerPort);
            speedTaskDesc.setUseEncryption(speedEncryption);
        }
        wrapper.setSpeedTaskDesc(speedTaskDesc);

        return wrapper;
    }

    private static SpeedTaskDesc parseSpeedMeasurementTypeParameters (final SpeedMeasurementTypeParameters params) {
        final SpeedTaskDesc ret = new SpeedTaskDesc();
        if (params == null) {
            return ret;
        }

        if (params.getRttCount() != null) {
            ret.setRttCount(params.getRttCount());
        }
        if (params.getMeasurementConfiguration() != null) {
            if (params.getMeasurementConfiguration().getDownloadClassList() != null) {
               for (final SpeedMeasurementTypeParameters.SpeedMeasurementConfiguration.SpeedMeasurementClass speedClass : params.getMeasurementConfiguration().getDownloadClassList()) {
                   if (speedClass.getDefault() && speedClass.getNumStreams() != null) {
                       ret.setDownloadStreams(speedClass.getNumStreams());
                       break;
                   }
               }
            }

            if (params.getMeasurementConfiguration().getUploadClassList() != null) {
                for (final SpeedMeasurementTypeParameters.SpeedMeasurementConfiguration.SpeedMeasurementClass speedClass : params.getMeasurementConfiguration().getUploadClassList()) {
                    if (speedClass.getDefault() && speedClass.getNumStreams() != null) {
                        ret.setUploadStreams(speedClass.getNumStreams());
                        break;
                    }
                }
            }
        }
        return ret;
    }
}
