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

package at.alladin.rmbt.client.helper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser
{
    // Start filing Errors
    JSONArray errorList = null;
    
    // constructor
    public JSONParser()
    {
        // Start filing Errors
        errorList = new JSONArray();
    }
    
    public JSONObject getURL(final URI uri)
    {
        JSONObject jObj = null;
        String responseBody;
        
        try
        {
            final HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 20000);
            HttpConnectionParams.setSoTimeout(params, 20000);
            final HttpClient client = new DefaultHttpClient(params);
            
            final HttpGet httpget = new HttpGet(uri);
            
            final ResponseHandler<String> responseHandler = new BasicResponseHandler();
            responseBody = client.execute(httpget, responseHandler);
            
            // try parse the string to a JSON object
            try
            {
                jObj = new JSONObject(responseBody);
            }
            catch (final JSONException e)
            {
                writeErrorList("Error parsing JSON " + e.toString());
            }
            
        }
        catch (final UnsupportedEncodingException e)
        {
            writeErrorList("Wrong encoding");
            // e.printStackTrace();
        }
        catch (final HttpResponseException e)
        {
            writeErrorList("URI: " + uri.toString() + " - Server responded with Code " + e.getStatusCode() + " and message '" + e.getMessage() + "'");
        }
        catch (final ClientProtocolException e)
        {
            writeErrorList("Wrong Protocol");
            // e.printStackTrace();
        }
        catch (final IOException e)
        {
            writeErrorList("IO Exception");
            e.printStackTrace();
        }
        
        // return JSONObject
        return jObj;
    }
    
    public JSONObject sendJSONToUrl(final URI uri, final JSONObject data)
    {
        JSONObject jObj = null;

        try
        {
            final URL url = uri.toURL();
            final HttpURLConnection client = (HttpURLConnection) url.openConnection();
            client.setConnectTimeout(20000);
            client.setReadTimeout(20000);

            client.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            client.setRequestMethod("POST");

            byte[] outputBytes = data.toString().getBytes("UTF-8");
            final OutputStream os = client.getOutputStream();
            os.write(outputBytes);
            os.close();

            System.out.println("response code: " + client.getResponseMessage());
            BufferedInputStream in = new BufferedInputStream(client.getInputStream());

            byte[] contents = new byte[1024];

            int bytesRead = 0;
            StringBuilder response = new StringBuilder();
            while((bytesRead = in.read(contents)) != -1) {
                response.append(new String(contents, 0, bytesRead));
            }
            
            final String responseBody = response.toString();
            System.out.println(responseBody);

            // try parse the string to a JSON object
            try
            {
                jObj = new JSONObject(responseBody);
            }
            catch (final JSONException e)
            {
                writeErrorList("Error parsing JSON " + e.toString());
            }
            
        }
        catch (final UnsupportedEncodingException e)
        {
            writeErrorList("Wrong encoding");
            // e.printStackTrace();
        }
        catch (final IOException e)
        {
            writeErrorList("IO Exception");
            e.printStackTrace();
        }

        if (jObj == null)
            jObj = createErrorJSON();
        
        // return JSONObject
        return jObj;
    }
    
    private void writeErrorList(final String errorText)
    {
        try
        {
            errorList.put(errorList.length(), errorText);
            System.out.println(errorText);
        }
        catch (final JSONException e)
        {
            System.out.println("Error writing ErrorList: " + e.toString());
        }
    }
    
    private JSONObject createErrorJSON()
    {
        final JSONObject errorAnswer = new JSONObject();
        try
        {
            errorAnswer.putOpt("error", errorList);
        }
        catch (final JSONException e)
        {
            System.out.println("Error saving ErrorList: " + e.toString());
        }
        return errorAnswer;
    }
    
    /**
     * 
     * @param object
     * @return
     * @throws JSONException
     */
    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<?> keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)));
        }
        return map;
    }
    
    /**
     * 
     * @param array
     * @return
     * @throws JSONException
     */
    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }
 
    /**
     * 
     * @param json
     * @return
     * @throws JSONException
     */
    private static Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            return toMap((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        } else {
            return json;
        }
    }
}
