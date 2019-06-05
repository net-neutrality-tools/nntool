package at.alladin.nettest.nntool.android.app.util.info.signal;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class SignalStrengthChangeEvent {

    private CurrentSignalStrength currentSignalStrength;

    public SignalStrengthChangeEvent(final CurrentSignalStrength currentSignalStrength) {
        this.currentSignalStrength = currentSignalStrength;
    }

    public CurrentSignalStrength getCurrentSignalStrength() {
        return currentSignalStrength;
    }

    public void setCurrentSignalStrength(CurrentSignalStrength currentSignalStrength) {
        this.currentSignalStrength = currentSignalStrength;
    }

    @Override
    public String toString() {
        return "SignalStrengthChangeEvent{" +
                "currentSignalStrength=" + currentSignalStrength +
                '}';
    }
}
