/*******************************************************************************
 * Copyright 2013-2019 alladin-IT GmbH
 * Copyright 2014-2016 SPECURE GmbH
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

import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import at.alladin.nntool.client.helper.Config;
import at.alladin.nntool.client.helper.RMBTOutputCallback;
import at.alladin.nntool.client.helper.TestStatus;
import at.alladin.nntool.client.v2.task.TaskDesc;
import at.alladin.nntool.client.v2.task.service.TestMeasurement;
import at.alladin.nntool.client.v2.task.service.TrafficService;
import at.alladin.nntool.util.tools.InformationCollectorTool;

public class ClientHolder
{
    private static final ExecutorService COMMON_THREAD_POOL = Executors.newCachedThreadPool();

    private final long durationInitNano = 2500000000L; // TODO

    private final AtomicLong initNano = new AtomicLong(-1);
    private final AtomicLong pingNano = new AtomicLong(-1);
    private final AtomicLong downBitPerSec = new AtomicLong(-1);
    private final AtomicLong upBitPerSec = new AtomicLong(-1);
    
    /* ping status */
    private final AtomicLong pingTsStart = new AtomicLong(-1);
    private final AtomicInteger pingNumDone = new AtomicInteger(-1);
    private final AtomicLong pingTsLastPing = new AtomicLong(-1);
    
    private final static long MIN_DIFF_TIME = 100000000; // 100 ms
    
    private final static int KEEP_LAST_ENTRIES = 20;
    private int lastCounter;

//    private TotalTestResult result;
    
    private SSLSocketFactory sslSocketFactory;
    
    private RMBTOutputCallback outputCallback;
    private final boolean outputToStdout = true;
    
//    private final ControlServerConnection controlConnection;
    
    private final AtomicBoolean aborted = new AtomicBoolean();
    
    private String errorMsg = "";
    
    
    /*------------------------------------
    	V2 tests
    --------------------------------------*/
    
    public final static String TASK_UDP = "udp";
    public final static String TASK_TCP = "tcp";
    public final static String TASK_DNS = "dns";
    public final static String TASK_VOIP = "voip";
    public final static String TASK_NON_TRANSPARENT_PROXY = "non_transparent_proxy";
    public final static String TASK_HTTP = "http_proxy";
    public final static String TASK_WEBSITE = "website";
    public final static String TASK_TRACEROUTE = "traceroute";
    public final static String TASK_ECHO_PROTOCOL = "echo_protocol";
    public final static String TASK_AUDIO_STREAMING = "audio_streaming";

    private List<TaskDesc> taskDescList;

    /*------------------------------------*/
    
    private final AtomicReference<TestStatus> testStatus = new AtomicReference<TestStatus>(TestStatus.WAIT);
    private final AtomicReference<TestStatus> statusBeforeError = new AtomicReference<TestStatus>(null);
    private final AtomicLong statusChangeTime = new AtomicLong();
    
    private TrafficService trafficService;
    
    private InformationCollectorTool informationCollectorTool;

    private String collectorUrl;
    
    public static ExecutorService getCommonThreadPool()
    {
        return COMMON_THREAD_POOL;
    }
    
    private ConcurrentHashMap<TestStatus, TestMeasurement> measurementMap = new ConcurrentHashMap<TestStatus, TestMeasurement>();

    public static ClientHolder getInstance(final List<TaskDesc> taskDescList, final String collectorUrl) {
        return new ClientHolder(taskDescList, collectorUrl);
    }

    private ClientHolder(final List<TaskDesc> taskDescList, final String collectorUrl) {
        this.taskDescList = taskDescList;
        this.collectorUrl = collectorUrl;
    }

    public String getCollectorUrl() {
        return collectorUrl;
    }

    public void setCollectorUrl(String collectorUrl) {
        this.collectorUrl = collectorUrl;
    }

    public void setTrafficService(TrafficService trafficService) {
    	this.trafficService = trafficService;
    }
    
    public TrafficService getTrafficService() {
    	return this.trafficService;
    }
    
    public void setInformationCollectorTool(InformationCollectorTool tool) {
    	this.informationCollectorTool = tool;
    }
    
    public InformationCollectorTool getInformationCollectorTool() {
    	return this.informationCollectorTool;
    }
    
    public void startInformationCollectorTool() {
    	if (this.informationCollectorTool != null && !this.informationCollectorTool.isRunning()) {
    		this.informationCollectorTool.start(COMMON_THREAD_POOL);
    	}
    }
    
    public JSONObject getInformationCollectorToolIntermediateResult(boolean clean) {
    	if (this.informationCollectorTool != null && !this.informationCollectorTool.isRunning()) {
    		return this.informationCollectorTool.getJsonObject(clean);
    	}
    	
    	return null;
    }
    
    public void stopInformationCollectorTool() {
    	if (this.informationCollectorTool != null && this.informationCollectorTool.isRunning()) {
    		this.informationCollectorTool.stop();
    	}    	
    }
    
    /*private*/public SSLSocketFactory createSSLSocketFactory() // is now public and gets called during QoS measurement because QoS is always encrypted
    {
        log("initSSL...");
        try
        {
            final SSLContext sc = getSSLContext(null, null);
            
            sslSocketFactory = sc.getSocketFactory();
        }
        catch (final Exception e)
        {
            setErrorStatus();
            log(e);
        }
        
        return sslSocketFactory;
    }
    
    public static TrustManager getTrustingManager()
    {
        return new javax.net.ssl.X509TrustManager()
        {
            public X509Certificate[] getAcceptedIssuers()
            {
                return new X509Certificate[] {};
            }
            
            public void checkClientTrusted(final X509Certificate[] certs, final String authType)
                    throws CertificateException
            {
                // System.out.println("[TRUSTING] checkClientTrusted: " +
                // Arrays.toString(certs) + " - " + authType);
            }
            
            public void checkServerTrusted(final X509Certificate[] certs, final String authType)
                    throws CertificateException
            {
                // System.out.println("[TRUSTING] checkServerTrusted: " +
                // Arrays.toString(certs) + " - " + authType);
            }
        };
    }
    
    public static SSLContext getSSLContext(final String caResource, final String certResource)
            throws NoSuchAlgorithmException, KeyManagementException
    {
        X509Certificate _ca = null;
        try
        {
            if (caResource != null)
            {
                final CertificateFactory cf = CertificateFactory.getInstance("X.509");
                _ca = (X509Certificate) cf.generateCertificate(ClientHolder.class.getClassLoader().getResourceAsStream(
                        caResource));
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        
        final X509Certificate ca = _ca;
        
        X509Certificate _cert = null;
        try
        {
            if (certResource != null)
            {
                final CertificateFactory cf = CertificateFactory.getInstance("X.509");
                _cert = (X509Certificate) cf.generateCertificate(ClientHolder.class.getClassLoader().getResourceAsStream(
                        certResource));
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        final X509Certificate cert = _cert;
        
        // TrustManagerFactory tmf = null;
        // try
        // {
        // if (cert != null)
        // {
        // final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        // ks.load(null, null);
        // ks.setCertificateEntry("crt", cert);
        //
        // tmf =
        // TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        // tmf.init(ks);
        // }
        // }
        // catch (Exception e)
        // {
        // e.printStackTrace();
        // }
        
        final TrustManager tm;
        if (cert == null)
            tm = getTrustingManager();
        else
            tm = new javax.net.ssl.X509TrustManager()
            {
                public X509Certificate[] getAcceptedIssuers()
                {
                    // System.out.println("getAcceptedIssuers");
                    if (ca == null)
                        return new X509Certificate[] { cert };
                    else
                        return new X509Certificate[] { ca };
                }
                
                public void checkClientTrusted(final X509Certificate[] certs, final String authType)
                        throws CertificateException
                {
                    // System.out.println("checkClientTrusted: " +
                    // Arrays.toString(certs) + " - " + authType);
                }
                
                public void checkServerTrusted(final X509Certificate[] certs, final String authType)
                        throws CertificateException
                {
                    // System.out.println("checkServerTrusted: " +
                    // Arrays.toString(certs) + " - " + authType);
                    if (certs == null)
                        throw new CertificateException();
                    for (final X509Certificate c : certs)
                        if (cert.equals(c))
                            return;
                    throw new CertificateException();
                }
            };
        
        final TrustManager[] trustManagers = new TrustManager[] { tm };
        
        javax.net.ssl.SSLContext sc;
        sc = javax.net.ssl.SSLContext.getInstance(Config.ENCRYPTION_STRING);
        
        sc.init(null, trustManagers, new java.security.SecureRandom());
        return sc;
    }
    
    public boolean abortTest(final boolean error)
    {
        System.out.println("RMBTClient stopTest");
        
        if (error)
            setErrorStatus();
        else
            setStatus(TestStatus.ABORTED);
        aborted.set(true);

        return true;
    }
    
    public void shutdown()
    {
        System.out.println("Shutting down RMBT thread pool...");

        System.out.println("Shutdown finished.");
    }
    
    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
    }
    
    public SSLSocketFactory getSslSocketFactory()
    {
        return sslSocketFactory;
    }
    
    public void setOutputCallback(final RMBTOutputCallback outputCallback)
    {
        this.outputCallback = outputCallback;
    }
    
    public TestStatus getStatus()
    {
        return testStatus.get();
    }
    
    public TestStatus getStatusBeforeError()
    {
        return statusBeforeError.get();
    }
    
    public void setStatus(final TestStatus status)
    {
        testStatus.set(status);
        statusChangeTime.set(System.nanoTime());
    }
        
    public void startTrafficService(final int threadId, final TestStatus status) {
    	//if (trafficService != null) {
    		//a concurrent map is needed in case multiple threads want to start the traffic service
    		//only the first thread should be able to start the service
    		TestMeasurement tm = new TestMeasurement(status.toString(), trafficService);
        	TestMeasurement previousTm = measurementMap.putIfAbsent(status, tm);
        	if (previousTm == null) {
        		tm.start(threadId);
        	}
    	//}
    }
    
    public void stopTrafficMeasurement(final int threadId, final TestStatus status) {
    	final TestMeasurement testMeasurement = measurementMap.get(status);
    	if (testMeasurement != null)
    	    testMeasurement.stop(threadId);
    }
    
    public String getErrorMsg()
    {
        return errorMsg;
    }

    private void setErrorStatus()
    {
        final TestStatus lastStatus = testStatus.getAndSet(TestStatus.ERROR);
        if (lastStatus != TestStatus.ERROR)
            statusBeforeError.set(lastStatus);
    }
    
    void log(final CharSequence text)
    {
        if (outputToStdout)
            System.out.println(text);
        if (outputCallback != null)
            outputCallback.log(text);
    }
    
    void log(final Exception e)
    {
        if (outputToStdout)
            e.printStackTrace(System.out);
        if (outputCallback != null)
            outputCallback.log(String.format(Locale.US, "Error: %s", e.getMessage()));
    }
    

    /**
     * 
     * @return
     */
	public List<TaskDesc> getTaskDescList() {
		return taskDescList;
	}
 
}
