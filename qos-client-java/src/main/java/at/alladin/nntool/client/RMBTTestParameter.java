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

import java.io.Serializable;

public class RMBTTestParameter implements Serializable
{
    
    private String host;
    private int port;
    private boolean encryption;
    private String token;
    private int pretestDuration = 2;
    private int duration;
    private int numThreads;
    private int numPings;
    private long startTime;

    public RMBTTestParameter() { }

    public RMBTTestParameter(final String host, final int port, final boolean encryption, final String token,
            final int duration, final int numThreads, final int numPings, final long startTime)
    {
        super();
        this.host = host;
        this.port = port;
        this.encryption = encryption;
        this.token = token;
        this.duration = duration;
        this.numThreads = numThreads;
        this.numPings = numPings;
        this.startTime = startTime;
    }
    
    public RMBTTestParameter(final String host, final int port, final boolean encryption,
            final int duration, final int numThreads, final int numPings)
    {
        super();
        this.host = host;
        this.port = port;
        this.encryption = encryption;
        this.duration = duration;
        this.numThreads = numThreads;
        this.numPings = numPings;
        this.token = null;
        this.startTime = 0;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setEncryption(boolean encryption) {
        this.encryption = encryption;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setPretestDuration(int pretestDuration) {
        this.pretestDuration = pretestDuration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public void setNumPings(int numPings) {
        this.numPings = numPings;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getHost()
    {
        return host;
    }
    
    public int getPort()
    {
        return port;
    }
    
    public boolean isEncryption()
    {
        return encryption;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public String getUUID()
    {
        if (token == null)
            return null;
        final String[] parts = token.split("_");
        if (parts == null || parts.length <= 0)
            return null;
        return parts[0];
    }
    
    public int getDuration()
    {
        return duration;
    }
    
    public int getPretestDuration()
    {
        return pretestDuration;
    }
    
    public int getNumThreads()
    {
        return numThreads;
    }
    
    public int getNumPings()
    {
        return numPings;
    }
    
    public long getStartTime()
    {
        return startTime;
    }

    public void check() throws IllegalArgumentException
    {
        if (host == null || host.length() == 0)
            throw new IllegalArgumentException("no host");
        if (port <= 0)
            throw new IllegalArgumentException("no port");
        if (getUUID() == null)
            throw new IllegalArgumentException("no uuid");
        if (numThreads <= 0)
            throw new IllegalArgumentException("num threads <= 0");
//        if (pretestDuration < 0)
//            throw new IllegalArgumentException("pretestDuration < 0");
    }
}
