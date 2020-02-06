//
// Created by fk on 1/28/20.
//

#ifndef AUDIOSTREAMINGTEST_H
#define AUDIOSTREAMINGTEST_H

#include <functional>
#include <future>
#include <chrono>
#include <string>
#include <vector>
#include <gst/gst.h>

class AudioStreamingTest {

public:

    inline void initialize(std::string url, long long int const bufferDurationMs, long long int const playbackDurationMs) {
        this->targetUrl = url;
        this->bufferDurationNs = bufferDurationMs;
        this->playbackDurationMs = playbackDurationMs;
    }

    std::string start();

    void stop();

    /**
     * function has params: level, tag, message
     * to be set on demand
     */
    inline void setLogFunction(std::function<void(std::string, std::string, std::string)> logFunction) {
        this->logFunction = logFunction;
    }

    /**
     * Callback when a new thread is started for the first time (e.g. to register with JVM), to be set on demand
     */
    inline void setOnThreadAttachCallback(std::function<void()> threadAttachCallback) {
        this->threadAttachCallback = threadAttachCallback;
    }

    /**
     * Callback when a new thread is ending (e.g. to unregister with JVM), to be set on demand
     */
    inline void setOnThreadDetachCallback(std::function<void()> threadDetachCallback) {
        this->threadDetachCallback = threadDetachCallback;
    }

private:

    const char * TAG = "AudioStreamingTest";

    /* Structure to contain all our information, so we can pass it to callbacks */
    typedef struct _CustomData
    {
        GstElement *pipeline;         /* The running pipeline */
        GMainContext *context;        /* GLib context used to run the main loop */
        GMainLoop *main_loop;         /* GLib main loop */
        gboolean initialized;         /* To avoid informing the UI multiple times about the initialization */
    } CustomData;

    CustomData * data;

    std::string targetUrl;

    long long int bufferDurationNs = -1;

    long long int playbackDurationMs = -1;

    std::function<void()> threadAttachCallback;

    std::function<void()> threadDetachCallback;

    std::function<void(std::string, std::string, std::string)> logFunction;

    bool isCurrentStall = false;
    bool isFirstStall = true;
    std::chrono::time_point<std::chrono::steady_clock> currentStallStart;
    std::vector<long long int> stallDurations;

    std::chrono::time_point<std::chrono::steady_clock> startCommandTime;
    std::chrono::time_point<std::chrono::steady_clock> startPlayTime;

    std::future<void> futureGstreamerMainLoop;
    std::future<void> futurePlaybackStop;

    std::string result;

    static void basicCallback (GstBus * bus, GstMessage * msg, AudioStreamingTest test);

    void messageCallback(GstBus * bus, GstMessage * msg);

    void runAudioStreamingTest(CustomData * data);

    void checkInitializationComplete(_CustomData *);
};


#endif //AUDIOSTREAMINGTEST_H
