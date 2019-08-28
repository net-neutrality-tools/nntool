package at.alladin.nettest.qos.android.impl;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import at.alladin.nntool.client.v2.task.service.MkitService;
import io.ooni.mk.MKAsyncTask;


/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class MkitServiceAndroidImpl implements MkitService {

    public final static class MkitResultJsonImpl implements MkitService.MkitResult {

        private String result;

        public MkitResultJsonImpl (final String result) {
            this.result = result;
        }

        @Override
        public JSONObject toJson() {
            try {
                return new JSONObject(result);
            } catch (JSONException ex) {
                Log.i(TAG, result);
                Log.e(TAG, ex.getMessage());
                return null;
            }
        }

        @Override
        public String toString() {
            return result;
        }
    }

    private final static String TAG = "NdtAndroidImpl";

    //The geoip paths are set on startup of the main activity
    public static String geoIPCountryDBPath;
    public static String geoIPASNDBPath;
    public static String caBundlePath;

    private JSONArray mkitInputArray;

    private MkitTestEnum mkitTestEnum;
    private JSONObject mkitTestConfig;
    private MkitResult result;

    private float currentProgress = 0;

    @Override
    public void setTestToExecute(final MkitTestEnum testToExecute) throws UnsupportedMkitTestException {
        this.mkitTestEnum = testToExecute;

        try {
            mkitTestConfig = getTestForEnum(mkitTestEnum);
        } catch (JSONException ex) {
            ex.printStackTrace();
            mkitTestConfig = null;
        }
        if (mkitTestConfig == null) {
            throw new UnsupportedMkitTestException("Specified mkit test not available. (" + mkitTestEnum+ ")");
        }
    }

    @Override
    public float getProgress() {
        return currentProgress;
    }

    @Override
    public void cancel() {

    }

    @Override
    public MkitResult call() throws Exception {

        if (mkitTestConfig == null) {
            return null;
        }

        if (mkitInputArray != null) {
            mkitTestConfig.put("inputs", mkitInputArray);
        }

        Log.i(TAG, "TEST CONFIG: " + mkitTestConfig.toString());

        MKAsyncTask task = MKAsyncTask.start(mkitTestConfig.toString());
        while (!task.isDone()) {
            final String event = task.waitForNextEvent();
            if (event != null) {
                Log.i(TAG, "EVENT: " + event);
                final JSONObject eventObj = new JSONObject(event);
                if (eventObj.has("key")) {
                    final String keyValue = eventObj.getString("key");
                    if ("measurement".equals(keyValue) && eventObj.has("value")) {
                        Log.i(TAG, "Measurement ended");
                        result = new MkitResultJsonImpl(eventObj.getJSONObject("value").getString("json_str"));
                    }
                }
            }
        }


        Log.i(TAG, "Finished MKAsync task");

        //mkitTest.run();
        //remove potentially unwanted result entries
        postProcessResult();
        return result;
    }

    private void updateProgress(final double testProgress) {
        this.currentProgress = (float) testProgress;
        Log.i(TAG, "Progress: " + currentProgress);
    }

    /**
     * Construct a test for the given enum
     * Will return a ready-to-use test, with all necessary options set
     * or NULL, if the test is not supported by the Android Impl
     *
     * List of (theoretically) available tests can be found in the nettests.hpp file on github (under MK_ENUM_TEST)
     * (current link: https://github.com/measurement-kit/measurement-kit/blob/master/include/measurement_kit/nettests/nettests.hpp)
     * @return
     */
    private JSONObject getTestForEnum (final MkitTestEnum mkitTestEnum) throws JSONException {
        switch (mkitTestEnum) {
            case MKIT_DASH:
                mkitTestConfig = new JSONObject();
                mkitTestConfig.put("name", "Dash");
                break;
            case MKIT_WEB_CONNECTIVITY:
                mkitTestConfig = new JSONObject();
                mkitTestConfig.put("name", "WebConnectivity");
                break;
            default:
                mkitTestConfig = null;
        }
        if (mkitTestConfig != null) {
            setBasicTestOptions(mkitTestConfig);
        }
        return mkitTestConfig;
    }

    /**
     * Takes the current result and current ndtTestEnum to postprocess obtained results
     * @return the result to be used from here on out (may be null, if the result is null)
     */
    private MkitResult postProcessResult() {
        if (result == null) {
            return result;
        }

        switch (mkitTestEnum) {
            /*
            case MKIT_WEB_CONNECTIVITY:
                try {
                    final JSONObject json = result.toJson();
                    final JSONArray jsonArray = json.getJSONObject("test_keys").getJSONArray("requests");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonArray.getJSONObject(i).getJSONObject("response").remove("body");
                    }
                    result = new NdtResultJsonImpl(json.toString());
                } catch(JSONException ex) {
                    ex.printStackTrace();
                    Log.e(TAG, "Error during parsing of result: " + result + "\nProceeding with unparsed result");
                }
                break;
            */
            case MKIT_WEB_CONNECTIVITY:
            case MKIT_TELEGRAM_MESSENGER:
            case MKIT_WHATSAPP_MESSENGER:
                try {
                    final JSONObject json = result.toJson();
                    final JSONArray jsonArray = json.getJSONObject("test_keys").getJSONArray("requests");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        final JSONObject tmp = jsonArray.getJSONObject(i).optJSONObject("response");
                        if (tmp != null && tmp.has("body")) {
                            tmp.remove("body");
                        }
                    }
                    result = new MkitResultJsonImpl(json.toString());
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    Log.e(TAG, "Error during parsing of result: " + result + "\nProceeding with unparsed result");
                }
        }
        return result;
    }

    private void setBasicTestOptions(final JSONObject config) throws JSONException {
        if (config == null) {
            return;
        }
        JSONObject options = new JSONObject();
        options.put("net/ca_bundle_path", caBundlePath);
        options.put("geoip_country_path", geoIPCountryDBPath);
        options.put("geoip_asn_path", geoIPASNDBPath);
        //disable file report
        options.put("no_file_report", true);
        options.put("no_collector", true);
        //options.put("no_bouncer", true);
        options.put("no_geoip", true);
        options.put("no_resolver_lookup", true);

        config.put("log_level", "INFO");
        config.put("options", options);
        //config.put("disabled_events", ARRAY);

    }

    @Override
    public void addInput(final String input) {
        if (input != null) {
            Log.i(TAG, "INPUT: " + input);
            if (mkitInputArray == null) {
                mkitInputArray = new JSONArray();
            }
            mkitInputArray.put(input.replace("\"", ""));
        }
    }


    @Override
    public void addFlags(final String flags) {
        /*
        if (flags != null && mkitTest != null) {
            final String[] flagArray = flags.split(";");
            for (String s : flagArray) {
                final String[] args = s.split(" ");
                //if no value is provided, assume true as value
                ndtTest.set_option(args[0], args.length > 1 && !args[1].equals("") ? args[1] : "1");
            }
        }
        */
    }


}
