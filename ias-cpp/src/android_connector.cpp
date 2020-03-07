/*!
    \file android_connector.cpp
    \author zafaco GmbH <info@zafaco.de>
    \author alladin-IT GmbH <info@alladin.at>
    \date Last update: 2020-03-06

    Copyright (C) 2016 - 2020 zafaco GmbH
    Copyright (C) 2019 alladin-IT GmbH

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

#include "android_connector.h"
#include "../../ias-libnntool/json11.hpp"
#include "trace.h"
#include <vector>
#include <android/log.h>

const char* AndroidConnector::TAG = "cpp";

extern "C" JNIEXPORT
jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
    __android_log_write(ANDROID_LOG_DEBUG, AndroidConnector::TAG, "jni on load executed");
    return AndroidConnector::getInstance().jniLoad(vm);
}

extern "C" JNIEXPORT
void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
    __android_log_write(ANDROID_LOG_DEBUG, AndroidConnector::TAG, "jni on unload executed");
    return AndroidConnector::getInstance().unregisterSharedObject();
}

extern "C" JNIEXPORT
void JNICALL Java_com_zafaco_speed_jni_JniSpeedMeasurementClient_startMeasurement (JNIEnv* env, jobject thiz) {
    AndroidConnector::getInstance().startMeasurement();
}

extern "C" JNIEXPORT
void JNICALL Java_com_zafaco_speed_jni_JniSpeedMeasurementClient_stopMeasurement (JNIEnv* env, jobject thiz) {
    AndroidConnector::getInstance().stopMeasurement();
}

extern "C" JNIEXPORT
void JNICALL Java_com_zafaco_speed_jni_JniSpeedMeasurementClient_shareMeasurementState (JNIEnv* env, jobject caller, jobject speedTaskDesc,
                    jobject baseMeasurementState, jobject pingMeasurementState, jobject downloadMeasurementState, jobject uploadMeasurementState) {
    AndroidConnector &connector = AndroidConnector::getInstance();
    connector.registerSharedObject(env, caller, baseMeasurementState, pingMeasurementState, downloadMeasurementState, uploadMeasurementState);
    connector.setSpeedSettings(env, speedTaskDesc);
}

extern "C" JNIEXPORT
void JNICALL Java_com_zafaco_speed_jni_JniSpeedMeasurementClient_cleanUp (JNIEnv* env, jobject caller) {
    AndroidConnector::getInstance().unregisterSharedObject();
}


jint AndroidConnector::jniLoad(JavaVM* vm) {
    JNIEnv* env;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR; // JNI version not supported.
    }

    jclass clazz = env->FindClass("com/zafaco/speed/jni/JniSpeedMeasurementClient");
    jniHelperClass = (jclass) env->NewGlobalRef(clazz);
    callbackID = env->GetMethodID(jniHelperClass, "cppCallback", "(Ljava/lang/String;)V");
    cppCallbackFinishedID = env->GetMethodID(jniHelperClass, "cppCallbackFinished", "(Ljava/lang/String;Lcom/zafaco/speed/JniSpeedMeasurementResult;)V");

    clazz = env->FindClass("com/zafaco/speed/jni/exception/AndroidJniCppException");
    jniExceptionClass = (jclass) env->NewGlobalRef(clazz);

    //get the fields for the SpeedphaseState
    clazz = env->FindClass("com/zafaco/speed/SpeedMeasurementState$SpeedPhaseState");
    fieldAvgThroughput = env->GetFieldID(clazz, "throughputAvgBps", "J");

    //get the fields for the PingPhaseState
    clazz = env->FindClass("com/zafaco/speed/SpeedMeasurementState$PingPhaseState");
    fieldAverageMs = env->GetFieldID(clazz, "averageMs", "J");

    //get the fields for the SpeedMeasurementState
    clazz = env->FindClass("com/zafaco/speed/SpeedMeasurementState");
    fieldProgress = env->GetFieldID(clazz, "progress", "F");

    setMeasurementPhaseByStringValueID = env->GetMethodID(clazz, "setMeasurementPhaseByStringValue", "(Ljava/lang/String;)V");

    clazz = env->FindClass("com/zafaco/speed/JniSpeedMeasurementResult");
    speedMeasurementResultClazz = (jclass) env->NewGlobalRef(clazz);
    clazz = env->FindClass("com/zafaco/speed/JniSpeedMeasurementResult$RttUdpResult");
    resultUdpClazz = (jclass) env->NewGlobalRef(clazz);
    clazz = env->FindClass("com/zafaco/speed/JniSpeedMeasurementResult$BandwidthResult");
    resultBandwidthClazz = (jclass) env->NewGlobalRef(clazz);
    clazz = env->FindClass("com/zafaco/speed/JniSpeedMeasurementResult$TimeInfo");
    timeClazz = (jclass) env->NewGlobalRef(clazz);

    //according to the google examples, we can keep the reference to the javaVM until android takes it away from us
    javaVM = vm;

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
    const jclass clazz = env->FindClass("com/zafaco/speed/SpeedTaskDesc");

    jfieldID toParseId = env->GetFieldID(clazz, "speedServerAddrV4", "Ljava/lang/String;");
    jstring serverUrl = (jstring) env->GetObjectField(speedTaskDesc, toParseId);
    if (serverUrl != nullptr) {
        const char * url = env->GetStringUTFChars(serverUrl, NULL);
        this->speedTaskDesc.measurementServerUrlV4 = std::string(url);
    }

    toParseId = env->GetFieldID(clazz, "speedServerAddrV6", "Ljava/lang/String;");
    serverUrl = (jstring) env->GetObjectField(speedTaskDesc, toParseId);
    if (serverUrl != nullptr) {
        const char * urlv6 = env->GetStringUTFChars(serverUrl, NULL);
        this->speedTaskDesc.measurementServerUrlV6 = std::string(urlv6);
    }

    toParseId = env->GetFieldID(clazz, "rttCount", "I");
    this->speedTaskDesc.rttCount = (int) env->GetIntField(speedTaskDesc, toParseId);

    toParseId = env->GetFieldID(clazz, "downloadStreams", "I");
    this->speedTaskDesc.downloadStreams = (int) env->GetIntField(speedTaskDesc, toParseId);

    toParseId = env->GetFieldID(clazz, "uploadStreams", "I");
    this->speedTaskDesc.uploadStreams = (int) env->GetIntField(speedTaskDesc, toParseId);

    toParseId = env->GetFieldID(clazz, "speedServerPort", "I");
    this->speedTaskDesc.speedServerPort = (int) env->GetIntField(speedTaskDesc, toParseId);

    toParseId = env->GetFieldID(clazz, "performDownload", "Z");
    this->speedTaskDesc.performDownload = env->GetBooleanField(speedTaskDesc, toParseId);

    toParseId = env->GetFieldID(clazz, "performUpload", "Z");
    this->speedTaskDesc.performUpload = env->GetBooleanField(speedTaskDesc, toParseId);

    toParseId = env->GetFieldID(clazz, "performRtt", "Z");
    this->speedTaskDesc.performRtt = env->GetBooleanField(speedTaskDesc, toParseId);

    toParseId = env->GetFieldID(clazz, "useEncryption", "Z");
    this->speedTaskDesc.isEncrypted = env->GetBooleanField(speedTaskDesc, toParseId);

    toParseId = env->GetFieldID(clazz, "useIpV6", "Z");
    this->speedTaskDesc.useIpv6 = env->GetBooleanField(speedTaskDesc, toParseId);

    toParseId = env->GetFieldID(clazz, "clientIp", "Ljava/lang/String;");
    serverUrl = (jstring) env->GetObjectField(speedTaskDesc, toParseId);
    if (serverUrl != nullptr) {
        const char * clientIp = env->GetStringUTFChars(serverUrl, NULL);
        this->speedTaskDesc.clientIp = std::string(clientIp);
    } else {
        this->speedTaskDesc.clientIp = "0.0.0.0";
    }

}

void AndroidConnector::unregisterSharedObject() {
    JNIEnv* env = getJniEnv();
    if (env == nullptr) {
        return;
    }

    //clean up the global jni references
    if (jniExceptionClass != nullptr) {
        env->DeleteGlobalRef(jniExceptionClass);
        jniExceptionClass = nullptr;
    }
    if (jniHelperClass != nullptr) {
        env->DeleteGlobalRef(jniHelperClass);
        jniHelperClass = nullptr;
    }
    if (speedMeasurementResultClazz != nullptr) {
        env->DeleteGlobalRef(speedMeasurementResultClazz);
        speedMeasurementResultClazz = nullptr;
    }
    if (resultUdpClazz != nullptr) {
        env->DeleteGlobalRef(resultUdpClazz);
        resultUdpClazz = nullptr;
    }
    if (resultBandwidthClazz != nullptr) {
        env->DeleteGlobalRef(resultBandwidthClazz);
        resultBandwidthClazz = nullptr;
    }
    if (timeClazz != nullptr) {
        env->DeleteGlobalRef(timeClazz);
        timeClazz = nullptr;
    }
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
    JNIEnv* env = getJniEnv();
    if (env == nullptr) {
        return;
    }

    jmethodID initId = env->GetMethodID(speedMeasurementResultClazz, "<init>", "()V");

    jobject speedMeasurementResult = env->NewObject(speedMeasurementResultClazz, initId);

    env->CallVoidMethod(baseMeasurementState, setMeasurementPhaseByStringValueID, env->NewStringUTF(getStringForMeasurementPhase(currentTestPhase).c_str()));

    //parse the json for now
    if (currentTestPhase == MeasurementPhase::PING) {
        if (message["rtt_udp_info"].is_object()) {
            json11::Json const recentResult = message["rtt_udp_info"];
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

    const jstring javaMsg = env->NewStringUTF(json11::Json(message).dump().c_str());
    env->CallVoidMethod(jniCaller, callbackID, javaMsg, speedMeasurementResult);

    env->CallVoidMethod(jniCaller, callbackID, env->NewStringUTF(""));

}

void AndroidConnector::callbackFinished (json11::Json::object& message) {

    JNIEnv* env = getJniEnv();
    if (env == nullptr) {
        return;
    }

    //parse result
    jmethodID initId = env->GetMethodID(speedMeasurementResultClazz, "<init>", "()V");

    jobject speedMeasurementResult = env->NewObject(speedMeasurementResultClazz, initId);

    JavaParseInformation parse;

    parse.longClass = env->FindClass("java/lang/Long");
    parse.staticLongValueOf = env->GetStaticMethodID(parse.longClass, "valueOf", "(J)Ljava/lang/Long;");

    parse.intClass = env->FindClass("java/lang/Integer");
    parse.staticIntValueOf = env->GetStaticMethodID(parse.intClass, "valueOf", "(I)Ljava/lang/Integer;");

    parse.floatClass = env->FindClass("java/lang/Float");
    parse.staticFloatValueOf = env->GetStaticMethodID(parse.floatClass, "valueOf", "(F)Ljava/lang/Float;");

    if (message["rtt_udp_info"].is_object()) {
        jmethodID setMethod = env->GetMethodID(speedMeasurementResultClazz, "setRttUdpResult", "(Lcom/zafaco/speed/JniSpeedMeasurementResult$RttUdpResult;)V");
        initId = env->GetMethodID(resultUdpClazz, "<init>", "()V");

        Json const & rttEntry = message["rtt_udp_info"];
        jobject singleResult = env->NewObject(resultUdpClazz, initId);

        jmethodID setId = env->GetMethodID(resultUdpClazz, "setAverageNs", "(Ljava/lang/Long;)V");
        jobject obj = env->CallStaticObjectMethod(parse.longClass, parse.staticLongValueOf, std::stoll(rttEntry["average_ns"].string_value()));
        env->CallVoidMethod(singleResult, setId, obj);
        env->DeleteLocalRef(obj);

        setId = env->GetMethodID(resultUdpClazz, "setDurationNs", "(Ljava/lang/Long;)V");
        obj = env->CallStaticObjectMethod(parse.longClass, parse.staticLongValueOf, std::stoll(rttEntry["duration_ns"].string_value()));
        env->CallVoidMethod(singleResult, setId, obj);
        env->DeleteLocalRef(obj);

        setId = env->GetMethodID(resultUdpClazz, "setMaxNs", "(Ljava/lang/Long;)V");
        obj = env->CallStaticObjectMethod(parse.longClass, parse.staticLongValueOf, std::stoll(rttEntry["max_ns"].string_value()));
        env->CallVoidMethod(singleResult, setId, obj);
        env->DeleteLocalRef(obj);

        setId = env->GetMethodID(resultUdpClazz, "setMedianNs", "(Ljava/lang/Long;)V");
        obj = env->CallStaticObjectMethod(parse.longClass, parse.staticLongValueOf, std::stoll(rttEntry["median_ns"].string_value()));
        env->CallVoidMethod(singleResult, setId, obj);
        env->DeleteLocalRef(obj);

        setId = env->GetMethodID(resultUdpClazz, "setMinNs", "(Ljava/lang/Long;)V");
        obj = env->CallStaticObjectMethod(parse.longClass, parse.staticLongValueOf, std::stoll(rttEntry["min_ns"].string_value()));
        env->CallVoidMethod(singleResult, setId, obj);
        env->DeleteLocalRef(obj);

        setId = env->GetMethodID(resultUdpClazz, "setNumError", "(Ljava/lang/Integer;)V");
        obj = env->CallStaticObjectMethod(parse.intClass, parse.staticIntValueOf, std::stol(rttEntry["num_error"].string_value()));
        env->CallVoidMethod(singleResult, setId, obj);
        env->DeleteLocalRef(obj);

        setId = env->GetMethodID(resultUdpClazz, "setNumMissing", "(Ljava/lang/Integer;)V");
        obj = env->CallStaticObjectMethod(parse.intClass, parse.staticIntValueOf, std::stol(rttEntry["num_missing"].string_value()));
        env->CallVoidMethod(singleResult, setId, obj);
        env->DeleteLocalRef(obj);

        setId = env->GetMethodID(resultUdpClazz, "setNumReceived", "(Ljava/lang/Integer;)V");
        obj = env->CallStaticObjectMethod(parse.intClass, parse.staticIntValueOf, std::stol(rttEntry["num_received"].string_value()));
        env->CallVoidMethod(singleResult, setId, obj);
        env->DeleteLocalRef(obj);

        setId = env->GetMethodID(resultUdpClazz, "setNumSent", "(Ljava/lang/Integer;)V");
        obj = env->CallStaticObjectMethod(parse.intClass, parse.staticIntValueOf, std::stol(rttEntry["num_sent"].string_value()));
        env->CallVoidMethod(singleResult, setId, obj);
        env->DeleteLocalRef(obj);

        setId = env->GetMethodID(resultUdpClazz, "setPacketSize", "(Ljava/lang/Integer;)V");
        obj = env->CallStaticObjectMethod(parse.intClass, parse.staticIntValueOf, std::stol(rttEntry["packet_size"].string_value()));
        env->CallVoidMethod(singleResult, setId, obj);
        env->DeleteLocalRef(obj);

        setId = env->GetMethodID(resultUdpClazz, "setPeer", "(Ljava/lang/String;)V");
        obj = env->NewStringUTF(rttEntry["peer"].string_value().c_str());
        env->CallVoidMethod(singleResult, setId, obj);
        env->DeleteLocalRef(obj);

        setId = env->GetMethodID(resultUdpClazz, "setProgress", "(Ljava/lang/Float;)V");
        obj = env->CallStaticObjectMethod(parse.floatClass, parse.staticFloatValueOf, rttEntry["progress"].number_value());
        env->CallVoidMethod(singleResult, setId, obj);
        env->DeleteLocalRef(obj);

        setId = env->GetMethodID(resultUdpClazz, "setStandardDeviationNs", "(Ljava/lang/Long;)V");
        obj = env->CallStaticObjectMethod(parse.longClass, parse.staticLongValueOf, std::stoll(rttEntry["standard_deviation_ns"].string_value()));
        env->CallVoidMethod(singleResult, setId, obj);
        env->DeleteLocalRef(obj);

        jmethodID addMethod = env->GetMethodID(resultUdpClazz, "addSingleRtt", "(Ljava/lang/Long;Ljava/lang/Long;)V");
        if (rttEntry["rtts"].is_array()) {
            jobject timeStampObj;
            for (json11::Json const & singleRtt : rttEntry["rtts"].array_items()) {
                long long rttValue = -1;
                if (singleRtt["rtt_ns"].is_number()) {
                    rttValue = (long long) singleRtt["rtt_ns"].int_value();
                }
                obj = rttValue != - 1 ? env->CallStaticObjectMethod(parse.longClass, parse.staticLongValueOf, rttValue) : nullptr;
                long long timeStamp = -1;
                if (singleRtt["relative_time_ns_measurement_start"].is_string()) {
                    timeStamp = std::stoll(singleRtt["relative_time_ns_measurement_start"].string_value());
                }
                timeStampObj = timeStamp != -1 ? env->CallStaticObjectMethod(parse.longClass, parse.staticLongValueOf, timeStamp) : nullptr;

                env->CallVoidMethod(singleResult, addMethod, obj, timeStampObj);
                env->DeleteLocalRef(obj);
                env->DeleteLocalRef(timeStampObj);
            }
        }
        env->CallVoidMethod(speedMeasurementResult, setMethod, singleResult);
    }

    if (message["download_info"].is_array()) {
        jmethodID addMethod = env->GetMethodID(speedMeasurementResultClazz, "addDownloadInfo", "(Lcom/zafaco/speed/JniSpeedMeasurementResult$BandwidthResult;)V");
        initId = env->GetMethodID(resultBandwidthClazz, "<init>", "()V");

        for (Json const & downloadEntry : message["download_info"].array_items()) {
            jobject singleResult = env->NewObject(resultBandwidthClazz, initId);
            setBandwidthResult(env, downloadEntry, singleResult, parse);
            env->CallVoidMethod(speedMeasurementResult, addMethod, singleResult);
        }
    }

    if (message["upload_info"].is_array()) {
        jmethodID addMethod = env->GetMethodID(speedMeasurementResultClazz, "addUploadInfo", "(Lcom/zafaco/speed/JniSpeedMeasurementResult$BandwidthResult;)V");
        initId = env->GetMethodID(resultBandwidthClazz, "<init>", "()V");

        for (Json const & downloadEntry : message["upload_info"].array_items()) {
            jobject singleResult = env->NewObject(resultBandwidthClazz, initId);
            setBandwidthResult(env, downloadEntry, singleResult, parse);
            env->CallVoidMethod(speedMeasurementResult, addMethod, singleResult);
        }
    }

    if (message["time_info"].is_object()) {
        Json const & timeEntry = message["time_info"];
        jmethodID addMethod = env->GetMethodID(speedMeasurementResultClazz, "setTimeInfo", "(Lcom/zafaco/speed/JniSpeedMeasurementResult$TimeInfo;)V");

        initId = env->GetMethodID(timeClazz, "<init>", "()V");
        jobject timeObj = env->NewObject(timeClazz, initId);
        jmethodID setId;

        jobject obj;

        if (timeEntry["download_start"].is_string()) {
            setId = env->GetMethodID(timeClazz, "setDownloadStart", "(Ljava/lang/Long;)V");
            jobject obj = env->CallStaticObjectMethod(parse.longClass, parse.staticLongValueOf, std::stoll(timeEntry["download_start"].string_value()));
            env->CallVoidMethod(timeObj, setId, obj);
            env->DeleteLocalRef(obj);
        }

        if (timeEntry["download_end"].is_string()) {
            setId = env->GetMethodID(timeClazz, "setDownloadEnd", "(Ljava/lang/Long;)V");
            obj = env->CallStaticObjectMethod(parse.longClass, parse.staticLongValueOf, std::stoll(timeEntry["download_end"].string_value()));
            env->CallVoidMethod(timeObj, setId, obj);
            env->DeleteLocalRef(obj);
        }

        if (timeEntry["rtt_udp_start"].is_string()) {
            setId = env->GetMethodID(timeClazz, "setRttUdpStart", "(Ljava/lang/Long;)V");
            obj = env->CallStaticObjectMethod(parse.longClass, parse.staticLongValueOf, std::stoll(timeEntry["rtt_udp_start"].string_value()));
            env->CallVoidMethod(timeObj, setId, obj);
            env->DeleteLocalRef(obj);
        }

        if (timeEntry["rtt_udp_end"].is_string()) {
            setId = env->GetMethodID(timeClazz, "setRttUdpEnd", "(Ljava/lang/Long;)V");
            obj = env->CallStaticObjectMethod(parse.longClass, parse.staticLongValueOf, std::stoll(timeEntry["rtt_udp_end"].string_value()));
            env->CallVoidMethod(timeObj, setId, obj);
            env->DeleteLocalRef(obj);
        }

        if (timeEntry["upload_start"].is_string()) {
            setId = env->GetMethodID(timeClazz, "setUploadStart", "(Ljava/lang/Long;)V");
            obj = env->CallStaticObjectMethod(parse.longClass, parse.staticLongValueOf, std::stoll(timeEntry["upload_start"].string_value()));
            env->CallVoidMethod(timeObj, setId, obj);
            env->DeleteLocalRef(obj);
        }

        if (timeEntry["upload_end"].is_string()) {
            setId = env->GetMethodID(timeClazz, "setUploadEnd", "(Ljava/lang/Long;)V");
            obj = env->CallStaticObjectMethod(parse.longClass, parse.staticLongValueOf, std::stoll(timeEntry["upload_end"].string_value()));
            env->CallVoidMethod(timeObj, setId, obj);
            env->DeleteLocalRef(obj);
        }

        env->CallVoidMethod(speedMeasurementResult, addMethod, timeObj);
    }

    jmethodID setId = env->GetMethodID(speedMeasurementResultClazz, "setMeasurementServerIp", "(Ljava/lang/String;)V");
    jstring str = env->NewStringUTF(this->measurementServerIp.c_str());
    env->CallVoidMethod(speedMeasurementResult, setId, str);
    env->DeleteLocalRef(str);

    const jstring javaMsg = env->NewStringUTF(json11::Json(message).dump().c_str());
    env->CallVoidMethod(jniCaller, cppCallbackFinishedID, javaMsg, speedMeasurementResult);

    if (baseMeasurementState != nullptr) {
        env->SetFloatField(baseMeasurementState, fieldProgress, 1);
        env->CallVoidMethod(baseMeasurementState, setMeasurementPhaseByStringValueID, env->NewStringUTF(getStringForMeasurementPhase(MeasurementPhase::END).c_str()));
    }

}

void AndroidConnector::callbackError(std::string message) {

    JNIEnv* env = getJniEnv();
    if (env == nullptr) {
        return;
    }
    env->ExceptionClear();
    if (env->ExceptionCheck()) {
        pendingErrorMessages.push_back(message);
    } else {
        env->ThrowNew(jniExceptionClass, message.c_str());
    }
}

void AndroidConnector::passJniSpeedState (JNIEnv* env, const MeasurementPhase& speedStateToSet, const json11::Json& json) const {
    jobject toFill = nullptr;
    json11::Json obj;

    switch (speedStateToSet) {
    case MeasurementPhase::PING:
        toFill = pingMeasurementState;
        obj = json["average_ns"];
        if (obj.is_string()) {
            env->SetLongField(toFill, fieldAverageMs, std::stoll(obj.string_value()) / 1e6);
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
            env->SetLongField(toFill, fieldAvgThroughput, std::stoll(obj.string_value()));
        }

        break;
    }

    obj = json["progress"];
    if (obj.is_number()) {
        env->SetFloatField(baseMeasurementState, fieldProgress, obj.number_value());
    }

}

void AndroidConnector::setBandwidthResult (JNIEnv * env, json11::Json const & jsonItems, jobject & result, JavaParseInformation & parseInfo) {
    jmethodID setId = env->GetMethodID(resultBandwidthClazz, "setBytes", "(Ljava/lang/Long;)V");
    jobject obj = env->CallStaticObjectMethod(parseInfo.longClass, parseInfo.staticLongValueOf, std::stoll(jsonItems["bytes"].string_value()));
    env->CallVoidMethod(result, setId, obj);
    env->DeleteLocalRef(obj);

    setId = env->GetMethodID(resultBandwidthClazz, "setBytesIncludingSlowStart", "(Ljava/lang/Long;)V");
    obj = env->CallStaticObjectMethod(parseInfo.longClass, parseInfo.staticLongValueOf, std::stoll(jsonItems["bytes_including_slow_start"].string_value()));
    env->CallVoidMethod(result, setId, obj);
    env->DeleteLocalRef(obj);

    setId = env->GetMethodID(resultBandwidthClazz, "setDurationNs", "(Ljava/lang/Long;)V");
    obj = env->CallStaticObjectMethod(parseInfo.longClass, parseInfo.staticLongValueOf, std::stoll(jsonItems["duration_ns"].string_value()));
    env->CallVoidMethod(result, setId, obj);
    env->DeleteLocalRef(obj);

    setId = env->GetMethodID(resultBandwidthClazz, "setDurationNsTotal", "(Ljava/lang/Long;)V");
    obj = env->CallStaticObjectMethod(parseInfo.longClass, parseInfo.staticLongValueOf, std::stoll(jsonItems["duration_ns_total"].string_value()));
    env->CallVoidMethod(result, setId, obj);
    env->DeleteLocalRef(obj);

    setId = env->GetMethodID(resultBandwidthClazz, "setThroughputAvgBps", "(Ljava/lang/Long;)V");
    obj = env->CallStaticObjectMethod(parseInfo.longClass, parseInfo.staticLongValueOf, std::stoll(jsonItems["throughput_avg_bps"].string_value()));
    env->CallVoidMethod(result, setId, obj);
    env->DeleteLocalRef(obj);

    setId = env->GetMethodID(resultBandwidthClazz, "setNumStreamsEnd", "(Ljava/lang/Integer;)V");
    obj = env->CallStaticObjectMethod(parseInfo.intClass, parseInfo.staticIntValueOf, std::stol(jsonItems["num_streams_end"].string_value()));
    env->CallVoidMethod(result, setId, obj);
    env->DeleteLocalRef(obj);

    setId = env->GetMethodID(resultBandwidthClazz, "setNumStreamsStart", "(Ljava/lang/Integer;)V");
    obj = env->CallStaticObjectMethod(parseInfo.intClass, parseInfo.staticIntValueOf, std::stol(jsonItems["num_streams_start"].string_value()));
    env->CallVoidMethod(result, setId, obj);
    env->DeleteLocalRef(obj);

    setId = env->GetMethodID(resultBandwidthClazz, "setProgress", "(Ljava/lang/Float;)V");
    obj = env->CallStaticObjectMethod(parseInfo.floatClass, parseInfo.staticFloatValueOf, jsonItems["progress"].number_value());
    env->CallVoidMethod(result, setId, obj);
    env->DeleteLocalRef(obj);

}

void AndroidConnector::printLog(std::string const & category, std::string const & message) const {
    android_LogPriority logPriority = ANDROID_LOG_INFO;

    if (category == "DEBUG") {
        logPriority = ANDROID_LOG_DEBUG;
    } else if (category == "INFO") {
        logPriority = ANDROID_LOG_INFO;
    } else if (category == "WARN") {
        logPriority = ANDROID_LOG_WARN;
    } else if (category == "CRITICAL") {
        logPriority = ANDROID_LOG_ERROR;
    } else if (category == "ERROR") {
        logPriority = ANDROID_LOG_FATAL;
    }
    __android_log_write(logPriority, TAG, message.c_str());
}


void AndroidConnector::startMeasurement() {
    try {
        CTrace::setLogFunction(std::bind(&AndroidConnector::printLog, this, std::placeholders::_1, std::placeholders::_2));

        //init from ias-client

        ::DEBUG 			= false;
        ::RUNNING 			= true;

        ::RTT				= speedTaskDesc.performRtt;
        ::DOWNLOAD 			= speedTaskDesc.performDownload;
        ::UPLOAD 			= speedTaskDesc.performUpload;

        json11::Json::object jRttParameters;
        json11::Json::object jDownloadParameters;
        json11::Json::object jUploadParameters;

        json11::Json::object jMeasurementParameters;

        //set requested test cases
        jRttParameters["performMeasurement"] = ::RTT;
        jDownloadParameters["performMeasurement"] = ::DOWNLOAD;
        jUploadParameters["performMeasurement"] = ::UPLOAD;

        //set default measurement parameters
        jDownloadParameters["streams"] = std::to_string(speedTaskDesc.downloadStreams);
        jUploadParameters["streams"] = std::to_string(speedTaskDesc.uploadStreams);
        jRttParameters["ping_query"] = std::to_string(speedTaskDesc.rttCount);
        jMeasurementParameters["rtt"] = json11::Json(jRttParameters);
        jMeasurementParameters["download"] = json11::Json(jDownloadParameters);
        jMeasurementParameters["upload"] = json11::Json(jUploadParameters);

        jMeasurementParameters["platform"] = "mobile";
        jMeasurementParameters["clientos"] = "android";
        jMeasurementParameters["wsTLD"] = "";
        jMeasurementParameters["wsTargetPort"] = std::to_string(speedTaskDesc.speedServerPort);
        jMeasurementParameters["wsWss"] = speedTaskDesc.isEncrypted ? "1" : "0";
        jMeasurementParameters["wsAuthToken"] = "placeholderToken";
        jMeasurementParameters["wsAuthTimestamp"] = "placeholderTimestamp";

        jMeasurementParameters["clientIp"] = this->speedTaskDesc.clientIp;

        json11::Json::array jTargets;
        if (speedTaskDesc.useIpv6) {
            TRC_INFO("Measuring with ipv6");
            jTargets.push_back(speedTaskDesc.measurementServerUrlV6);
        } else {
            TRC_INFO("Measuring with ipv4");
            jTargets.push_back(speedTaskDesc.measurementServerUrlV4);
        }
        jMeasurementParameters["wsTargets"] = json11::Json(jTargets);

        json11::Json jMeasurementParametersJson = jMeasurementParameters;

        measurementStart(jMeasurementParametersJson.dump());
    } catch (std::exception & ex) {
        shutdown();
        JNIEnv* env = getJniEnv();
        if (env == nullptr) {
            return;
        }
        env->ThrowNew(jniExceptionClass, ex.what());
    }

}


void AndroidConnector::stopMeasurement() {
    measurementStop();
}
