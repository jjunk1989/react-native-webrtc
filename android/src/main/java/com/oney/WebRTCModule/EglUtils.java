package com.oney.WebRTCModule;

import android.util.Log;

import org.webrtc.EglBase;
// import org.webrtc.EglBase10;
// import org.webrtc.EglBase14;

public class EglUtils {
    /**
     *  remove webrtc eglbase10/eglbase14  from webrtc 61.
     *  add force opengl 4xmsaa config
     *  more details https://developer.android.com/reference/javax/microedition/khronos/egl/EGL10.html#EGL_LEVEL
     *
     * */
    /**
     * The root {@link EglBase} instance shared by the entire application for
     * the sake of reducing the utilization of system resources (such as EGL
     * contexts).
     */
    private static EglBase rootEglBase;

    /**
     * Lazily creates and returns the one and only {@link EglBase} which will
     * serve as the root for all contexts that are needed.
     */
    public static synchronized EglBase getRootEglBase() {
        if (rootEglBase == null) {
            // XXX EglBase14 will report that isEGL14Supported() but its
            // getEglConfig() will fail with a RuntimeException with message
            // "Unable to find any matching EGL config". Fall back to EglBase10
            // in the described scenario.
            EglBase eglBase = null;
//            int[] configAttributes = EglBase.CONFIG_PLAIN;
            int [] configAttributes = {
                    // 12324, 8,             // EGL_RED_SIZE
                    // 12323, 8,             // EGL_GREEN_SIZE
                    // 12322, 8,             // EGL_BLUE_SIZE
                    // 12352, 4,             // EGL_RENDERABLE_TYPE
                    // 12344                 // EGL_NONE
                    12329, 0,                // EGL_LEVEL
                    12352, 4,                // EGL_RENDERABLE_TYPE
                    12351, 12430,            // EGL_COLOR_BUFFER_TYPE EGL_RGB_BUFFER
                    12324, 8,                // EGL_RED_SIZE
                    12323, 8,                // EGL_GREEN_SIZE
                    12322, 8,                // EGL_BLUE_SIZE
                    12325, 16,               // EGL_DEPTH_SIZE
                    12338, 1,                // EGL_SAMPLE_BUFFERS
                    12337, 4,                // EGL_SAMPLES  在这里修改MSAA的倍数，4就是4xMSAA，再往上开程序可能会崩
                    12344
            };
            RuntimeException cause = null;

            // try {
            //     if (EglBase14.isEGL14Supported()) {
            //         eglBase
            //             = new EglBase14(
            //                     /* sharedContext */ null,
            //                     configAttributes);
            //     }
            // } catch (RuntimeException ex) {
            //     // Fall back to EglBase10.
            //     cause = ex;
            // }

            // if (eglBase == null) {
            //     try {
            //         eglBase
            //             = new EglBase10(
            //                     /* sharedContext */ null,
            //                     configAttributes);
            //     } catch (RuntimeException ex) {
            //         // Neither EglBase14, nor EglBase10 succeeded to initialize.
            //         cause = ex;
            //     }
            // }
            try {
                eglBase = EglBase.create(null, configAttributes);
            } catch (RuntimeException ex) {
                cause = ex;
            }

            if (cause != null) {
                Log.e(EglUtils.class.getName(), "Failed to create EglBase", cause);
            } else {
                rootEglBase = eglBase;
            }
        }

        return rootEglBase;
    }

    public static EglBase.Context getRootEglBaseContext() {
        EglBase eglBase = getRootEglBase();

        return eglBase == null ? null : eglBase.getEglBaseContext();
    }
}
