package com.zafaco.moduleCommon;

/*!
    \file Http.java
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2019-11-13

    Copyright (C) 2016 - 2019 zafaco GmbH

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3 
    as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class Http
{
    /**************************** Variables ****************************/

    Context ctx;

    //Module Objects
    private Tool mTool;

    private JSONObject jData;

    /*******************************************************************/

    public Http()
    {
        mTool = new Tool();
    }

    /**
     * Post the Results to the DB Server
     * @return 0 if ok, else -1
     */
    public JSONObject genericHTTPRequest(final String sServer, final String sRequest, final HashMap<String, String> hHeader, final String sData)
    {
        jData = new JSONObject();

        //Check DB if entries have to be send to our server
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String sResponseServer = "";
                StringBuilder sResponse = new StringBuilder();


                HttpURLConnection con = null;

                try
                {
                    //DEBUG
                    mTool.printOutput("[genericHTTPRequest] Server: "+sServer);
                    mTool.printOutput("[genericHTTPRequest] Request: "+sData);

                    //--------------------------------------------------------------------------------------
                    URL uServer = new URL(sServer);

                    con = (HttpURLConnection) uServer.openConnection();
					con.setConnectTimeout(4000);
                    con.setRequestMethod(sRequest);
                    con.setRequestProperty("User-Agent", "android_client");
                    con.setDoOutput(true);

                    if(sData != null)
                        con.setRequestProperty("Content-Length", String.valueOf(sData.length()));

                    //Add additional Header
                    for (Map.Entry<String, String> entry : hHeader.entrySet())
                    {
                        con.setRequestProperty(entry.getKey(), entry.getValue());
                    }

                    if(sRequest.equals("POST"))
                    {
                        DataOutputStream dos = new DataOutputStream(con.getOutputStream());
                        dos.writeBytes(sData);
                        dos.flush();
                        dos.close();
                    }

                    //Get Return Code
                    jData.put("nCode",con.getResponseCode());

                    if( con.getResponseCode() == 200 )
                    {
                        InputStream ins = con.getInputStream();
                        InputStreamReader isr = new InputStreamReader(ins);
                        BufferedReader in = new BufferedReader(isr);

                        while ((sResponseServer = in.readLine()) != null)
                        {
                            sResponse.append(sResponseServer);
                        }
                        sResponseServer = sResponse.toString();

                        in.close();
                    }

                    //Get Response
                    jData.put("sResponse",sResponseServer);

                    //--------------------------------------------------------------------------------------

                    //DEBUG
                    mTool.printOutput("[genericHTTPRequest] Response["+jData.getString("nCode")+"]: "+sResponseServer);

                }
                catch (Exception e)
                {
                    mTool.printTrace(e);

                    try
                    {
                        //Get Connection Return Code
                        jData.put("nCode",con.getResponseCode());
                        jData.put("sResponse","-");
                    }
                    catch (Exception ex) { mTool.printTrace(ex); }
                }

            }
        });

        //Start Thread
        thread.start();

        try
        {
            thread.join();
        }
        catch (Exception ex) { mTool.printTrace(ex); }

        return this.jData;
    }

    public JSONObject genericDownloadRequest(final String sServer, final String sRequest, final HashMap<String, String> hHeader, final String sData, final String sDestination)
    {
        jData = new JSONObject();

        //Check DB if entries have to be send to our server
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    //DEBUG
                    mTool.printOutput("[genericDownloadRequest] Request: "+sData);
                    mTool.printOutput("[genericDownloadRequest] Server: "+sServer);

                    URL urldbserver = new URL(sServer);

                    HttpsURLConnection con = (HttpsURLConnection) urldbserver.openConnection();
                    con.setConnectTimeout(4000);
                    con.setRequestMethod(sRequest);
                    con.setRequestProperty("Content-length", "0");
                    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    con.setRequestProperty("User-Agent", "java_client");
                    con.setDoOutput(true);

                    if(sData != null)
                        con.setRequestProperty("Content-Length", String.valueOf(sData.length()));

                    //Add additional Header
                    for (Map.Entry<String, String> entry : hHeader.entrySet())
                    {
                        con.setRequestProperty(entry.getKey(), entry.getValue());
                    }

                    if(sRequest.equals("POST"))
                    {
                        DataOutputStream dos = new DataOutputStream(con.getOutputStream());
                        dos.writeBytes(sData);
                        dos.flush();
                        dos.close();
                    }

                    //Get Return Code
                    jData.put("nCode",con.getResponseCode());

                    if( con.getResponseCode() == 200 )
                    {
                        InputStream ins = con.getInputStream();

                        OutputStream outputStream = new FileOutputStream(sDestination);

                        mTool.printOutput("[genericDownloadRequest] Destination File: "+sDestination);

                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = ins.read(buffer))>0)
                        {
                            outputStream.write(buffer, 0, length);
                        }
                        outputStream.close();

                    }
                    jData.put("nCode",con.getResponseCode());

                }
                catch (Exception e)
                {
                    mTool.printTrace(e);

                    try
                    {
                        //Get Connection Return Code
                        jData.put("nCode",-1);
                        jData.put("sResponse","-");
                    }
                    catch (Exception ex) { mTool.printTrace(ex); }

                }

            }
        });

        //Start Thread
        thread.start();

        try
        {
            thread.join();
        }
        catch (Exception ex) { mTool.printTrace(ex); }

		return this.jData;
    }
}
