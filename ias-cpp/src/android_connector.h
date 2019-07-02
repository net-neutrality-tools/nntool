#ifndef CPP_CLIENT_ANDROID_INTERFACE_H
#define CPP_CLIENT_ANDROID_INTERFACE_H

#include <string>
#include <vector>
#include <map>
#include <jni.h>
#include <iostream>
#include "../../ias-libnntool/json11.hpp"
#include "type.h"

extern void measurementStart(std::string measurementParameters);

extern void measurementStop();

extern void startTestCase(int nTestCase);

extern void shutdown();

extern void show_usage(char* argv0);

extern bool DEBUG;
extern bool RUNNING;

extern const char* PLATFORM;
extern const char* CLIENT_OS;

extern unsigned long long TCP_STARTUP;

extern bool RTT;
extern bool DOWNLOAD;
extern bool UPLOAD;

extern bool TIMER_ACTIVE;
extern bool TIMER_RUNNING;
extern bool TIMER_STOPPED;

extern bool hasError;

extern int TIMER_INDEX;
extern int TIMER_DURATION;
extern unsigned long long MEASUREMENT_DURATION;

extern int NETTYPE;


extern struct conf_data conf;
extern struct measurement measurements;

extern std::vector<char> randomDataValues;

extern pthread_mutex_t mutex1;

extern std::map<int,int> syncing_threads;

extern class CConfigManager* pConfig;
extern class CConfigManager* pXml;
extern class CConfigManager* pService;

extern class CCallback *pCallback;
extern class CMeasurement* pMeasurement;

extern MeasurementPhase currentTestPhase;

extern std::function<void(int)> signalFunction;

class AndroidConnector {

    public:
        static AndroidConnector& getInstance() {
            static AndroidConnector instance;
            return instance;
        }

        static const char* TAG;

        void registerSharedObject(JNIEnv* env, jobject caller, jobject baseMeasurementState, jobject pingMeasurementState, jobject downloadMeasurementState, jobject uploadMeasurementState);

        void setSpeedSettings(JNIEnv* env, jobject speedTaskDesc);
        /**
        *   resets state and releases global java references made during setup
        *   this function is automatically called from the jni.onUnload event
        */
        void unregisterSharedObject();

        void startMeasurement();

        void stopMeasurement();

        jint jniLoad(JavaVM* vm);

        void callback(json11::Json::object& message) const;

        void callbackError(std::string message);

        void callbackFinished (json11::Json::object& message);
        /*
        * The method forwarded to the trace to allow for easy android printing
        */
        void printLog(const std::string& message) const;

        inline void detachCurrentThreadFromJvm() const {
            javaVM->DetachCurrentThread();
        }

        AndroidConnector(AndroidConnector const&) = delete;
        void operator=(AndroidConnector const&) = delete;
        AndroidConnector(AndroidConnector const&&) = delete;
        void operator=(AndroidConnector const&&) = delete;

    private:

        struct JavaParseInformation {
            jclass longClass;
            jmethodID staticLongValueOf;

            jclass intClass;
            jmethodID staticIntValueOf;

            jclass floatClass;
            jmethodID staticFloatValueOf;
        };

        JavaVM *javaVM;
        jclass jniHelperClass;
        jobject jniCaller;

        jclass jniExceptionClass;

        //the method to be sending the callback to
        jmethodID callbackID;
        jmethodID cppCallbackFinishedID;
        jmethodID setMeasurementPhaseByStringValueID;

        //the object to set the current progress state in
        jobject baseMeasurementState;
        jobject downloadMeasurementState;
        jobject uploadMeasurementState;
        jobject pingMeasurementState;

        //the field ids of the settable values
        //baseMeasurementFields
        jfieldID fieldProgress;
        
        //speedMeasurementFields
        jfieldID fieldAvgThroughput;

        //pingMeasurementFields
        jfieldID fieldAverageMs;

        //the settings from the SpeedTaskDesc
        std::string measurementServerUrlV4;
        std::string measurementServerUrlV6;
        int rttCount;
        int downloadStreams;
        int uploadStreams;
        int speedServerPort;
        bool performDownload;
        bool performUpload;
        bool performRtt;
        bool isEncrypted;

        //the classes for the final result
        jclass speedMeasurementResultClazz;
        jclass resultUdpClazz;
        jclass resultBandwidthClazz;
        jclass timeClazz;

        std::vector<std::string> pendingErrorMessages;

        AndroidConnector() {};

        void setBandwidthResult (JNIEnv * env, json11::Json const & jsonItems, jobject & result, JavaParseInformation & parseInfo);

        inline JNIEnv * getJniEnv() const {
            if (javaVM != nullptr) {
                JNIEnv* env;
                jint err = javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);
                if (err == JNI_EDETACHED) {
                    if (javaVM->AttachCurrentThread(&env, NULL) != 0) {
                        return nullptr;
                    }
                } else if (err != JNI_OK) {
                    return nullptr;
                }
                return env;
            }
            return nullptr;
        }

        void passJniSpeedState (JNIEnv *env, const MeasurementPhase& speedStateToSet, const json11::Json& json) const;

        static std::string getStringForMeasurementPhase(const MeasurementPhase &phase)  {
            switch (phase) {
                case MeasurementPhase::INIT:
                    return "INIT";
                case MeasurementPhase::PING:
                    return "PING";
                case MeasurementPhase::DOWNLOAD:
                    return "DOWNLOAD";
                case MeasurementPhase::UPLOAD:
                    return "UPLOAD";
                case MeasurementPhase::END:
                    return "END";
                
            }
        }

};

#endif //CPP_CLIENT_ANDROID_INTERFACE_H
