package com.emamagic.message_input;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.emamagic.emoji.BuildConfig;

import java.util.Arrays;
import java.util.List;

public class Logger {

    private final static String TAG = Logger.class.getSimpleName();

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static void e(@NonNull String tag, @Nullable Object text) {
        if (!DEBUG) {
            return;
        }
        Log.e(tag, text != null ? text.toString() : "LOGGER IS NULL");//avoid null
    }

    public static void d(@NonNull String tag, @Nullable Object text) {
        if (!DEBUG) {
            return;
        }
        Log.d(tag, text != null ? text.toString() : "LOGGER IS NULL");//avoid null
    }

    public static void d(@NonNull String tag, @Nullable Object text, @Nullable Throwable throwable) {
        if (!DEBUG) {
            return;
        }
        Log.d(tag, text != null ? text.toString() : "LOGGER IS NULL", throwable);//avoid null

    }

    public static void i(@NonNull String tag, @Nullable Object text) {
        if (!DEBUG) {
            return;
        }
        Log.i(tag, text != null ? text.toString() : "LOGGER IS NULL");//avoid null
    }

    public static void throwable(Throwable throwable) {
        if (!DEBUG) {
            return;
        }
        throwable.printStackTrace();
    }

    public static void exception(Exception exception) {
        if (!DEBUG) {
            return;
        }
        exception.printStackTrace();
    }

    public static void exception(Throwable throwable) {
        if (!DEBUG) {
            return;
        }
        throwable.printStackTrace();
    }

    public static void d(@Nullable Object text) {
        d(getCurrentClassName() + " || " + getCurrentMethodName(), text);//avoid null
    }

    public static void i(@Nullable Object text) {
        i(getCurrentClassName() + " || " + getCurrentMethodName(), text);//avoid null
    }

    public static void e(Object... objects) {
        if (objects != null && objects.length > 0) {
            e(getCurrentClassName() + " || " + getCurrentMethodName(), Arrays.toString(objects));
        } else {
            e(getCurrentClassName() + " || " + getCurrentMethodName(), getCurrentMethodName());
        }
    }

    public static void e(List<Object> objects) {
        if (objects != null) {
            e(getCurrentClassName() + " || " + getCurrentMethodName(), Arrays.toString(objects.toArray()));
        } else {
            e(TAG, null);
        }
    }

    private static String getCurrentMethodName() {
        try {
            return Thread.currentThread().getStackTrace()[4].getMethodName() + "()";
        } catch (Exception ignored) {
        }
        return TAG;
    }

    private static String getCurrentClassName() {
        try {
            String className = Thread.currentThread().getStackTrace()[4].getClassName();
            String[] temp = className.split("[.]");
            className = temp[temp.length - 1];
            return className;
        } catch (Exception ignored) {
        }
        return TAG;
    }
}
