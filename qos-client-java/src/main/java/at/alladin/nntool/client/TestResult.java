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

package at.alladin.nntool.client;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public abstract class TestResult {
    /**
     * Local address of connection
     */
	public InetAddress ip_local;
	
	/**
	 * External ip of server connection
	 */
    public InetAddress ip_server;
    
    /**
     * External port (server) connected to
     */
    public int port_remote;

    /**
     * "<Standard name for protocol> (<Name of cypher suite>)"
     */
    public String encryption = "NONE";
    


}
