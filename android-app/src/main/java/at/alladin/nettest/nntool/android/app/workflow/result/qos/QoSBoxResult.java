package at.alladin.nettest.nntool.android.app.workflow.result.qos;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class QoSBoxResult {

    private QoSMeasurementTypeDto type;

    private String name;

    private String icon;

    private Integer successCount;

    private Integer evaluationCount;

    public QoSMeasurementTypeDto getType() {
        return type;
    }

    public void setType(QoSMeasurementTypeDto type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getEvaluationCount() {
        return evaluationCount;
    }

    public void setEvaluationCount(Integer evaluationCount) {
        this.evaluationCount = evaluationCount;
    }

}
