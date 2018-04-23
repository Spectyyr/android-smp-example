/*
* Copyright (c) 2018 SessionM. All rights reserved.
*/
package com.sessionm.smp_auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.sessionm.core.api.SessionMError;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static void createAlertDialog(Activity activity, SessionMError sessionMError) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(sessionMError.getMessage())
                .setTitle(sessionMError.getCode())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    public static Map<String, Object> getAllGettersValue(String className, Object object) {
        Class classToInvestigate = null;
        try {
            classToInvestigate = Class.forName(className);
        } catch (ClassNotFoundException e) {
        }
        return getAllGettersValue(classToInvestigate, object);
    }

    public static Map<String, Object> getAllGettersValue(Class classToInvestigate, Object object) {

        Map<String, Object> result = new HashMap<>();
        try {
            Method[] aClassMethods = classToInvestigate.getDeclaredMethods();
            for (Method m : aClassMethods) {
                String methodName = m.getName();
                // Found a method m
                if (methodName.startsWith("get") || methodName.startsWith("is")) {
                    Object value = m.invoke(object);
                    result.put(parseAttributeFromGetter(methodName), value);
                }
            }
        } catch (SecurityException e) {
            // Access denied!
        } catch (Exception e) {
            // Unknown exception
        }
        return result;
    }

    private static String parseAttributeFromGetter(String getterName) {
        String attribute = "";
        if (getterName.startsWith("get"))
            attribute = getterName.substring(3, getterName.length());
        else if (getterName.startsWith("is"))
            attribute = getterName.substring(2, getterName.length());
        return attribute;
    }
}
