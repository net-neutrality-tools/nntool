//
// Created by fk on 1/30/20.
//

#ifndef ANDROID_FIX_JNICONNECTOR_H
#define ANDROID_FIX_JNICONNECTOR_H

#include <jni.h>
#include <string>
#include <android/log.h>

class JniConnector {

public:
    static JniConnector& getInstance() {
        static JniConnector instance;
        return instance;
    }

    static void nativeInit(JNIEnv * env, jobject caller, jstring url, jlong bufferDurationNs, jlong playbackDurationNs) {
        JniConnector::getInstance().init(std::string(env->GetStringUTFChars(url, NULL)), (long long int) bufferDurationNs, (long long int) playbackDurationNs);
    }
    static jstring nativeStartTest(JNIEnv * env, jobject caller) {
        std::string result = JniConnector::getInstance().startTest();
        const char * t = result.c_str();
        __android_log_write (ANDROID_LOG_ERROR, "audio-streaming",
                             t);
        return env->NewStringUTF(result.c_str());
    }
    static void nativeFinalize() {
        JniConnector::getInstance().finalize();
    }

    jint loadJVM (JavaVM * vm);

    void logMessage(std::string level, std::string tag, std::string message);

private:
    //The prebuilt openh264 codec comes with text-relocations, which are forbidden under modern android versions, according to the internet the codec plugin can be manually built without text-relocations
/* List of implemented native methods */
    JNINativeMethod native_methods[3] = {
            {"nativeInit", "(Ljava/lang/String;JJ)V", (void *) nativeInit},
            {"nativeStartTest", "()Ljava/lang/String;", (void *) nativeStartTest},
            {"nativeFinalize", "()V", (void *) nativeFinalize},
    };

    JavaVM * javaVm;

    JNIEnv * attachCurrentThread();
    void detachCurrentThread();

    std::string url;
    long long int bufferDurationNs;
    long long int playbackDurationNs;

    //std::function<void(std::string, std::string, std::string)> logFunction;

    void init(std::string url, long long int bufferDurationNs, long long int playbackDurationNs);

    std::string startTest();

    void finalize();

};


#endif //ANDROID_FIX_JNICONNECTOR_H
