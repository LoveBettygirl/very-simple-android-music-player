package com.example.musicplayer;

public class Tag {
    public static String getTag(String tag) {
        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        StackTraceElement method = stackTraceElement[1];

        for (StackTraceElement s : stackTraceElement) {
            if (s.getClassName().contains(tag)) {
                method = s;
                break;
            }
        }

        return method.getClassName() + "." + method.getMethodName() + "()";
    }
}
