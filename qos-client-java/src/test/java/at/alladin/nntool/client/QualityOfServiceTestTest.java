/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nntool.client;

import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.alladin.nettest.qos.QoSMeasurementClientProgressListener;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.nntool.client.helper.TaskDescriptionHelper;
import at.alladin.nntool.client.v2.task.AbstractEchoProtocolTask;
import at.alladin.nntool.client.v2.task.DnsTask;
import at.alladin.nntool.client.v2.task.EchoProtocolTcpTask;
import at.alladin.nntool.client.v2.task.EchoProtocolUdpTask;
import at.alladin.nntool.client.v2.task.HttpProxyTask;
import at.alladin.nntool.client.v2.task.NonTransparentProxyTask;
import at.alladin.nntool.client.v2.task.QoSControlConnection;
import at.alladin.nntool.client.v2.task.QoSTestEnum;
import at.alladin.nntool.client.v2.task.TaskDesc;
import at.alladin.nntool.client.v2.task.TcpTask;
import at.alladin.nntool.client.v2.task.TracerouteTask;
import at.alladin.nntool.client.v2.task.UdpTask;
import at.alladin.nntool.client.v2.task.VoipTask;
import at.alladin.nntool.client.v2.task.WebsiteTask;
import at.alladin.nntool.client.v2.task.result.QoSResultCollector;
import at.alladin.nntool.client.v2.task.result.QoSTestResult;
import at.alladin.nntool.client.v2.task.service.TestSettings;
import at.alladin.nntool.client.v2.task.service.TrafficService;
import at.alladin.nntool.client.v2.task.service.WebsiteTestService;
import at.alladin.nntool.util.tools.TracerouteService;
import mockit.Expectations;
import mockit.Mocked;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Felix Kendlbacher (fk@alladin.at)
 */
public class QualityOfServiceTestTest {

    private ClientHolder clientHolder;

    private TestSettings testSettings;

    private QualityOfServiceTest qosTest;

    private TaskDesc httpProxyTaskDesc, nonTransparentProxyTaskDesc, dnsTaskDesc, tcpTaskDesc, udpTaskDesc,
            voipTaskDesc, tracerouteTaskDesc, websiteTaskDesc, echoTcpTaskDesc,
            echoUdpTaskDesc, echoUnknownTaskDesc, echoNoProtocolTaskDesc;

    @Mocked
    private DnsTask dnsTask;

    @Mocked
    private TcpTask tcpTask;

    @Mocked
    private UdpTask udpTask;

    @Mocked
    private VoipTask voipTask;

    @Mocked
    private TracerouteTask tracerouteTask;

    @Mocked
    private WebsiteTask websiteTask;

    @Mocked
    private EchoProtocolTcpTask echoProtocolTcpTask;

    @Mocked
    private EchoProtocolUdpTask echoProtocolUdpTask;

    @Mocked
    private HttpProxyTask httpProxyTask;

    @Mocked
    private NonTransparentProxyTask nonTransparentProxyTask;

    @Mocked
    private QoSControlConnection controlConnection;

    @Mocked
    private WebsiteTestService websiteTestService;

    @Before
    public void init () {
        clientHolder = ClientHolder.getInstance(TaskDescriptionHelper.createTaskDescList("host", "80",
                null, null, "host", null, null), null);
        testSettings = new TestSettings();

        final List<InetAddress> dnsServerAddressList = new ArrayList<>();
        dnsServerAddressList.add(InetAddress.getLoopbackAddress());
        testSettings.setUseSsl(false);
        testSettings.setDnsServerAddressList(dnsServerAddressList);
        testSettings.setTracerouteServiceClazz(TracerouteService.class);
        testSettings.setWebsiteTestService(websiteTestService);


        httpProxyTaskDesc = new TaskDesc("host", 80, false, "token", 1, 1, 1, 1, new HashMap<String, Object>(), ClientHolder.TASK_HTTP);
        nonTransparentProxyTaskDesc = new TaskDesc("host", 80, false, "token", 1, 1, 1, 1, new HashMap<String, Object>(), ClientHolder.TASK_NON_TRANSPARENT_PROXY);
        dnsTaskDesc = new TaskDesc("host", 80, false, "token", 1, 1, 1, 1, new HashMap<String, Object>(), ClientHolder.TASK_DNS);
        tcpTaskDesc = new TaskDesc("host", 80, false, "token", 1, 1, 1, 1, new HashMap<String, Object>(), ClientHolder.TASK_TCP);
        udpTaskDesc = new TaskDesc("host", 80, false, "token", 1, 1, 1, 1, new HashMap<String, Object>(), ClientHolder.TASK_UDP);
        voipTaskDesc = new TaskDesc("host", 80, false, "token", 1, 1, 1, 1, new HashMap<String, Object>(), ClientHolder.TASK_VOIP);
        tracerouteTaskDesc = new TaskDesc("host", 80, false, "token", 1, 1, 1, 1, new HashMap<String, Object>(), ClientHolder.TASK_TRACEROUTE);
        websiteTaskDesc = new TaskDesc("host", 80, false, "token", 1, 1, 1, 1, new HashMap<String, Object>(), ClientHolder.TASK_WEBSITE);

        Map<String, Object> echoParams = new HashMap<>();
        echoParams.put(AbstractEchoProtocolTask.PROTOCOL, AbstractEchoProtocolTask.PROTOCOL_TCP);
        echoTcpTaskDesc = new TaskDesc("host", 80, false, "token", 1, 1, 1, 1, echoParams, ClientHolder.TASK_ECHO_PROTOCOL);
        echoParams = new HashMap<>();
        echoParams.put(AbstractEchoProtocolTask.PROTOCOL, AbstractEchoProtocolTask.PROTOCOL_UDP);
        echoUdpTaskDesc = new TaskDesc("host", 80, false, "token", 1, 1, 1, 1, echoParams, ClientHolder.TASK_ECHO_PROTOCOL);
        echoParams = new HashMap<>();
        echoParams.put(AbstractEchoProtocolTask.PROTOCOL, "Not an echo protocol");
        echoUnknownTaskDesc = new TaskDesc("host", 80, false, "token", 1, 1, 1, 1, echoParams, ClientHolder.TASK_ECHO_PROTOCOL);
        echoNoProtocolTaskDesc = new TaskDesc("host", 80, false, "token", 1, 1, 1, 1, new HashMap<String, Object>(), ClientHolder.TASK_ECHO_PROTOCOL);

    }

    @Test
    public void basicParsingOfTaskListTest () throws Exception {

        // get every test type to return the correct taskDesc
        new Expectations() {{

            httpProxyTask.getTestType();
            result = QosMeasurementType.HTTP_PROXY;
            httpProxyTask.getTestServerAddr();
            minTimes = 0;
            result = "test_server_address";
            httpProxyTask.getTaskDesc();
            result = httpProxyTaskDesc;

            nonTransparentProxyTask.getTestType();
            result = QosMeasurementType.NON_TRANSPARENT_PROXY;
            nonTransparentProxyTask.getTestServerAddr();
            minTimes = 0;
            result = "other_test_server_address";
            nonTransparentProxyTask.getTaskDesc();
            result = nonTransparentProxyTaskDesc;


            dnsTask.getTestType();
            result = QosMeasurementType.DNS;
            dnsTask.getTaskDesc();
            minTimes = 0;
            result = dnsTaskDesc;
            dnsTask.getTestServerAddr();
            minTimes = 0;
            result = "test_server_address";


            tcpTask.getTestType();
            result = QosMeasurementType.TCP;
            tcpTask.getTaskDesc();
            minTimes = 0;
            result = tcpTaskDesc;
            tcpTask.getTestServerAddr();
            minTimes = 0;
            result = "third_test_server_address";

            udpTask.getTestType();
            result = QosMeasurementType.UDP;
            udpTask.getTaskDesc();
            minTimes = 0;
            result = udpTaskDesc;
            udpTask.getTestServerAddr();
            minTimes = 0;
            result = "test_server_address";

            voipTask.getTestType();
            result = QosMeasurementType.VOIP;
            voipTask.getTaskDesc();
            minTimes = 0;
            result = voipTaskDesc;
            voipTask.getTestServerAddr();
            minTimes = 0;
            result = "test_server_address";

            tracerouteTask.getTestType();
            result = QosMeasurementType.TRACEROUTE;
            tracerouteTask.getTaskDesc();
            minTimes = 0;
            result = tracerouteTaskDesc;
            tracerouteTask.getTestServerAddr();
            minTimes = 0;
            result = "other_test_server_address";

            websiteTask.getTestType();
            result = QosMeasurementType.WEBSITE;
            websiteTask.getTaskDesc();
            minTimes = 0;
            result = websiteTaskDesc;
            websiteTask.getTestServerAddr();
            minTimes = 0;
            result = "test_server_address";

            echoProtocolTcpTask.getTestType();
            result = QosMeasurementType.ECHO_PROTOCOL;
            echoProtocolTcpTask.getTaskDesc();
            minTimes = 0;
            result = echoTcpTaskDesc;
            echoProtocolTcpTask.getTestServerAddr();
            minTimes = 0;
            result = "test_server_address";

            echoProtocolUdpTask.getTestType();
            result = QosMeasurementType.ECHO_PROTOCOL;
            echoProtocolUdpTask.getTaskDesc();
            minTimes = 0;
            result = echoUdpTaskDesc;
            echoProtocolUdpTask.getTestServerAddr();
            minTimes = 0;
            result = "test_server_address";

            httpProxyTask.getTestType();
            result = QosMeasurementType.HTTP_PROXY;
            httpProxyTask.getTaskDesc();
            minTimes = 0;
            result = httpProxyTaskDesc;
            httpProxyTask.getTestServerAddr();
            minTimes = 0;
            result = "third_test_server_address";

            nonTransparentProxyTask.getTestType();
            result = QosMeasurementType.NON_TRANSPARENT_PROXY;
            nonTransparentProxyTask.getTaskDesc();
            minTimes = 0;
            result = nonTransparentProxyTaskDesc;
            nonTransparentProxyTask.getTestServerAddr();
            minTimes = 0;
            result = "test_server_address";
        }};


        clientHolder.getTaskDescList().add(httpProxyTaskDesc);
        clientHolder.getTaskDescList().add(dnsTaskDesc);
        clientHolder.getTaskDescList().add(tcpTaskDesc);
        clientHolder.getTaskDescList().add(udpTaskDesc);
        clientHolder.getTaskDescList().add(voipTaskDesc);
        clientHolder.getTaskDescList().add(tracerouteTaskDesc);
        clientHolder.getTaskDescList().add(websiteTaskDesc);
        clientHolder.getTaskDescList().add(echoTcpTaskDesc);
        clientHolder.getTaskDescList().add(echoUdpTaskDesc);
        clientHolder.getTaskDescList().add(httpProxyTaskDesc);
        clientHolder.getTaskDescList().add(nonTransparentProxyTaskDesc);

        qosTest = new QualityOfServiceTest(clientHolder, testSettings);

        assertEquals("Unexpected qosTestMap size", 9, qosTest.getTestMap().size());
        assertEquals("The two Http proxy tests have not been registered correctly", 2, qosTest.getTestMap().get(QosMeasurementType.HTTP_PROXY).size());
        assertEquals("The two echo protocol tests have not been registered correctly", 2, qosTest.getTestMap().get(QosMeasurementType.ECHO_PROTOCOL).size());
    }

    @Test
    public void invalidQosTypeIsNotAddedTest () throws Exception {

        new Expectations() {{
            httpProxyTask.getTestType();
            result = QosMeasurementType.HTTP_PROXY;
            httpProxyTask.getTestServerAddr();
            minTimes = 0;
            result = "test_server_address";
            httpProxyTask.getTaskDesc();
            minTimes = 0;
            result = httpProxyTaskDesc;

        }};

        final String invalidQosId = "INVALID_QOS_TEST_ID";

        final TaskDesc invalidDesc = new TaskDesc("host", 80, false, "token", 1, 1, 1, 1, new HashMap<String, Object>(), invalidQosId);
        clientHolder.getTaskDescList().add(invalidDesc);
        clientHolder.getTaskDescList().add(httpProxyTaskDesc);

        qosTest = new QualityOfServiceTest(clientHolder, testSettings);

        assertEquals("Unexpected qosTestMap size", 1, qosTest.getTestMap().size());
        assertEquals("The two Http proxy tests have not been registered correctly", 1, qosTest.getTestMap().get(QosMeasurementType.HTTP_PROXY).size());
    }

    @Test
    public void noTracerouteTestWithoutServiceClassTest () throws Exception {
        clientHolder.getTaskDescList().add(tracerouteTaskDesc);

        testSettings.setTracerouteServiceClazz(null);
        qosTest = new QualityOfServiceTest(clientHolder, testSettings);

        assertEquals("Unexpected qosTestMap size", 0, qosTest.getTestMap().size());
    }

    @Test
    public void noWebsiteTestWithoutServiceTest () throws Exception {
        clientHolder.getTaskDescList().add(websiteTaskDesc);

        testSettings.setWebsiteTestService(null);
        qosTest = new QualityOfServiceTest(clientHolder, testSettings);

        assertEquals("Unexpected qosTestMap size", 0, qosTest.getTestMap().size());
    }

    @Test
    public void noEchoProtocolWithoutProtocolDefinitionTest () throws Exception {
        clientHolder.getTaskDescList().add(echoNoProtocolTaskDesc);

        qosTest = new QualityOfServiceTest(clientHolder, testSettings);

        assertEquals("Unexpected qosTestMap size", 0, qosTest.getTestMap().size());
    }

    @Test
    public void unknownEchoProtocolWithoutProtocolDefinitionTest () throws Exception {
        clientHolder.getTaskDescList().add(echoUnknownTaskDesc);

        qosTest = new QualityOfServiceTest(clientHolder, testSettings);

        assertEquals("Unexpected qosTestMap size", 0, qosTest.getTestMap().size());
    }

    @Test
    public void basicTaskExecutionTest (@Mocked final TrafficService trafficService) throws Exception {

        // get every test type to return the correct taskDesc
        new Expectations() {{

            dnsTask.getTestType();
            result = QosMeasurementType.DNS;
            dnsTask.getTaskDesc();
            minTimes = 0;
            result = dnsTaskDesc;
            dnsTask.getTestServerAddr();
            minTimes = 0;
            result = "test_server_address";

            tcpTask.getTestType();
            result = QosMeasurementType.TCP;
            tcpTask.getTaskDesc();
            minTimes = 0;
            result = tcpTaskDesc;
            tcpTask.getTestServerAddr();
            minTimes = 0;
            result = "other_test_server_address";

            trafficService.start();
            times = 1;

            tcpTask.call();
            times = 2;
            result = new QoSTestResult(QosMeasurementType.TCP, tcpTask);

            dnsTask.call();
            times = 1;
            result = new QoSTestResult(QosMeasurementType.DNS, dnsTask);

        }};

        final QoSMeasurementClientProgressListener listener = new QoSMeasurementClientProgressListener() {
            @Override
            public void onProgress(float progress) {
            }

            @Override
            public void onQoSTypeProgress(QosMeasurementType qosType, float progress) {
            }

            @Override
            public void onQoSTypeStarted(QosMeasurementType qosType) {
            }

            @Override
            public void onQoSTypeFinished(QosMeasurementType qosType) {
            }

            @Override
            public void onQoSStatusChanged(QoSTestEnum newStatus) {
            }

            @Override
            public void onQoSTestsDefined(int testCount) {
                assertEquals("Invalid number of tests reported", 3, testCount);
            }
        };

        // listener that gets removed shall not receive any callbacks
        final QoSMeasurementClientProgressListener removedListener = new QoSMeasurementClientProgressListener() {
            @Override
            public void onProgress(float progress) {
                fail();
            }

            @Override
            public void onQoSTypeProgress(QosMeasurementType qosType, float progress) {
                fail();
            }

            @Override
            public void onQoSTypeStarted(QosMeasurementType qosType) {
                fail();
            }

            @Override
            public void onQoSTypeFinished(QosMeasurementType qosType) {
                fail();
            }

            @Override
            public void onQoSStatusChanged(QoSTestEnum newStatus) {
                assertEquals("Unexpected status in onQoSStatusChanged", QoSTestEnum.START, newStatus);
            }

            @Override
            public void onQoSTestsDefined(int testCount) {
                fail();
            }
        };

        List<QoSMeasurementClientProgressListener> listenerList = new ArrayList<>();
        listenerList.add(listener);
        listenerList.add(removedListener);

        clientHolder.getTaskDescList().add(dnsTaskDesc);
        clientHolder.getTaskDescList().add(tcpTaskDesc);
        clientHolder.getTaskDescList().add(tcpTaskDesc);

        testSettings.setTrafficService(trafficService);

        qosTest = new QualityOfServiceTest(clientHolder, testSettings, listenerList);

        assertEquals("Unexpected qosTestMap size", 2, qosTest.getTestMap().size());
        assertEquals("The two Http proxy tests have not been registered correctly", 2, qosTest.getTestMap().get(QosMeasurementType.TCP).size());

        qosTest.removeQoSProgressListener(removedListener);
        final QoSResultCollector res = qosTest.call();

        assertEquals("Unexpected number of results", 3, res.getResults().size());

    }

    @Test
    public void taskExecutionWithErrorIsNotInResultsTest (@Mocked final TrafficService trafficService) throws Exception {

        new Expectations() {{

            dnsTask.getTestType();
            result = QosMeasurementType.DNS;
            dnsTask.getTaskDesc();
            minTimes = 0;
            result = dnsTaskDesc;
            dnsTask.getTestServerAddr();
            minTimes = 0;
            result = "test_server_address";

            tcpTask.getTestType();
            result = QosMeasurementType.TCP;
            tcpTask.getTaskDesc();
            minTimes = 0;
            result = tcpTaskDesc;
            tcpTask.getTestServerAddr();
            minTimes = 0;
            result = "other_test_server_address";
            tcpTask.hasConnectionError();
            returns(
                    false,
                    true
            );

            trafficService.start();
            times = 1;

            tcpTask.call();
            times = 2;
            result = new QoSTestResult(QosMeasurementType.TCP, tcpTask);

            dnsTask.call();
            times = 1;
            result = new QoSTestResult(QosMeasurementType.DNS, dnsTask);

        }};

        clientHolder.getTaskDescList().add(dnsTaskDesc);
        clientHolder.getTaskDescList().add(tcpTaskDesc);
        clientHolder.getTaskDescList().add(tcpTaskDesc);

        testSettings.setTrafficService(trafficService);

        qosTest = new QualityOfServiceTest(clientHolder, testSettings);

        assertEquals("Unexpected qosTestMap size", 2, qosTest.getTestMap().size());
        assertEquals("The two Http proxy tests have not been registered correctly", 2, qosTest.getTestMap().get(QosMeasurementType.TCP).size());

        final QoSResultCollector res = qosTest.call();

        assertEquals("Unexpected number of results", 2, res.getResults().size());
        assertEquals("Unexpected qosTest status", QoSTestEnum.QOS_FINISHED, qosTest.getStatus());

    }

    @Test
    public void taskExecutionWithFatalErrorShowsError (@Mocked final TrafficService trafficService) throws Exception {

        final QoSTestResult fatalResult = new QoSTestResult(QosMeasurementType.TCP, tcpTask);
        fatalResult.setFatalError(true);

        new Expectations() {{

            dnsTask.getTestType();
            result = QosMeasurementType.DNS;
            dnsTask.getTaskDesc();
            minTimes = 0;
            result = dnsTaskDesc;
            dnsTask.getTestServerAddr();
            minTimes = 0;
            result = "test_server_address";

            tcpTask.getTestType();
            result = QosMeasurementType.TCP;
            tcpTask.getTaskDesc();
            minTimes = 0;
            result = tcpTaskDesc;
            tcpTask.getTestServerAddr();
            minTimes = 0;
            result = "other_test_server_address";
            tcpTask.hasConnectionError();
            returns(
                    false,
                    true
            );

            trafficService.start();
            times = 1;

            tcpTask.call();
            times = 2;
            returns(
                    new QoSTestResult(QosMeasurementType.TCP, tcpTask),
                    fatalResult
            );

            dnsTask.call();
            times = 1;
            result = new QoSTestResult(QosMeasurementType.DNS, dnsTask);

        }};

        clientHolder.getTaskDescList().add(dnsTaskDesc);
        clientHolder.getTaskDescList().add(tcpTaskDesc);
        clientHolder.getTaskDescList().add(tcpTaskDesc);

        testSettings.setTrafficService(trafficService);

        qosTest = new QualityOfServiceTest(clientHolder, testSettings);

        assertEquals("Unexpected qosTestMap size", 2, qosTest.getTestMap().size());
        assertEquals("The two Http proxy tests have not been registered correctly", 2, qosTest.getTestMap().get(QosMeasurementType.TCP).size());

        qosTest.call();
        assertEquals("Unexpected qosTest status", QoSTestEnum.ERROR, qosTest.getStatus());


    }

}

