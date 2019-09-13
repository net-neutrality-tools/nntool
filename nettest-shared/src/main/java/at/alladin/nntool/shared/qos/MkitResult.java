package at.alladin.nntool.shared.qos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public abstract class MkitResult extends AbstractResult {

    @JsonProperty("mkit_result")
    private Object result;

    @JsonProperty("test_type")
    private String testType;

    @JsonProperty("mkit_status")
    private String status;

    @JsonProperty("mkit_bytes_download")
    private Long bytesDownload;

    @JsonProperty("mkit_bytes_upload")
    private Long bytesUpload;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public Long getBytesDownload() {
        return bytesDownload;
    }

    public void setBytesDownload(Long bytesDownload) {
        this.bytesDownload = bytesDownload;
    }

    public Long getBytesUpload() {
        return bytesUpload;
    }

    public void setBytesUpload(Long bytesUpload) {
        this.bytesUpload = bytesUpload;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MkitResult [result=" + result + ", testType=" + testType + ", status=" + status + ", bytesDownload="
                + bytesDownload + ", bytesUpload=" + bytesUpload + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MkitResult other = (MkitResult) obj;
        if (bytesDownload == null) {
            if (other.bytesDownload != null)
                return false;
        } else if (!bytesDownload.equals(other.bytesDownload))
            return false;
        if (bytesUpload == null) {
            if (other.bytesUpload != null)
                return false;
        } else if (!bytesUpload.equals(other.bytesUpload))
            return false;
        if (result == null) {
            if (other.result != null)
                return false;
        } else if (!result.equals(other.result))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (testType == null) {
            if (other.testType != null)
                return false;
        } else if (!testType.equals(other.testType))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bytesDownload == null) ? 0 : bytesDownload.hashCode());
        result = prime * result + ((bytesUpload == null) ? 0 : bytesUpload.hashCode());
        result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((testType == null) ? 0 : testType.hashCode());
        return result;
    }
}
