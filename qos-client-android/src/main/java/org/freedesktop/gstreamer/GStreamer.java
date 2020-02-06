package org.freedesktop.gstreamer;

import android.content.Context;

/**
 * Weird intialisation method for the GStreamer library (which is actively looking for a org/freedesktop/gstreamer/Gstreamer class => move initialisation to cpp to remove this class
 */
public class GStreamer {
    private static native void nativeInit(Context context) throws Exception;

    public static void init(Context context) throws Exception {
        nativeInit(context);
    }
}
