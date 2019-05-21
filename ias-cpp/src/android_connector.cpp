#include "android_connector.h"
#include "../../ias-libnntool/json11.hpp"
#include "trace.h"
#include <vector>
#include <android/log.h>

const char* AndroidConnector::TAG = "cpp";

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {

    return AndroidConnector::getInstance().jniLoad(vm);
}

extern "C" JNIEXPORT void JNICALL Java_at_alladin_nettest_nntool_android_app_workflow_measurement_jni_JniSpeedMeasurementClient_startMeasurement (JNIEnv* env, jobject thiz) {
    AndroidConnector::getInstance().startMeasurement();
}

extern "C" JNIEXPORT void JNICALL Java_at_alladin_nettest_nntool_android_app_workflow_measurement_jni_JniSpeedMeasurementClient_stopMeasurement (JNIEnv* env, jobject thiz) {
    //AndroidConnector::getInstance().stopMeasurement();
}

extern "C" JNIEXPORT void JNICALL Java_at_alladin_nettest_nntool_android_app_workflow_measurement_jni_JniSpeedMeasurementClient_shareMeasurementState (JNIEnv* env, jobject caller,
                    jobject downloadMeasurementState, jobject uploadMeasurementState) {
    AndroidConnector::getInstance().registerSharedObject(env, caller, downloadMeasurementState, uploadMeasurementState);
}

jint AndroidConnector::jniLoad(JavaVM* vm) {
    JNIEnv* env;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR; // JNI version not supported.
    }

    jclass clazz = env->FindClass("at/alladin/nettest/nntool/android/app/workflow/measurement/jni/JniSpeedMeasurementClient");
    jniHelperClass = (jclass) env->NewGlobalRef(clazz);
    //according to the google examples, we can keep the reference to the javaVM until android takes it away from us
    javaVM = vm;
//    env->RegisterNatives(jniHelperClass, jniMethods, sizeof(jniMethods));
    //jniHelperClass = env->NewGlobalRef(clz);

    //jniHelperObj = env->NewGlobalRef(env, )

    __android_log_write(ANDROID_LOG_DEBUG, TAG, std::string("this is the android log function speaking").c_str());

    return JNI_VERSION_1_6;
}

void AndroidConnector::registerSharedObject(JNIEnv* env, jobject caller, jobject downloadMeasurementState, jobject uploadMeasurementState) {
    jclass classState = env->FindClass("at/alladin/nettest/nntool/android/app/workflow/measurement/jni/JniSpeedMeasurementState$JniSpeedState");
    jniCaller = env->NewGlobalRef(caller);

    downloadMeasurementState = env->NewGlobalRef(downloadMeasurementState);
    uploadMeasurementState = env->NewGlobalRef(uploadMeasurementState);

    fieldAvgDownloadThroughput = env->GetFieldID(classState, "throughputAvgBps", "J");
    fieldDurationMsTotal = env->GetFieldID(classState, "durationMsTotal", "J");
    fieldDurationMs = env->GetFieldID(classState, "durationMs", "J");
}

void AndroidConnector::unregisterSharedObject() {
    JNIEnv* env;
    JavaVM* vm = javaVM;
    jint err = vm->GetEnv((void**)&env, JNI_VERSION_1_6);
    if (err == JNI_EDETACHED) {
        //std::cout << "GetEnv: not attached" << std::endl;
        if (vm->AttachCurrentThread(&env, NULL) != 0) {
            return;
            //std::cout << "Failed to attach" << std::endl;
        }
    } else if (err != JNI_OK) {
        return;
    }

    //clean up the global jni references
    if (downloadMeasurementState != nullptr) {
        env->DeleteGlobalRef(downloadMeasurementState);
        downloadMeasurementState = nullptr;
    }
    if (jniCaller != nullptr) {
        env->DeleteGlobalRef(jniCaller);
        jniCaller = nullptr;
    }
}

void AndroidConnector::callback(json11::Json::object& message) {
    JNIEnv* env;
    JavaVM* vm = AndroidConnector::javaVM;
    jint err = vm->GetEnv((void**)&env, JNI_VERSION_1_6);
    if (err == JNI_EDETACHED) {
        //std::cout << "GetEnv: not attached" << std::endl;
        if (vm->AttachCurrentThread(&env, NULL) != 0) {
            return;
            //std::cout << "Failed to attach" << std::endl;
        }
    } else if (err != JNI_OK) {
        return;
    }
    const jmethodID callbackID = env->GetMethodID(jniHelperClass, "cppCallback", "(Ljava/lang/String;)V");

    //parse the json for now
    if (message["rtt_udp_info"].is_array()) {
        const json11::Json::array rttInfo = message["rtt_udp_info"].array_items();
        const json11::Json recentResult = rttInfo.at(rttInfo.size() -1);

    }

    //get most recent download result
    if (message["download_info"].is_array()) {
        const json11::Json::array dlInfo = message["download_info"].array_items();
        const json11::Json recentResult = dlInfo.at(dlInfo.size() -1);
        passJniSpeedState(env, DOWNLOAD, recentResult);
    }
    
	if (message["upload_info"].is_array()) {
        const json11::Json::array ulInfo = message["download_info"].array_items();
        const json11::Json recentResult = ulInfo.at(ulInfo.size() -1);
        passJniSpeedState(env, UPLOAD, recentResult);
    }    
    
    const jstring javaMsg = env->NewStringUTF(json11::Json(message).dump().c_str());
    env->CallVoidMethod(jniCaller, callbackID, javaMsg);

    
}

void AndroidConnector::passJniSpeedState (JNIEnv* env, SpeedState speedStateToSet, const json11::Json& json) const {
    jobject toFill = speedStateToSet == DOWNLOAD ? downloadMeasurementState : uploadMeasurementState;

    if (toFill == nullptr) {
        return;
    }

    //they're all strings
    json11::Json obj = json["duration_ns_total"];
    if (obj.is_string()) {
        env->SetLongField(toFill, fieldDurationMsTotal, std::stoll(obj.string_value()) / 1e6);
    }

    obj = json["throughput_avg_bps"];
    if (obj.is_string()) {
        env->SetLongField(toFill, fieldAvgDownloadThroughput, std::stol(obj.string_value()) / 1e6);
    }

    obj = json["duration_ns"];
    if (obj.is_string()) {
        env->SetLongField(toFill, fieldDurationMs, std::stoll(obj.string_value()) / 1e6);
    }

}

void AndroidConnector::printLog(const std::string& message) {
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
	jDownloadParameters["streams"] = "1";
	jUploadParameters["streams"] = "1";
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
	jTargets.push_back("peer-ias-de-01");
	jMeasurementParameters["wsTargets"] = json11::Json(jTargets);
	jMeasurementParameters["wsTargetsRtt"] = json11::Json(jTargets);

	json11::Json jMeasurementParametersJson = jMeasurementParameters;

	measurementStart(jMeasurementParametersJson.dump());

    //end init from ias-client

    AndroidConnector::unregisterSharedObject();
}

