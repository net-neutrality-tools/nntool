//
// Created by fk on 1/30/20.
//

#include "JniConnector.h"
#include "AudioStreamingTest.h"

#ifdef __ANDROID__
#include <android/log.h>
#endif

extern "C" JNIEXPORT
jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
    return JniConnector::getInstance().loadJVM(vm);
}


jint JniConnector::loadJVM(JavaVM *vm) {
    __android_log_print (ANDROID_LOG_ERROR, "tutorial-3",
                              "connecting in class");
    JNIEnv *env = nullptr;

    javaVm = vm;

    if (vm->GetEnv ((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return 0;
    }

    jclass klass = env->FindClass ("at/alladin/nettest/qos/android/impl/AudioStreamingServiceAndroidImpl");
    env->RegisterNatives (klass, native_methods,
                          3);

    return JNI_VERSION_1_6;
}

void JniConnector::init(std::string url, long long int bufferDurationNs, long long int playbackDurationNs) {
    this->url = url;
    this->bufferDurationNs = bufferDurationNs;
    this->playbackDurationNs = playbackDurationNs;
}

std::string JniConnector::startTest() {
    AudioStreamingTest test;
    test.setOnThreadAttachCallback(std::bind(&JniConnector::attachCurrentThread, this));
    test.setOnThreadDetachCallback(std::bind(&JniConnector::detachCurrentThread, this));
    test.setLogFunction(std::bind(&JniConnector::logMessage, this, std::placeholders::_1, std::placeholders::_2, std::placeholders::_3));
    this->logMessage("DEBUG", "CONNECTOR", this->url);
    this->logMessage("DEBUG", "CONNECTOR", std::to_string(this->bufferDurationNs));
    this->logMessage("DEBUG", "CONNECTOR", std::to_string(this->playbackDurationNs));

    test.initialize(this->url, this->bufferDurationNs, this->playbackDurationNs);
    return test.start();
}

void JniConnector::finalize() {

}

JNIEnv * JniConnector::attachCurrentThread ()
{
    JNIEnv *env;
    JavaVMAttachArgs args;

    args.version = JNI_VERSION_1_6;
    args.name = NULL;
    args.group = NULL;

    if (javaVm->AttachCurrentThread (&env, &args) < 0) {
        return NULL;
    }

    return env;
}

/* Unregister this thread from the VM */
void JniConnector::detachCurrentThread ()
{
    javaVm->DetachCurrentThread ();
}

void JniConnector::logMessage(std::string level, std::string tag, std::string message) {
#ifdef __ANDROID__
    if (level == "ERROR") {
        __android_log_write(ANDROID_LOG_ERROR, tag.c_str(), message.c_str());
    } else {
        __android_log_write(ANDROID_LOG_DEBUG, tag.c_str(), message.c_str());
    }
#endif
}
