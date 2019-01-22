package at.alladin.nettest.qos;

public class QoSMeasurementContext {

    private String controlServerHost;

    private int controlServerPort;

    private boolean useTls;

    private int controlServerApiVersion;

    public QoSMeasurementContext (final String controlServerHost) {
        this.controlServerHost = controlServerHost;
        this.controlServerPort = 443;
        this.useTls = true;
        this.controlServerApiVersion = 1;
    }

    public QoSMeasurementContext (final String controlServerHost, final int controlServerPort, final boolean useTls, final int controlServerApiVersion) {
        this.controlServerHost = controlServerHost;
        this.controlServerPort = controlServerPort;
        this.useTls = useTls;
        this.controlServerApiVersion = controlServerApiVersion;
    }

    public String getControlServerHost() {
        return controlServerHost;
    }

    public void setControlServerHost(String controlServerHost) {
        this.controlServerHost = controlServerHost;
    }

    public int getControlServerPort() {
        return controlServerPort;
    }

    public void setControlServerPort(int controlServerPort) {
        this.controlServerPort = controlServerPort;
    }

    public boolean isUseTls() {
        return useTls;
    }

    public void setUseTls(boolean useTls) {
        this.useTls = useTls;
    }

    public int getControlServerApiVersion() {
        return controlServerApiVersion;
    }

    public void setControlServerApiVersion(int controlServerApiVersion) {
        this.controlServerApiVersion = controlServerApiVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QoSMeasurementContext that = (QoSMeasurementContext) o;

        if (controlServerPort != that.controlServerPort) return false;
        if (useTls != that.useTls) return false;
        if (controlServerApiVersion != that.controlServerApiVersion) return false;
        if (controlServerHost != null ? !controlServerHost.equals(that.controlServerHost) : that.controlServerHost != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = controlServerHost != null ? controlServerHost.hashCode() : 0;
        result = 31 * result + controlServerPort;
        result = 31 * result + (useTls ? 1 : 0);
        result = 31 * result + controlServerApiVersion;
        return result;
    }

    @Override
    public String toString() {
        return "QoSMeasurementContext{" +
                "controlServerHost='" + controlServerHost + '\'' +
                ", controlServerPort=" + controlServerPort +
                ", useTls=" + useTls +
                ", controlServerApiVersion=" + controlServerApiVersion +
                '}';
    }
}
