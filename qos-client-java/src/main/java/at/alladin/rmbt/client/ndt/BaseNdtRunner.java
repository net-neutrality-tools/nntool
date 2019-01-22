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

package at.alladin.rmbt.client.ndt;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.json.JSONException;
import org.json.JSONObject;

import at.alladin.rmbt.client.helper.JSONParser;
import at.alladin.rmbt.client.helper.NdtStatus;
import net.measurementlab.ndt.NdtTests;

public class BaseNdtRunner implements NdtRunner
{
    private final AtomicInteger ndtProgress = new AtomicInteger();
    private final AtomicReference<NdtStatus> ndtStatus = new AtomicReference<NdtStatus>(NdtStatus.NOT_STARTED);
    private final AtomicBoolean ndtFailure = new AtomicBoolean();
    private final AtomicBoolean ndtCancelled = new AtomicBoolean();
    private final String ndtHost;

    private String ndtNetworkType;
    private UiServices uiServices;
    
    public BaseNdtRunner(String ndtHost)
    {
        if (ndtHost == null)
            this.ndtHost = getNdtHost();
        else
            this.ndtHost = ndtHost;
    }
    
    public BaseNdtRunner()
    {
        this(null);
    }
    
    public static String getNdtHost()
    {
        final JSONParser jParser = new JSONParser();
        final JSONObject obj = jParser.getURL(URI.create(at.alladin.rmbt.client.helper.Config.MLAB_NS));
        try
        {
            if (obj != null)
            {
//                System.out.println(obj.toString(4));
                return obj.getString("fqdn");
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return at.alladin.rmbt.client.helper.Config.NDT_FALLBACK_HOST;
    }

    public abstract class UiServices extends UiServicesAdapter
    {
        @Override
        public void incrementProgress()
        {
            ndtProgress.incrementAndGet();
        }

        @Override
        public boolean wantToStop()
        {
            return ndtCancelled.get();
        }

        public void cancel()
        {
            ndtCancelled.set(true);
        }

        @Override
        public void onFailure(final String errorMessage)
        {
            ndtFailure.set(true);
            System.out.println("NDT error:" + errorMessage);
        }

        public abstract void sendResults();
    }
    
    public NdtStatus getNdtStatus()
    {
        return ndtStatus.get();
    }
    
    public float getNdtProgress()
    {
        final int progress = ndtProgress.get();
        return (float) progress / UiServices.TEST_STEPS;
    }
    
    public void setNdtCancelled(final boolean cancelled)
    {
        ndtCancelled.set(cancelled);
    }

    /**
     * Need to set the NdtNetworkType before calling run
     */
    public void run()
    {
        if (this.ndtNetworkType == null) {
            System.err.println("NdtNetworkType is not set. Cannot run NDTTests");
            return;
        }
        ndtProgress.set(0);
        ndtStatus.set(NdtStatus.RUNNING);
        System.out.println("ndt status RUNNING");
        
        if (uiServices == null)
            uiServices = new UiServices() {
                @Override
                public void sendResults()
                {
                }
            };
        
        try
        {
            final NdtTests ndt = new NdtTests(ndtHost, uiServices, ndtNetworkType);
            ndt.run();
        }
        catch (final Throwable t)
        {
            t.printStackTrace();
        }
        
        if (ndtCancelled.get())
        {
            ndtStatus.set(NdtStatus.ABORTED);
            System.out.println("ndt status ABORTED");
        }
        else if (ndtFailure.get())
        {
            ndtStatus.set(NdtStatus.ERROR);
            System.out.println("ndt status ERROR");
        }
        else
        {
            ndtStatus.set(NdtStatus.RESULTS);
            System.out.println("ndt status RESULTS");
            uiServices.sendResults();
            ndtStatus.set(NdtStatus.FINISHED);
            System.out.println("ndt status FINISHED");
        }
    }

    public void setUiServices(final BaseNdtRunner.UiServices uiServices) {
        this.uiServices = uiServices;
    }

    public void setNdtNetworkType(final String ndtNetworkType) {
        this.ndtNetworkType = ndtNetworkType;
    }
}
