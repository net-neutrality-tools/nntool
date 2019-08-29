package at.alladin.nntool.client.v2.task;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import at.alladin.nettest.shared.helper.GsonBasicHelper;
import at.alladin.nntool.client.ClientHolder;
import at.alladin.nntool.client.QualityOfServiceTest;
import at.alladin.nntool.client.v2.task.result.QoSTestResult;
import at.alladin.nntool.client.v2.task.result.QoSTestResultEnum;
import at.alladin.nntool.client.v2.task.service.MkitService;
import at.alladin.nntool.client.v2.task.service.TestSettings;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class MkitTask extends AbstractQoSTask {


    public final static long DEFAULT_TIMEOUT = 30000000000L;

    public final static Gson gson = GsonBasicHelper.getDateTimeGsonBuilder().create();

    private final long timeout;

    private final List<String> inputArray;

    private final JsonArray flagArray;

    private MkitService.MkitTestEnum mkitTestEnum;

    private QoSTestResultEnum qosTestResultEnum;

    public final static String PARAM_TIMEOUT = "timeout";

    public final static String PARAM_NDT_TEST = "ndt_test";

    public final static String PARAM_INPUT = "input";

    public final static String PARAM_FLAGS = "flags";

    public final static String RESULT_DETAILS = "mkit_result";

    public final static String RESULT_STATUS = "mkit_status";

    public MkitTask (final QualityOfServiceTest qosTest, final String taskId, final TaskDesc taskDesc, final int threadId) {
        super(qosTest, taskDesc, threadId, threadId);

        //String value = (String) taskDesc.getParams().get(PARAM_TIMEOUT);
        //this.timeout = value != null ? Long.valueOf(value) : DEFAULT_TIMEOUT;
        this.timeout = DEFAULT_TIMEOUT;

        try {
            this.mkitTestEnum = MkitService.MkitTestEnum.valueOf(taskId.toUpperCase());
            this.qosTestResultEnum = QoSTestResultEnum.valueOf(taskId.toUpperCase());
        } catch (IllegalArgumentException ex) {
            System.out.println("Invalid test enum: " + mkitTestEnum.name());
            this.mkitTestEnum = null;
        }

        Object inputObj = taskDesc.getParams().get(PARAM_INPUT);
        if (inputObj instanceof List) {
            inputArray = (List) inputObj;
        } else if (inputObj instanceof String) {
            inputArray = gson.fromJson((String) inputObj, ArrayList.class);
        } else {
            inputArray = new ArrayList<>();
        }

        flagArray = new JsonArray();//gson.fromJson((String) taskDesc.getParams().get(PARAM_FLAGS), JsonArray.class);
    }

    public QoSTestResult call() throws Exception {
        final QoSTestResult testResult = initQoSTestResult(qosTestResultEnum);
        if (mkitTestEnum == null) {
            testResult.setFatalError(true);
            return testResult;
        }

        try {
            onStart(testResult);

            final TestSettings settings = getQoSTest().getTestSettings();

            final MkitService mkitService = settings.getMkitServiceClazz().newInstance();
            try {
                mkitService.setTestToExecute(mkitTestEnum);
                if (inputArray != null) {
                    for (String input : inputArray) {
                        mkitService.addInput(input);
                    }
                }
                if (flagArray != null) {
                    for (JsonElement elem : flagArray) {
                        mkitService.addFlags(elem.getAsString());
                    }
                }
            } catch (MkitService.UnsupportedMkitTestException ex) {
                System.out.println(ex.getMessage());
                testResult.setFatalError(true);
                return testResult;
            }

            mkitService.setOnMkitTestProgressListener(new MkitService.MkitTestProgressListener() {
                @Override
                public void onProgress(float progress) {
                    if (MkitTask.this.listener != null) {
                        MkitTask.this.listener.onProgress(progress, qosTestResultEnum);
                    }
                }
            });

            MkitService.MkitResult result = null;

            final Future<MkitService.MkitResult> ndtFuture = ClientHolder.getCommonThreadPool().submit(mkitService);
            try {
                result = ndtFuture.get(timeout, TimeUnit.NANOSECONDS);
                testResult.getResultMap().put(RESULT_STATUS, "OK");
            }
            catch (TimeoutException e) {
                testResult.getResultMap().put(RESULT_STATUS, "TIMEOUT");
            }
            finally {
                if (result != null) {
                    testResult.getResultMap().put(RESULT_DETAILS, result.toString());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            testResult.getResultMap().put(RESULT_STATUS, "ERROR");
        }
        finally {
            onEnd(testResult);
        }

        return testResult;
    }

    /*
     * (non-Javadoc)
     * @see at.alladin.rmbt.client.v2.task.AbstractRmbtTask#initTask()
     */
    @Override
    public void initTask() {
    }

    /*
     * (non-Javadoc)
     * @see at.alladin.rmbt.client.v2.task.QoSTask#getTestType()
     */
    public QoSTestResultEnum getTestType() {
        return qosTestResultEnum;
    }

    /*
     * (non-Javadoc)
     * @see at.alladin.rmbt.client.v2.task.QoSTask#needsQoSControlConnection()
     */
    public boolean needsQoSControlConnection() {
        return false;
    }
}
