/*
 * Copyright (c) 2018 SessionM. All rights reserved.
 */
package com.sessionm.smp_receipt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.sessionm.core.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Utils {
    private static final String TAG = "SessionM.TestApp";
    public static final String MEDIA_MOUNTED = "mounted";
    public static String mCameraPhotoPath;
    public final static int FILE_CHOOSER_RESULT_CODE = 1;

    public static String formatStringToJson(String text) {
        if (text == null)
            return null;

        StringBuilder json = new StringBuilder();
        String indentString = "";

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            switch (letter) {
                case '{':
                case '[':
                    json.append("\n" + indentString + letter + "\n");
                    indentString = indentString + "\t";
                    json.append(indentString);
                    break;
                case '}':
                case ']':
                    indentString = indentString.replaceFirst("\t", "");
                    json.append("\n" + indentString + letter);
                    break;
                case ',':
                    json.append(letter + "\n" + indentString);
                    break;

                default:
                    json.append(letter);
                    break;
            }
        }

        return json.toString();
    }

    public static Map<String, Object> getAllGettersValue(String className, Object object) {
        Class classToInvestigate = null;
        try {
            classToInvestigate = Class.forName(className);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Reflection failed! " + e);
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
            Log.e(TAG, "Reflection failed! " + e);
            // Access denied!
        } catch (Exception e) {
            Log.e(TAG, "Reflection failed! " + e);
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

    public static String getAttributeRawString(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry entry : map.entrySet()) {
            sb.append(entry.getKey());
            sb.append(" : ");
            sb.append(String.valueOf(entry.getValue()));
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void invokePrivateMethod(Object object, String methodName, Object... params) {
        try {
            Method method = object.getClass().getDeclaredMethod(methodName, params.getClass());
            method.setAccessible(true);
            method.invoke(object, params);
            method.setAccessible(false);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "Reflection failed! " + e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Reflection failed! " + e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "Reflection failed! " + e);
        }

    }

    //When using a more specific class, especially when the parameter contains interface
    public static void invokePrivateMethodWithParamClass(Object object, String methodName, Object[] params, Class[] paramClasses) {
        try {
            Method method = object.getClass().getDeclaredMethod(methodName, paramClasses);
            method.setAccessible(true);
            method.invoke(object, params);
            method.setAccessible(false);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "Reflection failed! " + e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Reflection failed! " + e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "Reflection failed! " + e);
        }

    }

    public static String updateNonEmptyField(EditText editText) {
        if (!editText.getText().toString().isEmpty())
            return editText.getText().toString();
        return null;
    }

    public static Double updateNonEmptyFieldDouble(EditText editText) {
        if (!editText.getText().toString().isEmpty())
            return Double.parseDouble(editText.getText().toString());
        return null;
    }

    public static int asIntPixels(float dips, Context context) {
        return (int) (asFloatPixels(dips, context) + 0.5f);
    }

    public static float asFloatPixels(float dips, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, displayMetrics);
    }

    public static boolean isStorePictureSupported(Context context) {
        return MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && context.checkCallingOrSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static ImageButton createCloseButton(Activity activity) {
        ImageButton closeButton = new ImageButton(activity);
        closeButton.setBackgroundColor(Color.TRANSPARENT);
        RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(asIntPixels(50, activity), asIntPixels(50, activity));
        closeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT | RelativeLayout.ALIGN_PARENT_TOP);
        closeParams.rightMargin = 0;
        closeParams.topMargin = 0;
        closeButton.setLayoutParams(closeParams);
        return closeButton;
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static void onShowFileChooser(Activity hostingActivity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(hostingActivity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (IOException ex) {
                Log.e(TAG, "Unable to create Image File from onShowFileChooser", ex);
            }

            if (photoFile != null) {
                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }

        Intent contentSelectionIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            contentSelectionIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        } else
            contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        hostingActivity.startActivityForResult(chooserIntent, FILE_CHOOSER_RESULT_CODE);
    }

    private static File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "SessionM_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return imageFile;
    }

    public static String getAllocatedMemory() {
        return String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024) + "MB";
    }

    public static void hideCurrentKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
