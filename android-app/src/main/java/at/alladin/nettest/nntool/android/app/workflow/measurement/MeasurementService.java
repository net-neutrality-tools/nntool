package at.alladin.nettest.nntool.android.app.workflow.measurement;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.async.OnTaskFinishedCallback;
import at.alladin.nettest.nntool.android.app.async.SendReportTask;
import at.alladin.nettest.nntool.android.app.util.AlertDialogUtil;
import at.alladin.nettest.nntool.android.app.util.FunctionalityHelper;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;
import at.alladin.nettest.nntool.android.app.util.RequestUtil;
import at.alladin.nettest.nntool.android.app.util.info.InformationCollector;
import at.alladin.nettest.nntool.android.app.util.info.InformationProvider;
import at.alladin.nettest.nntool.android.app.util.info.InformationService;
import at.alladin.nettest.nntool.android.app.util.info.interfaces.CurrentInterfaceTraffic;
import at.alladin.nettest.nntool.android.app.workflow.WorkflowTarget;
import at.alladin.nettest.nntool.android.speed.JniSpeedMeasurementResult;
import at.alladin.nettest.nntool.android.speed.SpeedMeasurementState;
import at.alladin.nettest.nntool.android.speed.SpeedTaskDesc;
import at.alladin.nettest.nntool.android.speed.jni.JniSpeedMeasurementClient;
import at.alladin.nettest.qos.QoSMeasurementClientControlAdapter;
import at.alladin.nettest.qos.android.QoSMeasurementClientAndroid;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.SpeedMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.SubMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.TimeBasedResultDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ConnectionInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.StatusDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.TrafficDto;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.nntool.client.ClientHolder;
import at.alladin.nntool.client.v2.task.TaskDesc;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class MeasurementService extends Service implements ServiceConnection {

    final static String TAG = MeasurementService.class.getSimpleName();

    public static String ACTION_START_SPEED_MEASUREMENT = "at.alladin.nettest.nntool.android.app.startSpeedMeasurement";

    public static String ACTION_START_QOS_MEASUREMENT = "at.alladin.nettest.nntool.android.app.startQosMeasurement";

    public static String EXTRAS_KEY_QOS_TASK_DESC_LIST = "qos_task_desk_list";

    public static String EXTRAS_KEY_QOS_TASK_COLLECTOR_URL = "qos_task_collector_url";

    public static String EXTRAS_KEY_SPEED_TASK_COLLECTOR_URL = "speed_task_collector_url";

    public static String EXTRAS_KEY_SPEED_TASK_DESC = "speed_task_desc";

    public static String EXTRAS_KEY_SPEED_TASK_CLIENT_IPV4_PRIVATE = "speed_task_client_ipv4_private";

    public static String EXTRAS_KEY_SPEED_TASK_CLIENT_IPV6_PRIVATE = "speed_task_client_ipv6_private";

    public static String EXTRAS_KEY_SPEED_TASK_CLIENT_IPV4_PUBLIC = "speed_task_client_ipv4_public";

    public static String EXTRAS_KEY_SPEED_TASK_CLIENT_IPV6_PUBLIC = "speed_task_client_ipv6_public";

    public static String EXTRAS_KEY_FOLLOW_UP_ACTIONS = "measurement_follow_up_actions";

    public static String EXTRAS_KEY_SPEED_EXECUTE = "speed_execute_measurement";

    public static String EXTRAS_KEY_QOS_EXECUTE = "qos_execute_measurement";

    public final static String IF_TRAFFIC_LIST_UPLOAD_TAG = "UPLOAD";

    public final static String IF_TRAFFIC_LIST_DOWNLOAD_TAG = "DOWNLOAD";

    public final static String IF_TRAFFIC_LIST_PING_TAG = "PING";

    final MeasurementServiceBinder binder = new MeasurementServiceBinder();

    final AtomicBoolean isBound = new AtomicBoolean(false);

    private Long overallStartTimeNs;

    private Long subMeasurementStartTimeNs;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private String collectorUrl;

    /**
     *  Boolean indicating if the currently started submeasurement is a follow up measurement to another (already executed) sub-measurement (true)
     *  or if the currently started submeasurement is the first (false)
    */
    private final AtomicBoolean isFollowUpAction = new AtomicBoolean(false);

    QoSMeasurementClientAndroid qosMeasurementClient;

    InformationCollector informationCollector;

    private JniSpeedMeasurementClient jniSpeedMeasurementClient;

    private List<SubMeasurementResult> subMeasurementResultList = new ArrayList<>();

    private Bundle bundle;

    private ArrayList<MeasurementType> followUpActions;

    private OnMeasurementErrorListener onMeasurementErrorListener;

    public class MeasurementServiceBinder extends Binder {
        public MeasurementService getService() {
            return MeasurementService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        final Intent serviceIntent = new Intent(this, InformationService.class);
        bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        unbindService(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "BIND " + intent);
        this.isBound.set(true);
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "UNBIND " + intent);
        this.isBound.set(false);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "REBIND " + intent);
        this.isBound.set(true);
        super.onRebind(intent);
    }

    public QoSMeasurementClientAndroid getQosMeasurementClient() {
        return qosMeasurementClient;
    }

    public JniSpeedMeasurementClient getJniSpeedMeasurementClient() {
        return jniSpeedMeasurementClient;
    }

    public void startMeasurement(final Bundle options) {
        this.bundle = options;
        followUpActions = (ArrayList<MeasurementType>) options.getSerializable(EXTRAS_KEY_FOLLOW_UP_ACTIONS);
        subMeasurementStartTimeNs = System.nanoTime();

        this.collectorUrl = options.getString(EXTRAS_KEY_SPEED_TASK_COLLECTOR_URL);
        final SpeedTaskDesc speedTaskDesc = (SpeedTaskDesc) options.getSerializable(EXTRAS_KEY_SPEED_TASK_DESC);
        final String clientIpv4 = options.getString(EXTRAS_KEY_SPEED_TASK_CLIENT_IPV4_PRIVATE);
        final String clientIpv6 = options.getString(EXTRAS_KEY_SPEED_TASK_CLIENT_IPV6_PRIVATE);

        //if ipV6 is available, use it
        if (clientIpv6 != null && !PreferencesUtil.isForceIpv4(getApplicationContext())) {
            Log.d(TAG, "Using IPv6...");
            speedTaskDesc.setUseIpV6(true);
            speedTaskDesc.setClientIp(clientIpv6);
        } else {
            Log.d(TAG, "Using IPv4...");
            speedTaskDesc.setClientIp(clientIpv4);
        }

        //fetch user settings to adapt provided taskDesc
        speedTaskDesc.setPerformRtt(PreferencesUtil.isPingEnabled(getApplicationContext()));
        speedTaskDesc.setPerformDownload(PreferencesUtil.isDownloadEnabled(getApplicationContext()));
        speedTaskDesc.setPerformUpload(PreferencesUtil.isUploadEnabled(getApplicationContext()));

        jniSpeedMeasurementClient = new JniSpeedMeasurementClient(speedTaskDesc);

        jniSpeedMeasurementClient.addMeasurementFinishedListener(new JniSpeedMeasurementClient.MeasurementFinishedListener() {
            @Override
            public void onMeasurementFinished(JniSpeedMeasurementResult result, SpeedTaskDesc taskDesc) {
                final SpeedMeasurementResult speedMeasurementResult = SubMeasurementResultParseUtil.parseIntoSpeedMeasurementResult(result, taskDesc);
                speedMeasurementResult.setRelativeStartTimeNs(overallStartTimeNs);
                speedMeasurementResult.setStatus(StatusDto.FINISHED);

                final Map<String, CurrentInterfaceTraffic> ifMap = informationCollector.getInterfaceTrafficMap();
                if (ifMap != null && ifMap.size() > 0) {
                    if (speedMeasurementResult.getConnectionInfo() == null) {
                        speedMeasurementResult.setConnectionInfo(new ConnectionInfoDto());
                    }

                    final CurrentInterfaceTraffic ifUp = ifMap.get(MeasurementService.IF_TRAFFIC_LIST_UPLOAD_TAG);
                    if (ifUp != null) {
                        final TrafficDto trafficDto = new TrafficDto();
                        trafficDto.setBytesRx(ifUp.getRxBytes());
                        trafficDto.setBytesTx(ifUp.getTxBytes());
                        speedMeasurementResult.getConnectionInfo().setAgentInterfaceUploadMeasurementTraffic(trafficDto);
                    }

                    final CurrentInterfaceTraffic ifDown = ifMap.get(MeasurementService.IF_TRAFFIC_LIST_DOWNLOAD_TAG);
                    if (ifDown != null) {
                        final TrafficDto trafficDto = new TrafficDto();
                        trafficDto.setBytesRx(ifDown.getRxBytes());
                        trafficDto.setBytesTx(ifDown.getTxBytes());
                        speedMeasurementResult.getConnectionInfo().setAgentInterfaceDownloadMeasurementTraffic(trafficDto);
                    }

                    final CurrentInterfaceTraffic ifTotal = informationCollector.getAggregatedIfValues();
                    if (ifTotal != null) {
                        final TrafficDto trafficDto = new TrafficDto();
                        trafficDto.setBytesRx(ifTotal.getRxBytes());
                        trafficDto.setBytesTx(ifTotal.getTxBytes());
                        speedMeasurementResult.getConnectionInfo().setAgentInterfaceTotalTraffic(trafficDto);
                    }
                }

                Log.d(TAG, speedMeasurementResult.toString());
                addSubMeasurementResult(speedMeasurementResult);
            }
        });

        jniSpeedMeasurementClient.addMeasurementPhaseListener(new JniSpeedMeasurementClient.MeasurementPhaseListener() {
            @Override
            public void onMeasurementPhaseStarted(SpeedMeasurementState.MeasurementPhase startedPhase) {

            }

            @Override
            public void onMeasurementPhaseFinished(SpeedMeasurementState.MeasurementPhase finishedPhase) {
                switch (finishedPhase) {
                    case PING:
                        informationCollector.takeTrafficSnapshot(IF_TRAFFIC_LIST_PING_TAG);
                        break;
                    case UPLOAD:
                        informationCollector.takeTrafficSnapshot(IF_TRAFFIC_LIST_UPLOAD_TAG);
                        break;
                    case DOWNLOAD:
                        informationCollector.takeTrafficSnapshot(IF_TRAFFIC_LIST_DOWNLOAD_TAG);
                        break;
                }
            }
        });

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    jniSpeedMeasurementClient.startMeasurement();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (onMeasurementErrorListener != null) {
                        onMeasurementErrorListener.onMeasurementError(ex);
                    }
                }
            }
        });
    }

    public void stopSpeedMeasurement () {
        try {
            jniSpeedMeasurementClient.stopMeasurement();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    public void startQosMeasurement(final Bundle options) {
        //TODO: remove & replace
        this.bundle = options;
        followUpActions = (ArrayList<MeasurementType>) options.getSerializable(EXTRAS_KEY_FOLLOW_UP_ACTIONS);
        subMeasurementStartTimeNs = System.nanoTime();

        final List<TaskDesc> taskDescList = (List<TaskDesc>) options.getSerializable(EXTRAS_KEY_QOS_TASK_DESC_LIST);
        this.collectorUrl = options.getString(EXTRAS_KEY_QOS_TASK_COLLECTOR_URL);
        final ClientHolder client = ClientHolder.getInstance(taskDescList, collectorUrl);
        qosMeasurementClient = new QoSMeasurementClientAndroid(client, getApplicationContext());
        final List<QosMeasurementType> enabledTypeList = getQoSEnabledTypeList(getApplicationContext());
        Iterator<QosMeasurementType> it = enabledTypeList.iterator();
        while (it.hasNext()) {
            QosMeasurementType t = it.next();
            if (!FunctionalityHelper.isQoSTypeAvailable(t, getApplicationContext())) {
                it.remove();
            }
        }
        qosMeasurementClient.setEnabledTypes(enabledTypeList);
        qosMeasurementClient.addControlListener(new QoSMeasurementClientControlAdapter() {
            @Override
            public void onMeasurementError(Exception e) {
                if (onMeasurementErrorListener != null) {
                    onMeasurementErrorListener.onMeasurementError(e);
                }
            }
        });
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                qosMeasurementClient.run();
            }
        });

        t.start();
    }

    public void cancelMeasurement() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            //if this is the first action of the current total measurement start the listeners
            if (!isFollowUpAction.getAndSet(true)) {
                if (informationCollector != null) {
                    informationCollector.stop();
                }
                informationCollector = new InformationCollector(InformationProvider.createMeasurementDefault(getApplicationContext()));
                startDateTime = LocalDateTime.now(DateTimeZone.UTC);
                overallStartTimeNs = System.nanoTime();
                informationCollector.setStartTimeNs(overallStartTimeNs);
                informationCollector.start();
            }
            Log.d(TAG, "Got intent with action: '" + intent.getAction() + "'");
            if (ACTION_START_SPEED_MEASUREMENT.equals(intent.getAction())) {
                startMeasurement(intent.getExtras());
                return START_STICKY;
            }
            else if (ACTION_START_QOS_MEASUREMENT.equals(intent.getAction())) {
                startQosMeasurement(intent.getExtras());
                return START_STICKY;
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "Service Connected " + name);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "Service Disconnected " + name);
    }

    /**
     * sends the results of all executed sub-measurements
     * @param mainActivity
     * @return true if the task has been executed, false if the results have already been sent
     */
    public boolean sendResults(final MainActivity mainActivity, final AtomicBoolean sendingResults) {
        if (!sendingResults.getAndSet(true)) {
            endDateTime = LocalDateTime.now(DateTimeZone.UTC);
            //stop collecting information and allow new measurements to start as non-followupaction
            informationCollector.stop();
            isFollowUpAction.set(false);
            if (bundle.getString(EXTRAS_KEY_SPEED_TASK_CLIENT_IPV6_PUBLIC) != null) {
                informationCollector.setClientIpPublic(bundle.getString(EXTRAS_KEY_SPEED_TASK_CLIENT_IPV6_PUBLIC));
                informationCollector.setClientIpPrivate(bundle.getString(EXTRAS_KEY_SPEED_TASK_CLIENT_IPV6_PRIVATE));
            } else {
                informationCollector.setClientIpPublic(bundle.getString(EXTRAS_KEY_SPEED_TASK_CLIENT_IPV4_PUBLIC));
                informationCollector.setClientIpPrivate(bundle.getString(EXTRAS_KEY_SPEED_TASK_CLIENT_IPV4_PRIVATE));
            }
            final LmapReportDto reportDto = RequestUtil.prepareLmapReportForMeasurement(subMeasurementResultList, informationCollector, mainActivity);
            if (reportDto.getTimeBasedResult() == null) {
                reportDto.setTimeBasedResult(new TimeBasedResultDto());
            }
            reportDto.getTimeBasedResult().setStartTime(startDateTime);
            reportDto.getTimeBasedResult().setEndTime(endDateTime);

            final SendReportTask task = new SendReportTask(mainActivity,
                    reportDto,
                    this.collectorUrl, new OnTaskFinishedCallback<MeasurementResultResponse>() {
                @Override
                public void onTaskFinished(MeasurementResultResponse result) {
                    //if the result has been sent, reset the list of previous measurements
                    subMeasurementResultList.clear();

                    WorkflowRecentResultParameter parameter = null;

                    if (result == null) {
                        AlertDialogUtil.showAlertDialog(mainActivity,
                                R.string.alert_send_measurement_result_title,
                                R.string.alert_send_measurement_results_error);
                    } else {
                        if (jniSpeedMeasurementClient != null) {
                            jniSpeedMeasurementClient.getSpeedMeasurementState().setMeasurementUuid(result.getUuid());
                        } else {
                            //if no speed measurement has been executed, the corresponding result uuid needs be set via the workflowparameter
                            parameter = new WorkflowRecentResultParameter();
                            parameter.setRecentResultUuid(result.getUuid());
                            parameter.setRecentResultOpenDataUuid(result.getOpenDataUuid());
                        }
                    }

                    mainActivity.navigateTo(WorkflowTarget.MEASUREMENT_RECENT_RESULT, parameter);
                }
            });
            task.execute();
            return true;
        }
        return false;
    }

    /**
     *
     * @return true if a follow up action (e.g. QOS, SPEED) to the current measurement is desired
     */
    public boolean hasFollowUpAction () {
        return followUpActions != null && followUpActions.size() > 0;
    }

    /**
     * Starts the next follow up action (e.g. QOS, SPEED) from the MainActivity
     * passes along the previous bundle, but removes the currently executed action from the follow up actions
     * @param activity
     */
    public void executeFollowUpAction (final MainActivity activity) {
        if (!hasFollowUpAction()) {
            return;
        }
        final MeasurementType typeToExecute = followUpActions.get(0);
        followUpActions.remove(0);
        bundle.putSerializable(EXTRAS_KEY_FOLLOW_UP_ACTIONS, followUpActions);
        activity.startMeasurement(typeToExecute, bundle);
    }

    /**
     * Adds the given submeasurementresult to the list of executed submeasurements
     * additionally adds time information to the submeasurement results
     * @param result
     */
    public void addSubMeasurementResult(final SubMeasurementResult result) {
        result.setRelativeStartTimeNs(subMeasurementStartTimeNs - overallStartTimeNs);
        result.setRelativeEndTimeNs(System.nanoTime() - overallStartTimeNs);
        subMeasurementResultList.add(result);
    }

    private List<QosMeasurementType> getQoSEnabledTypeList(final Context context) {
        final List<QosMeasurementType> ret = new ArrayList<>();
        for (QosMeasurementType type : QosMeasurementType.values()) {
            if (PreferencesUtil.isQoSTypeEnabled(context, type)) {
                ret.add(type);
            }
        }
        return ret;
    }

    public void setOnMeasurementErrorListener(final OnMeasurementErrorListener listener) {
        this.onMeasurementErrorListener = listener;
    }

    public void removeOnMeasurementErrorListener() {
        this.onMeasurementErrorListener = null;
    }

    public interface OnMeasurementErrorListener {
        void onMeasurementError(final Exception ex);
    }

}
