package at.alladin.nettest.nntool.android.app.util.info.system;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class SystemInfoEvent {

    private CurrentSystemInfo currentSystemInfo;

    public SystemInfoEvent(final CurrentSystemInfo currentSystemInfo) {
        this.currentSystemInfo = currentSystemInfo;
    }

    public CurrentSystemInfo getCurrentSystemInfo() {
        return currentSystemInfo;
    }

    public void setCurrentSystemInfo(CurrentSystemInfo currentSystemInfo) {
        this.currentSystemInfo = currentSystemInfo;
    }

    @Override
    public String toString() {
        return "SystemInfoEvent{" +
                "currentSystemInfo=" + currentSystemInfo +
                '}';
    }
}
