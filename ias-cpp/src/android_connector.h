#ifndef CPP_CLIENT_ANDROID_INTERFACE_H
#define CPP_CLIENT_ANDROID_INTERFACE_H

#include <string>
#include <map>
#include <jni.h>
#include <iostream>
#include "../../ias-libnntool/json11.hpp"

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

extern int TIMER_INDEX;
extern int TIMER_DURATION;
extern unsigned long long MEASUREMENT_DURATION;

extern int NETTYPE;


extern struct conf_data conf;
extern struct measurement measurements;

extern std::vector<char> randomDataValues;

extern pthread_mutex_t mutex1;

extern std::map<int,int> syncing_threads;

extern class CTrace* pTrace;

extern class CConfigManager* pConfig;
extern class CConfigManager* pXml;
extern class CConfigManager* pService;

extern class CCallback *pCallback;
extern class CMeasurement* pMeasurement;

class AndroidConnector {


    public:
        static AndroidConnector& getInstance() {
            static AndroidConnector instance;
            return instance;
        }

        void registerSharedObject(JNIEnv* env, jobject caller, jobject downloadMeasurementState, jobject uploadMeasurementState);

        /**
        *   resets state and releases global java references made during setup
        */
        void unregisterSharedObject();

        void startMeasurement();

        jint jniLoad(JavaVM* vm);

        void callback(json11::Json::object& message);
        /*
        * The method forwarded to the trace to allow for easy android printing
        */
        void printLog(const std::string& message);

        AndroidConnector(AndroidConnector const&) = delete;
        void operator=(AndroidConnector const&) = delete;
        AndroidConnector(AndroidConnector const&&) = delete;
        void operator=(AndroidConnector const&&) = delete;
    private:

        enum SpeedState {
            DOWNLOAD,
            UPLOAD
        };

        static const char* TAG;

        JavaVM *javaVM;
        jclass jniHelperClass;
        jobject jniHelperObj;
        jobject jniCaller;

        //the object to set the current progress state in
        jobject downloadMeasurementState;
        jobject uploadMeasurementState;

        //the field ids of the settable values
        jfieldID fieldAvgDownloadThroughput;
        jfieldID fieldDurationMsTotal;
        jfieldID fieldDurationMs;

        AndroidConnector() {};

        void passJniSpeedState (JNIEnv *env, SpeedState speedStateToSet, const json11::Json& json) const;

    //void trace (const std::string message);

//TODO: get the compiling to use basic cpp instructions as well


/*
JNIEXPORT void JNICALL startMeasurement (JNIEnv* env, jobject thiz, const std::string measurementConfiguration) {

}
*/

};

#endif //CPP_CLIENT_ANDROID_INTERFACE_H
