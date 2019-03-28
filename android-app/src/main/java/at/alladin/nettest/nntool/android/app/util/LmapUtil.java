package at.alladin.nettest.nntool.android.app.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.common.LmapOptionDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapControlDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.QoSMeasurementTypeParameters;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.nntool.client.v2.task.TaskDesc;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class LmapUtil {

    public enum LmapQosOption {
        SERVER_ADDR,
        SERVER_PORT,
        ENCRYPTION
    }

    //TODO: remove
    private final static String fakeToken = "bbd1ee96-0779-4619-b993-bb4bf7089754_1528136454_3gr2gw9lVhtVONV0XO62Vamu/uw=";

    public static List<TaskDesc> extractQosTaskDescList(final LmapControlDto controlDto) {
        final List<TaskDesc> taskDescList = new ArrayList<>();

        String serverAddr = null;
        Integer serverPort = null;
        Boolean encryption = false;

        try {
            System.out.println(new ObjectMapper().writeValueAsString(controlDto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (controlDto != null && controlDto.getTasks() != null) {
            for (final LmapTaskDto task : controlDto.getTasks()) {
                if (!MeasurementTypeDto.QOS.toString().equals(task.getName())) {
                    continue;
                }

                if (task.getOptions() == null) {
                    break;
                }

                QoSMeasurementTypeParameters qosParams = null;

                for (final LmapOptionDto option : task.getOptions()) {
                    if (option.getMeasurementParameters() != null) {
                        qosParams = (QoSMeasurementTypeParameters) option.getMeasurementParameters();
                    }
                    else {
                        try {
                            final LmapQosOption qosOption = LmapQosOption.valueOf(option.getName().toUpperCase(Locale.US));
                            switch(qosOption) {
                                case SERVER_ADDR:
                                    serverAddr = option.getValue();
                                    break;
                                case SERVER_PORT:
                                    serverPort = Integer.valueOf(option.getValue());
                                    break;
                                case ENCRYPTION:
                                    encryption = Boolean.valueOf(option.getValue());
                                    break;
                            }
                        }
                        catch (Exception e) {
                            //bad option name or illegal cast
                            e.printStackTrace();
                        }
                    }
                }

                if (qosParams != null && serverAddr != null && serverPort != null) {
                    for (final Map.Entry<QoSMeasurementTypeDto, List<Map<String, Object>>> e : qosParams.getObjectives().entrySet()) {
                        try {
                            final QosMeasurementType qosType = QosMeasurementType.fromQosTypeDto(e.getKey());
                            for (final Map<String, Object> qosTestParams : e.getValue()) {
                                if (!qosTestParams.containsKey("server_addr")) {
                                    qosTestParams.put("server_port", serverPort);
                                    qosTestParams.put("server_addr", serverAddr);
                                }

                                final TaskDesc taskDesc = new TaskDesc(serverAddr, serverPort, encryption,
                                        fakeToken, 0, 1, 0,
                                        System.nanoTime(), qosTestParams, qosType.getValue());

                                taskDescList.add(taskDesc);
                            }
                        }
                        catch (final Exception ex) {
                            //unknown qos type
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }

        return taskDescList;
    }
}
