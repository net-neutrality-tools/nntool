/*******************************************************************************
 * Copyright 2013-2019 alladin-IT GmbH
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
// based on: https://raw.githubusercontent.com/alladin-IT/open-rmbt/master/RMBTClient/src/at/alladin/rmbt/client/helper/Config.java
package at.alladin.nntool.client.helper;

/**
 * The system defaults.
 * 
 * 
 * 
 */
public abstract interface Config
{
    
    /*********************
     * 
     * Default Preferences
     * 
     *********************/
    
    public static final String CLIENT_NAME = "NNTool";
    public static final String VERSION_NUMBER = "0.3";
    public static final String VERSION_STRING = CLIENT_NAME + "v" + VERSION_NUMBER;
    public static final String CONTROL_DEFAULT_SERVER = "";    //TODO: replace w/useful value and replace as a whole once POC phase is over
    public static final int CONTROL_PORT = 443;
    public static final boolean CONTROL_SSL = true;
    public static final boolean QOS_SSL = true;//true;
    public static final String CONTROL_PATH = "";
    public static final String CONTROL_MAIN_URL = "/";
    public static final String CONTROL_V2_TESTS = "/qosTestRequest";
    public static final String CONTROL_NDT_RESULT_URL = "ndtResult";
    public static final String NEWS_HOST_URL = "/news";
    public static final String IP_HOST_URL = "/ip";
    public static final String HISTORY_HOST_URL = "/history";
    public static final String TESTRESULT_HOST_URL = "/testresult";
    public static final String TESTRESULT_DETAIL_HOST_URL = "/testresultdetail";
    public static final String TESTRESULT_QOS_HOST_URL = "/qosTestResult";
    public static final String TESTRESULT_OPENDATA_HOST_URL = "/opentests/";
    public static final String SYNC_HOST_URL = "/sync";
    public static final String SETTINGS_HOST_URL = "/settings";
    public static final String LOG_HOST_URL = "/log";
    
    // Verschluesselungsart -> TLS oder SSL
    public static final String ENCRYPTION_STRING = "TLS";
    
    public static final int SPEED_TEST_INTERVAL = 250;
    
    public static final String MLAB_NS = "http://mlab-ns.appspot.com/ndt?format=json";
    public static final String NDT_FALLBACK_HOST = "ndt.iupui.donar.measurement-lab.org";
    
}
