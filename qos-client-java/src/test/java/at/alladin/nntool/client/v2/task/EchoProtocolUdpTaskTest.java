package at.alladin.nntool.client.v2.task;

import org.junit.Before;
import org.junit.Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import at.alladin.nntool.client.ClientHolder;
import at.alladin.nntool.client.QualityOfServiceTest;
import at.alladin.nntool.client.helper.TaskDescriptionHelper;
import at.alladin.nntool.client.v2.task.result.QoSTestResult;
import at.alladin.nntool.client.v2.task.service.TestSettings;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;

import static org.junit.Assert.assertEquals;

/**
 * @author Felix Kendlbacher (fk@alladin.at)
 */
public class EchoProtocolUdpTaskTest {

    private static final String CORRECT_MESSAGE = "UDP payload!";

    private static final String WRONG_MESSAGE_ADDITIONAL_WHITESPACE = "UDP payload! ";

    private static final String WRONG_MESSAGE = "TCP payload!";

    private static final String ECHO_SERVICE_HOST = "echo.host";

    private QualityOfServiceTest qosTest;

    private TestSettings testSettings;

    private ClientHolder clientHolder;

    private DatagramPacket sendPacket;

    private InetAddress toReturn;

    @Before
    public void init() throws Exception {

        testSettings = new TestSettings();
        testSettings.setUseSsl(false);
        testSettings.setStartTimeNs(12);
        clientHolder = ClientHolder.getInstance(TaskDescriptionHelper.createTaskDescList(ECHO_SERVICE_HOST, "80",
                null, null, ECHO_SERVICE_HOST, new int[0], new int[]{76}), null);

        qosTest = new QualityOfServiceTest(clientHolder, testSettings);

        toReturn = InetAddress.getLoopbackAddress();
    }

    @Test
    public void testCorrectUdpEchoResponse(@Mocked final DatagramSocket mock, @Mocked final InetAddress inetAddress) throws Exception {

        new Expectations() {{

            // spoof the internet address (as we WILL get an UnknownHostException otherwise)
            InetAddress.getByName(anyString);
            result = toReturn;  //return loopback address

            // check the sent data for correctness, when send is called
            mock.send((DatagramPacket) any);
            result = new Delegate() {
                void delegateMethod(DatagramPacket packet) {
                    sendPacket = packet;
                }
            };

            //return the correct message
            mock.receive((DatagramPacket) any);
            result = new Delegate() {
                void delegateMethod(DatagramPacket packet) {
                    packet.setData(CORRECT_MESSAGE.getBytes());
                }
            };

        }};

        final EchoProtocolUdpTask task = new EchoProtocolUdpTask(qosTest, clientHolder.getTaskDescList().get(0), 0);
        final QoSTestResult res = task.call();

        assertEquals("Payload sent to server != " + CORRECT_MESSAGE, CORRECT_MESSAGE, new String(sendPacket.getData()));
        assertEquals("Result did not return OK","OK", res.getResultMap().get(AbstractEchoProtocolTask.RESULT_STATUS));
        assertEquals("Wrong echo service host in result", ECHO_SERVICE_HOST, res.getResultMap().get("echo_protocol_objective_host"));
        assertEquals("Wrong protocol type in result", AbstractEchoProtocolTask.PROTOCOL_UDP, res.getResultMap().get(AbstractEchoProtocolTask.RESULT_PROTOCOL));
        assertEquals("Payload stored in result !=" + CORRECT_MESSAGE, CORRECT_MESSAGE, res.getResultMap().get(AbstractEchoProtocolTask.RESULT));

    }

    @Test
    public void testWrongUdpEchoResponse (@Mocked final DatagramSocket mock, @Mocked final InetAddress inetAddress) throws Exception {

        new Expectations() {{

            // spoof the internet address (as we WILL get an UnknownHostException otherwise)
            InetAddress.getByName(anyString);
            result = toReturn;  //return loopback address

            // check the sent data for correctness, when send is called
            mock.send((DatagramPacket) any);
            result = new Delegate() {
                void delegateMethod(DatagramPacket packet) {
                    sendPacket = packet;
                }
            };

            //return the correct message
            mock.receive((DatagramPacket) any);
            result = new Delegate() {
                void delegateMethod(DatagramPacket packet) {
                    packet.setData(WRONG_MESSAGE.getBytes());
                }
            };

        }};

        final EchoProtocolUdpTask task = new EchoProtocolUdpTask(qosTest, clientHolder.getTaskDescList().get(0), 0);
        final QoSTestResult res = task.call();

        assertEquals("Payload sent to server != " + CORRECT_MESSAGE, CORRECT_MESSAGE, new String(sendPacket.getData()));
        assertEquals("Result did not return ERROR","ERROR", res.getResultMap().get(AbstractEchoProtocolTask.RESULT_STATUS));
        assertEquals("Wrong echo service host in result", ECHO_SERVICE_HOST, res.getResultMap().get("echo_protocol_objective_host"));
        assertEquals("Wrong protocol type in result", AbstractEchoProtocolTask.PROTOCOL_UDP, res.getResultMap().get(AbstractEchoProtocolTask.RESULT_PROTOCOL));
        assertEquals("Payload stored in result !=" + WRONG_MESSAGE, WRONG_MESSAGE, res.getResultMap().get(AbstractEchoProtocolTask.RESULT));
    }

    @Test
    public void testCorrectUdpEchoResponseWithAdditionalWhiteSpace (@Mocked final DatagramSocket mock, @Mocked final InetAddress inetAddress) throws Exception {

        new Expectations() {{

            // spoof the internet address (as we WILL get an UnknownHostException otherwise)
            InetAddress.getByName(anyString);
            result = toReturn;  //return loopback address

            // check the sent data for correctness, when send is called
            mock.send((DatagramPacket) any);
            result = new Delegate() {
                void delegateMethod(DatagramPacket packet) {
                    sendPacket = packet;
                }
            };

            //return the correct message
            mock.receive((DatagramPacket) any);
            result = new Delegate() {
                void delegateMethod(DatagramPacket packet) {
                    packet.setData(WRONG_MESSAGE_ADDITIONAL_WHITESPACE.getBytes());
                }
            };

        }};

        final EchoProtocolUdpTask task = new EchoProtocolUdpTask(qosTest, clientHolder.getTaskDescList().get(0), 0);
        final QoSTestResult res = task.call();

        assertEquals("Payload sent to server != " + CORRECT_MESSAGE, CORRECT_MESSAGE, new String(sendPacket.getData()));
        assertEquals("Result did not return ERROR","ERROR", res.getResultMap().get(AbstractEchoProtocolTask.RESULT_STATUS));
        assertEquals("Wrong echo service host in result", ECHO_SERVICE_HOST, res.getResultMap().get("echo_protocol_objective_host"));
        assertEquals("Wrong protocol type in result", AbstractEchoProtocolTask.PROTOCOL_UDP, res.getResultMap().get(AbstractEchoProtocolTask.RESULT_PROTOCOL));
        assertEquals("Payload stored in result !=" + WRONG_MESSAGE_ADDITIONAL_WHITESPACE, WRONG_MESSAGE_ADDITIONAL_WHITESPACE, res.getResultMap().get(AbstractEchoProtocolTask.RESULT));
    }

    @Test
    public void testUdpEchoResponseWithSocketError (@Mocked final DatagramSocket mock, @Mocked final InetAddress inetAddress) throws Exception {

        new Expectations() {{

            // spoof the internet address (as we WILL get an UnknownHostException otherwise)
            InetAddress.getByName(anyString);
            result = toReturn;  //return loopback address

            // check the sent data for correctness, when send is called
            mock.send((DatagramPacket) any);
            result = result = new SocketException("Forcefully thrown exception");

        }};

        final EchoProtocolUdpTask task = new EchoProtocolUdpTask(qosTest, clientHolder.getTaskDescList().get(0), 0);
        final QoSTestResult res = task.call();

        assertEquals("Result did not return ERROR","ERROR", res.getResultMap().get(AbstractEchoProtocolTask.RESULT_STATUS));
        assertEquals("Wrong echo service host in result", ECHO_SERVICE_HOST, res.getResultMap().get("echo_protocol_objective_host"));
        assertEquals("Wrong protocol type in result", AbstractEchoProtocolTask.PROTOCOL_UDP, res.getResultMap().get(AbstractEchoProtocolTask.RESULT_PROTOCOL));
    }

    @Test
    public void testUdpEchoResponseWithSocketTimeoutError (@Mocked final DatagramSocket mock, @Mocked final InetAddress inetAddress) throws Exception {

        new Expectations() {{

            // spoof the internet address (as we WILL get an UnknownHostException otherwise)
            InetAddress.getByName(anyString);
            result = toReturn;  //return loopback address

            // check the sent data for correctness, when send is called
            mock.send((DatagramPacket) any);
            result = new Delegate() {
                void delegateMethod(DatagramPacket packet) {
                    sendPacket = packet;
                }
            };

            //return the correct message
            mock.receive((DatagramPacket) any);
            result = new SocketTimeoutException("Forcefully thrown exception");

        }};

        final EchoProtocolUdpTask task = new EchoProtocolUdpTask(qosTest, clientHolder.getTaskDescList().get(0), 0);
        final QoSTestResult res = task.call();

        assertEquals("Result did not return TIMEOUT","TIMEOUT", res.getResultMap().get(AbstractEchoProtocolTask.RESULT_STATUS));
        assertEquals("Wrong echo service host in result", ECHO_SERVICE_HOST, res.getResultMap().get("echo_protocol_objective_host"));
        assertEquals("Wrong protocol type in result", AbstractEchoProtocolTask.PROTOCOL_UDP, res.getResultMap().get(AbstractEchoProtocolTask.RESULT_PROTOCOL));
    }

}
