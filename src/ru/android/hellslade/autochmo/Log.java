package ru.android.hellslade.autochmo;

import android.text.TextUtils;

public final class Log {

   private static final String TAG = "Autochmo";
	
   public static void v(String msg) {
       android.util.Log.v(TAG, getLocation() + msg);
   }
   
   private static String getLocation() {
       final String className = Log.class.getName();
       final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
       boolean found = false;

       for (int i = 0; i < traces.length; i++) {
           StackTraceElement trace = traces[i];

           try {
               if (found) {
                   if (!trace.getClassName().startsWith(className)) {
                       Class<?> clazz = Class.forName(trace.getClassName());
                       return "[" + getClassName(clazz) + ":" + trace.getMethodName() + ":" + trace.getLineNumber() + "]: ";
                   }
               }
               else if (trace.getClassName().startsWith(className)) {
                   found = true;
                   continue;
               }
           }
           catch (ClassNotFoundException e) {
           }
       }

       return "[]: ";
   }

   private static String getClassName(Class<?> clazz) {
       if (clazz != null) {
           if (!TextUtils.isEmpty(clazz.getSimpleName())) {
               return clazz.getSimpleName();
           }

           return getClassName(clazz.getEnclosingClass());
       }

       return "";
   }
}