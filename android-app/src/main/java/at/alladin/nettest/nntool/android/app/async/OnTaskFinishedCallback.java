package at.alladin.nettest.nntool.android.app.async;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public interface OnTaskFinishedCallback<T> {

    void onTaskFinished(T result);
}
