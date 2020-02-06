//
// Created by fk on 1/28/20.
// Heavily inspired by the gstreamer tutorials (see: https://gstreamer.freedesktop.org/documentation/tutorials/index.html?gi-language=c)
//

#include "AudioStreamingTest.h"

std::string AudioStreamingTest::start() {
    data = g_new0 (CustomData, 1);

    runAudioStreamingTest(data);

    g_free (data);

    std::string ret = "{ \"audio_start_time_ns\": " + std::to_string((startPlayTime - startCommandTime).count()) + ", ";
    ret += "\"stalls\": [";
    for (int i = 0; i < stallDurations.size(); i++) {
        ret += std::to_string(stallDurations[i]);
        if (i != stallDurations.size() -1) {
            ret += ", ";
        }
    }
    ret += "] }";

    return ret;

}

void AudioStreamingTest::stop() {
    if (!data)
        return;
    g_main_loop_quit (data->main_loop);

}

void AudioStreamingTest::runAudioStreamingTest(CustomData * data) {

    GstBus *bus;
    GSource *bus_source;
    GError *error = NULL;

    /* Create our own GLib Main Context and make it the default one */
    data->context = g_main_context_new ();
    g_main_context_push_thread_default (data->context);

    std::string pipelineConfig = "uridecodebin buffer-duration=" + std::to_string(this->bufferDurationNs) + " uri=" + this->targetUrl + " ! audioconvert ! volume mute=TRUE ! autoaudiosink";

    if(this->logFunction) {
        this->logFunction("DEBUG", TAG, pipelineConfig);
    }

    /* Build pipeline */
    data->pipeline =
            gst_parse_launch(pipelineConfig.c_str(),
                             &error);
    if (error) {
        gchar *message =
                g_strdup_printf ("Unable to build pipeline: %s", error->message);
        //TODO: log error
        g_clear_error (&error);
        g_free (message);
        return;
    }

    /* Set the pipeline to READY, so it can already accept a window handle, if we have one */
    gst_element_set_state (data->pipeline, GST_STATE_READY);

    /* Instruct the bus to emit signals for each received message, and connect to the interesting signals */
    bus = gst_element_get_bus (data->pipeline);
    bus_source = gst_bus_create_watch (bus);
    g_source_set_callback (bus_source, (GSourceFunc) gst_bus_async_signal_func,
                           NULL, NULL);
    g_source_attach (bus_source, data->context);
    g_source_unref (bus_source);
    g_signal_connect (G_OBJECT (bus), "message", (GCallback) AudioStreamingTest::basicCallback,
            this);
    gst_object_unref (bus);

    /* Create a GLib Main Loop and set it to run */
    data->main_loop = g_main_loop_new (data->context, FALSE);
    checkInitializationComplete (data);
    g_main_loop_run (data->main_loop);
    g_main_loop_unref (data->main_loop);
    data->main_loop = NULL;

    /* Free resources */
    g_main_context_unref (data->context);
    gst_element_set_state (data->pipeline, GST_STATE_NULL);
    gst_object_unref (data->pipeline);

    return;
}

void AudioStreamingTest::messageCallback(GstBus *bus, GstMessage *msg) {
    if (this->logFunction) {
        this->logFunction("DEBUG", TAG, "message received w/code: " + std::to_string(GST_MESSAGE_TYPE(msg)));
    }
    switch (GST_MESSAGE_TYPE(msg)) {
        case GST_MESSAGE_ERROR: {
            GError *err;
            gchar *debug_info;
            gchar *message_string;

            gst_message_parse_error (msg, &err, &debug_info);
            //TODO: error handling!!
            g_clear_error (&err);
            g_free (debug_info);
            g_free (message_string);
            break;
        }
        case GST_MESSAGE_EOS: {
            break;
        }
        case GST_MESSAGE_STREAM_START: {
            startPlayTime = std::chrono::steady_clock::now();
            if (this->logFunction) {
                this->logFunction("DEBUG", TAG, "Starting stream");
            }
            break;
        }
        case GST_MESSAGE_BUFFERING: {
            if (!isFirstStall && !isCurrentStall) {
                isCurrentStall = true;
                currentStallStart = std::chrono::steady_clock::now();
            }

            gint percent = 0;
            gst_message_parse_buffering(msg, &percent);

            if (this->logFunction) {
                logFunction("DEBUG", TAG, "buffering w/percent: " + std::to_string(percent));
            }

            if (100 == percent) {
                if (isFirstStall) {
                    isFirstStall = false;
                } else {
                    isCurrentStall = false;
                    stallDurations.push_back(
                            (std::chrono::steady_clock::now() - currentStallStart).count());
                }
            }
            break;
        }
    }

}

void AudioStreamingTest::checkInitializationComplete(CustomData * data)
{
    if (!data->initialized && data->main_loop) {
        data->initialized = TRUE;
        gst_element_set_state (data->pipeline, GST_STATE_PLAYING);
        startCommandTime = std::chrono::steady_clock::now();
        futurePlaybackStop = std::async(std::launch::async, [&] {
            std::this_thread::sleep_for(std::chrono::milliseconds(this->playbackDurationMs));
            if (this->logFunction) {
                this->logFunction("DEBUG", TAG, "stopping playback");
            }
            stop();
        });
    } else {
    //TODO: error handling
    }
}

void AudioStreamingTest::basicCallback (GstBus * bus, GstMessage * msg, AudioStreamingTest test) {
    test.messageCallback(bus, msg);
}