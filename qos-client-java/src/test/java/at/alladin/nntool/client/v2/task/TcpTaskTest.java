package at.alladin.nntool.client.v2.task;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import at.alladin.nntool.client.ClientHolder;
import at.alladin.nntool.client.QualityOfServiceTest;
import at.alladin.nntool.client.v2.task.result.QoSTestResult;
import at.alladin.nntool.client.v2.task.service.TestSettings;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;

import static org.junit.Assert.assertEquals;

/**
 * @author Felix Kendlbacher (fk@alladin.at)
 */
public class TcpTaskTest {

    private InetAddress loopback;

    private TestSettings testSettings;

    private ClientHolder clientHolder;

    private QualityOfServiceTest qosTest;

    private OutputStream testOutputStream;

    private TaskDesc incomingTaskDesc;

    @Before
    public void init () {
        loopback = InetAddress.getLoopbackAddress();

        testSettings = new TestSettings();
        testSettings.setUseSsl(false);
        testSettings.setStartTimeNs(12);
        clientHolder = ClientHolder.getInstance("host", "80",
                new int[] {80}, null, "host", null, null);

        qosTest = new QualityOfServiceTest(clientHolder, testSettings);

        testOutputStream = new ByteArrayOutputStream();

        // provide params for incoming tcp test
        final Map<String, Object> params = new HashMap<>();
        params.put(TcpTask.PARAM_PORT_IN, "80");
        params.put(AbstractQoSTask.PARAM_QOS_TEST_OBJECTIVE_ID, 1);
        params.put(AbstractQoSTask.PARAM_QOS_CONCURRENCY_GROUP, 200);


        incomingTaskDesc = new TaskDesc("host", 80, false, "token", 80,
                1, 1, 1, params);
    }

    /**
     * Outgoing TCP tests
     */

    @Test
    public void testTcpOutgoingSuccessful (@Mocked final QoSControlConnection controlConnection,
                                 @Mocked final Socket socket) throws Exception {

        new Expectations() {{

            controlConnection.sendTaskCommand((AbstractQoSTask) any, withPrefix("TCPTEST OUT"), (ControlConnectionResponseCallback) any);
            result = new Delegate () {
                public void delegateMethod (AbstractQoSTask qoSTask, String cmd, ControlConnectionResponseCallback callback) {
                    callback.onResponse("OK", cmd);
                }
            };

            socket.getInputStream();
            result = new ByteArrayInputStream("PING".getBytes());

            socket.getOutputStream();
            result = testOutputStream;

        }};

        final TcpTask tcpTask = new TcpTask(qosTest, clientHolder.getTaskDescList().get(0), 0);
        tcpTask.setControlConnection(controlConnection);

        final QoSTestResult res = tcpTask.call();

        assertEquals("Result did not return OK","OK", res.getResultMap().get(TcpTask.RESULT_OUT));
        assertEquals("Payload sent to server != PING", "PING\n", testOutputStream.toString());
        assertEquals("Incorrect received result", "PING", res.getResultMap().get(TcpTask.RESULT_RESPONSE_OUT));
        assertEquals("Incorrect test out port", 80, res.getResultMap().get(TcpTask.RESULT_PORT_OUT));

    }

    @Test
    public void testTcpOutgoingControllerSendsError (@Mocked final QoSControlConnection controlConnection) throws Exception {

        new Expectations() {{

            controlConnection.sendTaskCommand((AbstractQoSTask) any, withPrefix("TCPTEST OUT"), (ControlConnectionResponseCallback) any);
            result = new Delegate () {
                public void delegateMethod (AbstractQoSTask qoSTask, String cmd, ControlConnectionResponseCallback callback) {
                    callback.onResponse("ERROR", cmd);
                }
            };

        }};

        final TcpTask tcpTask = new TcpTask(qosTest, clientHolder.getTaskDescList().get(0), 0);
        tcpTask.setControlConnection(controlConnection);

        final QoSTestResult res = tcpTask.call();

        assertEquals("Result did not return ERROR","ERROR", res.getResultMap().get(TcpTask.RESULT_OUT));
        assertEquals("Incorrect test out port", 80, res.getResultMap().get(TcpTask.RESULT_PORT_OUT));

    }


    @Test
    public void testTcpOutgoingControllerSendsNull (@Mocked final InetAddress inetAddress, @Mocked final QoSControlConnection controlConnection,
                                                     @Mocked final Socket socket) throws Exception {

        new Expectations() {{

            controlConnection.sendTaskCommand((AbstractQoSTask) any, withPrefix("TCPTEST OUT"), (ControlConnectionResponseCallback) any);
            result = new Delegate () {
                public void delegateMethod (AbstractQoSTask qoSTask, String cmd, ControlConnectionResponseCallback callback) {
                    callback.onResponse(null, cmd);
                }
            };

        }};

        final TcpTask tcpTask = new TcpTask(qosTest, clientHolder.getTaskDescList().get(0), 0);
        tcpTask.setControlConnection(controlConnection);

        final QoSTestResult res = tcpTask.call();

        assertEquals("Result did not return ERROR","ERROR", res.getResultMap().get(TcpTask.RESULT_OUT));
        assertEquals("Incorrect test out port", 80, res.getResultMap().get(TcpTask.RESULT_PORT_OUT));

    }

    @Test
    public void testTcpOutgoingTimeout (@Mocked final QoSControlConnection controlConnection,
                                        @Mocked final Socket socket) throws Exception {

        new Expectations() {{

            controlConnection.sendTaskCommand((AbstractQoSTask) any, withPrefix("TCPTEST OUT"), (ControlConnectionResponseCallback) any);
            result = new Delegate () {
                public void delegateMethod (AbstractQoSTask qoSTask, String cmd, ControlConnectionResponseCallback callback) {
                    callback.onResponse("OK", cmd);
                }
            };

            socket.getInputStream();
            result = new SocketTimeoutException("Forcefully thrown exception");

            socket.getOutputStream();
            result = testOutputStream;

        }};

        final TcpTask tcpTask = new TcpTask(qosTest, clientHolder.getTaskDescList().get(0), 0);
        tcpTask.setControlConnection(controlConnection);

        final QoSTestResult res = tcpTask.call();

        assertEquals("Result did not return TIMEOUT","TIMEOUT", res.getResultMap().get(TcpTask.RESULT_OUT));
        assertEquals("Payload sent to server != PING", "PING\n", testOutputStream.toString());
        assertEquals("Incorrect test out port", 80, res.getResultMap().get(TcpTask.RESULT_PORT_OUT));
    }

    @Test
    public void testTcpOutgoingConnectionError (@Mocked final QoSControlConnection controlConnection,
                                        @Mocked final Socket socket) throws Exception {

        new Expectations() {{

            controlConnection.sendTaskCommand((AbstractQoSTask) any, withPrefix("TCPTEST OUT"), (ControlConnectionResponseCallback) any);
            result = new Delegate () {
                public void delegateMethod (AbstractQoSTask qoSTask, String cmd, ControlConnectionResponseCallback callback) {
                    callback.onResponse("OK", cmd);
                }
            };

            socket.getInputStream();
            result = new ConnectException("Forcefully thrown exception");

        }};

        final TcpTask tcpTask = new TcpTask(qosTest, clientHolder.getTaskDescList().get(0), 0);
        tcpTask.setControlConnection(controlConnection);

        final QoSTestResult res = tcpTask.call();

        assertEquals("Result did not return ERROR","ERROR", res.getResultMap().get(TcpTask.RESULT_OUT));
        assertEquals("Incorrect test out port", 80, res.getResultMap().get(TcpTask.RESULT_PORT_OUT));
    }

    /**
     * Incoming TCP tests
     */

    @Test
    public void testTcpIncomingSuccessful (@Mocked final QoSControlConnection controlConnection,
                                           @Mocked final ServerSocket serverSocket, @Mocked final Socket socket) throws Exception {

        new Expectations() {{

            //make sure the TCPTEST IN 80 cmd is sent exactly once
            controlConnection.sendTaskCommand((AbstractQoSTask) any, "TCPTEST IN 80", (ControlConnectionResponseCallback) any);
            times = 1;

            socket.getInputStream();
            result = new ByteArrayInputStream("PING".getBytes());

        }};

        final TcpTask tcpTask = new TcpTask(qosTest, incomingTaskDesc, 0);
        tcpTask.setControlConnection(controlConnection);
        final QoSTestResult res = tcpTask.call();

        assertEquals("Result did not return OK","OK", res.getResultMap().get(TcpTask.RESULT_IN));
        assertEquals("Incorrect received result", "PING", res.getResultMap().get(TcpTask.RESULT_RESPONSE_IN));
        assertEquals("Incorrect test in port", 80, res.getResultMap().get(TcpTask.RESULT_PORT_IN));
    }

    @Test
    public void testTcpIncomingTimeout (@Mocked final QoSControlConnection controlConnection,
                                        @Mocked final ServerSocket serverSocket, @Mocked final Socket socket) throws Exception {

        new Expectations() {{

            //make sure the TCPTEST IN 80 cmd is sent exactly once
            controlConnection.sendTaskCommand((AbstractQoSTask) any, "TCPTEST IN 80", (ControlConnectionResponseCallback) any);
            times = 1;

            socket.getInputStream();
            result = new SocketTimeoutException("Forcefully thrown exception");

        }};

        final TcpTask tcpTask = new TcpTask(qosTest, incomingTaskDesc, 0);
        tcpTask.setControlConnection(controlConnection);
        final QoSTestResult res = tcpTask.call();

        assertEquals("Result did not return TIMEOUT","TIMEOUT", res.getResultMap().get(TcpTask.RESULT_IN));
        assertEquals("Incorrect test in port", 80, res.getResultMap().get(TcpTask.RESULT_PORT_IN));
    }

    @Test
    public void testTcpIncomingError (@Mocked final QoSControlConnection controlConnection,
                                        @Mocked final ServerSocket serverSocket, @Mocked final Socket socket) throws Exception {

        new Expectations() {{

            //make sure the TCPTEST IN 80 cmd is sent exactly once
            controlConnection.sendTaskCommand((AbstractQoSTask) any, "TCPTEST IN 80", (ControlConnectionResponseCallback) any);
            times = 1;

            serverSocket.accept();
            result = new SocketException("Forcefully thrown exception");

        }};

        final TcpTask tcpTask = new TcpTask(qosTest, incomingTaskDesc, 0);
        tcpTask.setControlConnection(controlConnection);
        final QoSTestResult res = tcpTask.call();

        assertEquals("Result did not return ERROR","ERROR", res.getResultMap().get(TcpTask.RESULT_IN));
        assertEquals("Incorrect test in port", 80, res.getResultMap().get(TcpTask.RESULT_PORT_IN));
    }
}
