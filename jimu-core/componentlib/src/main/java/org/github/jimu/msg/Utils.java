package org.github.jimu.msg;

import android.os.Looper;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * <p><b>Package:</b> org.github.jimu.msg </p>
 * <p><b>Project:</b> JIMU </p>
 * <p><b>Classname:</b> Utils </p>
 * <p><b>Description:</b> utilities </p>
 * Created by leobert on 2018/4/26.
 */
public class Utils {
    public static String getProcessName() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + android.os.Process.myPid() + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    static <T> void validateCompoEventManagerInterface(Class<T> service) {
        if (!service.isInterface()) {
            throw new IllegalArgumentException("CompoEventManagerAPI must be declared as interfaces.");
        }
        if (service.getInterfaces().length > 0) {
            throw new IllegalArgumentException("CompoEventManagerAPI interfaces must not extend any other interfaces.");
        }
    }

    public static void checkNotNull(Object obj, String msg) {
        if (obj == null)
            throw new NullPointerException("null check failed; " + msg);
    }
}
