#include "android_connector.h"
#include "../../ias-libnntool/json11.hpp"
#include "trace.h"
#include <vector>
#include <android/log.h>

const char* AndroidConnector::TAG = "cpp";

extern "C" JNIEXPORT
jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {

    return AndroidConnector::getInstance().jniLoad(vm);
}

extern "C" JNIEXPORT
void JNICALL Java_at_alladin_nettest_nntool_android_app_workflow_measurement_jni_JniSpeedMeasurementClient_startMeasurement (JNIEnv* env, jobject thiz) {
    AndroidConnector::getInstance().startMeasurement();
}

extern "C" JNIEXPORT
void JNICALL Java_at_alladin_nettest_nntool_android_app_workflow_measurement_jni_JniSpeedMeasurementClient_stopMeasurement (JNIEnv* env, jobject thiz) {
    //AndroidConnector::getInstance().stopMeasurement();
}

extern "C" JNIEXPORT
void JNICALL Java_at_alladin_nettest_nntool_android_app_workflow_measurement_jni_JniSpeedMeasurementClient_shareMeasurementState (JNIEnv* env, jobject caller, jobject speedTaskDesc,
                    jobject baseMeasurementState, jobject pingMeasurementState, jobject downloadMeasurementState, jobject uploadMeasurementState) {
    AndroidConnector &connector = AndroidConnector::getInstance();
    connector.registerSharedObject(env, caller, baseMeasurementState, pingMeasurementState, downloadMeasurementState, uploadMeasurementState);
    connector.setSpeedSettings(env, speedTaskDesc);
}

extern "C" JNIEXPORT
void JNICALL Java_at_alladin_nettest_nntool_android_app_workflow_measurement_jni_JniSpeedMeasurementClient_cleanUp (JNIEnv* env, jobject caller) {
    AndroidConnector::getInstance().unregisterSharedObject();
}

jint AndroidConnector::jniLoad(JavaVM* vm) {
    JNIEnv* env;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR; // JNI version not supported.
    }

    jclass clazz = env->FindClass("at/alladin/nettest/nntool/android/app/workflow/measurement/jni/JniSpeedMeasurementClient");
    jniHelperClass = (jclass) env->NewGlobalRef(clazz);
    callbackID = env->GetMethodID(jniHelperClass, "cppCallback", "(Ljava/lang/String;)V");
    cppCallbackFinishedID = env->GetMethodID(jniHelperClass, "cppCallbackFinished", "(Ljava/lang/String;)V");

    //get the fields for the SpeedphaseState
    clazz = env->FindClass("at/alladin/nettest/nntool/android/app/workflow/measurement/SpeedMeasurementState$SpeedPhaseState");
    fieldAvgThroughput = env->GetFieldID(clazz, "throughputAvgBps", "J");
    fieldDurationMsTotal = env->GetFieldID(clazz, "durationMsTotal", "J");
    fieldDurationMs = env->GetFieldID(clazz, "durationMs", "J");

    //get the fields for the PingPhaseState
    clazz = env->FindClass("at/alladin/nettest/nntool/android/app/workflow/measurement/SpeedMeasurementState$PingPhaseState");
    fieldAverageMs = env->GetFieldID(clazz, "averageMs", "J");

    //get the fields for the SpeedMeasurementState
    clazz = env->FindClass("at/alladin/nettest/nntool/android/app/workflow/measurement/SpeedMeasurementState");
    fieldProgress = env->GetFieldID(clazz, "progress", "F");

    setMeasurementPhaseByStringValueID = env->GetMethodID(clazz, "setMeasurementPhaseByStringValue", "(Ljava/lang/String;)V");


    //according to the google examples, we can keep the reference to the javaVM until android takes it away from us
    javaVM = vm;

    __android_log_write(ANDROID_LOG_DEBUG, TAG, std::string("Jni on load called").c_str());

    return JNI_VERSION_1_6;
}

void AndroidConnector::registerSharedObject(JNIEnv* env, jobject caller, jobject baseMeasurementState, jobject pingMeasurementState, jobject downloadMeasurementState, jobject uploadMeasurementState) {
    jniCaller = env->NewGlobalRef(caller);

    this->baseMeasurementState = env->NewGlobalRef(baseMeasurementState);
    this->downloadMeasurementState = env->NewGlobalRef(downloadMeasurementState);
    this->uploadMeasurementState = env->NewGlobalRef(uploadMeasurementState);
    this->pingMeasurementState = env->NewGlobalRef(pingMeasurementState);
}

void AndroidConnector::setSpeedSettings(JNIEnv* env, jobject speedTaskDesc) {
    const jclass clazz = env->FindClass("at/alladin/nettest/nntool/android/app/util/LmapUtil$SpeedTaskDesc");

    jfieldID toParseId = env->GetFieldID(clazz, "speedServerAddrV4", "Ljava/lang/String;");
    const jstring serverUrl = (jstring) env->GetObjectField(speedTaskDesc, toParseId);
    const char *url = env->GetStringUTFChars(serverUrl, NULL);
    measurementServerUrl = std::string(url);

    toParseId = env->GetFieldID(clazz, "rttCount", "I");
    rttCount = (int) env->GetIntField(speedTaskDesc, toParseId);

    toParseId = env->GetFieldID(clazz, "downloadStreams", "I");
    downloadStreams = (int) env->GetIntField(speedTaskDesc, toParseId);

    toParseId = env->GetFieldID(clazz, "uploadStreams", "I");
    uploadStreams = (int) env->GetIntField(speedTaskDesc, toParseId);
}

void AndroidConnector::unregisterSharedObject() {
    JNIEnv* env;
    jint err = javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);
    if (err == JNI_EDETACHED) {
        //std::cout << "GetEnv: not attached" << std::endl;
        if (javaVM->AttachCurrentThread(&env, NULL) != 0) {
            return;
            //std::cout << "Failed to attach" << std::endl;
        }
    } else if (err != JNI_OK) {
        return;
    }

    //clean up the global jni references
    if (baseMeasurementState != nullptr) {
        env->DeleteGlobalRef(baseMeasurementState);
        baseMeasurementState = nullptr;
    }
    if (downloadMeasurementState != nullptr) {
        env->DeleteGlobalRef(downloadMeasurementState);
        downloadMeasurementState = nullptr;
    }
    if (uploadMeasurementState != nullptr) {
        env->DeleteGlobalRef(uploadMeasurementState);
        uploadMeasurementState = nullptr;
    }
    if (pingMeasurementState != nullptr) {
        env->DeleteGlobalRef(pingMeasurementState);
        pingMeasurementState = nullptr;
    }
    if (jniCaller != nullptr) {
        env->DeleteGlobalRef(jniCaller);
        jniCaller = nullptr;
    }
}

void AndroidConnector::callback(json11::Json::object& message) const {
    JNIEnv* env;
    jint err = javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);
    if (err == JNI_EDETACHED) {
        if (javaVM->AttachCurrentThread(&env, NULL) != 0) {
            return;
        }
    } else if (err != JNI_OK) {
        return;
    }

    env->CallVoidMethod(baseMeasurementState, setMeasurementPhaseByStringValueID, env->NewStringUTF(getStringForMeasurementPhase(currentTestPhase).c_str()));

    //parse the json for now
    if (currentTestPhase == MeasurementPhase::PING) {
        if (message["rtt_udp_info"].is_object()) {
            const json11::Json recentResult = message["rtt_udp_info"];
            passJniSpeedState(env, MeasurementPhase::PING, recentResult);
        } else {
            env->SetFloatField(baseMeasurementState, fieldProgress, 0.0f);
        }
    }

    //get most recent download result
    if (currentTestPhase == MeasurementPhase::DOWNLOAD) {

        if (message["download_info"].is_array()) {
            const json11::Json::array dlInfo = message["download_info"].array_items();
            const json11::Json recentResult = dlInfo.at(dlInfo.size() -1);
            passJniSpeedState(env, MeasurementPhase::DOWNLOAD, recentResult);
        } else {
            env->SetFloatField(baseMeasurementState, fieldProgress, 0.0f);
        }
    }
    
	if (currentTestPhase == MeasurementPhase::UPLOAD) {
        if (message["upload_info"].is_array()) {
            const json11::Json::array ulInfo = message["upload_info"].array_items();
            const json11::Json recentResult = ulInfo.at(ulInfo.size() -1);
            passJniSpeedState(env, MeasurementPhase::UPLOAD, recentResult);
        } else {
            env->SetFloatField(baseMeasurementState, fieldProgress, 0.0f);
        }
    }    
    
    const jstring javaMsg = env->NewStringUTF("callback");//json11::Json(message).dump().c_str());
    env->CallVoidMethod(jniCaller, callbackID, javaMsg);

}

void AndroidConnector::callbackFinished (const json11::Json::object& message) {

    JNIEnv* env;
    jint err = javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);
    if (err == JNI_EDETACHED) {
        if (javaVM->AttachCurrentThread(&env, NULL) != 0) {
            return;
        }
    } else if (err != JNI_OK) {
        return;
    }

    const jstring javaMsg = env->NewStringUTF(json11::Json(message).dump().c_str());
    env->CallVoidMethod(jniCaller, cppCallbackFinishedID, javaMsg);

    if (baseMeasurementState != nullptr) {
        env->SetFloatField(baseMeasurementState, fieldProgress, 1);
        env->CallVoidMethod(baseMeasurementState, setMeasurementPhaseByStringValueID, env->NewStringUTF(getStringForMeasurementPhase(MeasurementPhase::END).c_str()));
    }

    unregisterSharedObject();
}

void AndroidConnector::callbackError(const int errorCode, const std::string &errorMessage) const {
    //TODO: error handling
}

void AndroidConnector::passJniSpeedState (JNIEnv* env, const MeasurementPhase& speedStateToSet, const json11::Json& json) const {
    jobject toFill = nullptr;
    json11::Json obj;

    switch (speedStateToSet) {
    case MeasurementPhase::PING:
        toFill = pingMeasurementState;
        obj = json["average_ns"];
        printLog(json.dump());
        if (obj.is_string()) {
        //    env->SetLongField(toFill, fieldAverageMs, std::stoll(obj.string_value()) / 1e6);
        }
        break;
    case MeasurementPhase::DOWNLOAD:
        toFill = downloadMeasurementState;
    case MeasurementPhase::UPLOAD:
        if (toFill == nullptr) {
            toFill = uploadMeasurementState;
        }
        //they're all strings
        obj = json["throughput_avg_bps"];
        if (obj.is_string()) {
            env->SetLongField(toFill, fieldAvgThroughput, std::stoll(obj.string_value()) / 1e6);
        }

        break;
    }

    obj = json["progress"];
    if (obj.is_number()) {
        env->SetFloatField(baseMeasurementState, fieldProgress, obj.number_value());
    }

}

void AndroidConnector::printLog(const std::string& message) const {
    __android_log_write(ANDROID_LOG_DEBUG, TAG, message.c_str());
}


void AndroidConnector::startMeasurement() {
    CTrace::setLogFunction(std::bind(&AndroidConnector::printLog, this, std::placeholders::_1));

    //init from ias-client

    ::DEBUG 			= false;
	::RUNNING 			= true;

	::RTT				= true;
	::DOWNLOAD 			= true;
	::UPLOAD 			= true;

	json11::Json::object jRttParameters;
	json11::Json::object jDownloadParameters;
	json11::Json::object jUploadParameters;

	json11::Json::object jMeasurementParameters;

	//set requested test cases
	jRttParameters["performMeasurement"] = ::RTT;
	jDownloadParameters["performMeasurement"] = ::DOWNLOAD;
	jUploadParameters["performMeasurement"] = ::UPLOAD;

	//set default measurement parameters
	jDownloadParameters["streams"] = std::to_string(downloadStreams);
	jUploadParameters["streams"] = std::to_string(uploadStreams);
	jMeasurementParameters["rtt"] = json11::Json(jRttParameters);
	jMeasurementParameters["download"] = json11::Json(jDownloadParameters);
	jMeasurementParameters["upload"] = json11::Json(jUploadParameters);

	jMeasurementParameters["platform"] = "desktop";
	jMeasurementParameters["clientos"] = "linux";
	jMeasurementParameters["wsTLD"] = "net-neutrality.tools";
	jMeasurementParameters["wsTargetPort"] = "80";
	jMeasurementParameters["wsWss"] = "0";
	jMeasurementParameters["wsAuthToken"] = "placeholderToken";
	jMeasurementParameters["wsAuthTimestamp"] = "placeholderTimestamp";

	json11::Json::array jTargets;
	jTargets.push_back(measurementServerUrl);
	jMeasurementParameters["wsTargets"] = json11::Json(jTargets);
	jMeasurementParameters["wsTargetsRtt"] = json11::Json(jTargets);

	json11::Json jMeasurementParametersJson = jMeasurementParameters;

	measurementStart(jMeasurementParametersJson.dump());

    //end init from ias-client

    AndroidConnector::unregisterSharedObject();
}

