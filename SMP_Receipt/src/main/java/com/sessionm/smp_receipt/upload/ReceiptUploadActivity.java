/*
 * Copyright (c) 2018 SessionM. All rights reserved.
 */

package com.sessionm.smp_receipt.upload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sessionm.core.api.SessionMError;
import com.sessionm.core.util.JSONObject;
import com.sessionm.core.util.Util;
import com.sessionm.receipt.api.ReceiptsListener;
import com.sessionm.receipt.api.ReceiptsManager;
import com.sessionm.receipt.api.data.Receipt;
import com.sessionm.smp_receipt.R;
import com.sessionm.smp_receipt.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SessionM receipt capture activity.
 */
public class ReceiptUploadActivity extends Activity {
    public static final String RECEIPT_ATTRIBUTE_TITLE = "title";
    public static final String RECEIPT_ATTRIBUTE_DESCRIPTION = "description";

    public static final String RECEIPT_ONPROGRESS_INTENT = "receipt_progress";
    public static final String RECEIPT_ONUPLOADED_INTENT = "receipt_uploaded";
    public static final String RECEIPT_IMAGE_COUNT = "image_count";
    private static final String TAG = "SessionM.Receipt";
    private boolean isFromResult;
    private boolean showWelcomePage = true;
    ImageView receiptImage;

    private Bitmap bitmap;
    private List<Uri> uris = new ArrayList<>();
    ProgressDialog progressDialog;
    private boolean isFromCamera = false;

    private JSONObject payloadJSONObject;

    private static final String TEST_CAMPAIGN_ID = "426";
    private static final String TEST_PLACEMENT_ID = "";
    private String campaignID = TEST_CAMPAIGN_ID;
    private String placementID = TEST_PLACEMENT_ID;

    private boolean shouldSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_upload_activity);

        Map<String, String> attributesMap = new HashMap<>();
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            attributesMap = (HashMap<String, String>) bundle.getSerializable("attributes");
            showWelcomePage = bundle.getBoolean("welcome");
            String cID = bundle.getString("campaign_id");
            if (cID != null && !cID.isEmpty())
                campaignID = cID;
            String pID = bundle.getString("placement_id");
            if (pID != null && !pID.isEmpty())
                placementID = pID;
            shouldSetListener = bundle.getBoolean("should_set_listener");
        }

        ImageButton closeButton = findViewById(R.id.receipt_upload_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "Close button from Receipt Activity");
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        TextView useImageText = findViewById(R.id.receipt_submit_button);
        TextView retakeText = findViewById(R.id.receipt_retake_button);

        final Map<String, String> finalAttributesMap = attributesMap;
        String uploadURL = "";
        if (attributesMap != null) {
            uploadURL = attributesMap.get("url");
            String adGroupID = attributesMap.get("adGroupId");
            if (adGroupID != null && !adGroupID.isEmpty())
                campaignID = adGroupID;
            if (attributesMap.get("payload") != null)
                payloadJSONObject = JSONObject.create(attributesMap.get("payload"));
        }

        final String finalUploadURL = uploadURL;
        useImageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) {
                    sendReceiptWithProgressDialog(finalUploadURL);
                }
            }
        });

        retakeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.onShowFileChooser(ReceiptUploadActivity.this);
            }
        });
        //Receipt Image
        receiptImage = findViewById(R.id.receipt_upload_image_view);
        Utils.onShowFileChooser(this);
        ReceiptsManager.getInstance().setListener(_receiptListener);
    }

    ReceiptsListener _receiptListener = new ReceiptsListener() {
        @Override
        public void onReceiptUploaded(Receipt receipt) {
            if (progressDialog != null && progressDialog.isShowing()) {
                updateProgressDialogWithResult(true, receipt.toString());
            }
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Image uploaded! ID: " + receipt.getID());
                Log.d(TAG, "Memory image uploaded: " + Utils.getAllocatedMemory());
            }
        }

        @Override
        public void onReceiptsFetched(List<Receipt> receiptList) {
        }

        @Override
        public void onProgress(Receipt receipt) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.setMessage(getString(com.sessionm.receipt.R.string.uploading) + receipt.getImageURLs().size() + "/" + receipt.getImageCount());
            }
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, getString(com.sessionm.receipt.R.string.uploading) + receipt.getImageURLs().size());
                Log.d(TAG, getString(com.sessionm.receipt.R.string.uploading) + "Memory: " + Utils.getAllocatedMemory());
            }
        }

        @Override
        public void onFailure(SessionMError error) {
            updateProgressDialogWithResult(false, error.getMessage());
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldSetListener)

            if (!isFromResult) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Resumed activity from new created.");
                }
                if (!showWelcomePage) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Show file chooser because there is no welcome page.");
                    }
                    Utils.onShowFileChooser(this);
                }
            } else {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Resumed activity from result.");
                }
                if (bitmap == null)
                    this.finish();
            }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        forceImageRecycle();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Memory onActivityResult: " + Utils.getAllocatedMemory());
        }
        isFromResult = true;
        if (requestCode != Utils.FILE_CHOOSER_RESULT_CODE) {
            super.onActivityResult(requestCode, resultCode, intent);
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Wrong file chooser result code!");
            }
            return;
        }

        Uri[] results = null;

        // Check that the response is a good one
        if (resultCode == Activity.RESULT_OK) {
            if (intent == null || intent.getData() == null) {
                isFromCamera = true;
                // If there is not data, then we may have taken a photo
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Results not from document. Trying to get from photo.");
                }
                if (Utils.mCameraPhotoPath != null)
                    results = new Uri[]{Uri.parse(Utils.mCameraPhotoPath)};
            } else {
                isFromCamera = false;
                // results from Documents
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Results from documents.");
                }
                String dataString = intent.getDataString();
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
                //Remove redundant "file:" prefix
                String filePath = "";
                if (Utils.mCameraPhotoPath != null && Utils.mCameraPhotoPath.length() > 4) {
                    filePath = Utils.mCameraPhotoPath.substring(5);
                    File f = new File(filePath);
                    if (f.exists()) {
                        boolean deleted = f.delete();
                        if (Log.isLoggable(TAG, Log.DEBUG)) {
                            if (deleted) {
                                Log.d(TAG, "Image from Documents! New camera photo deleted.");
                            } else {
                                Log.d(TAG, "Image from Documents! New camera photo NOT deleted.");
                            }
                        }
                    }
                }
                if (results != null && results[0] != null) {
                    final int takeFlags = intent.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        getContentResolver().takePersistableUriPermission(results[0], takeFlags);
                    }
                }
            }
            if (results != null) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Results is valid! Generating bitmap...");
                }
                forceImageRecycle();
                generateBitmap(results[0]);
                if (bitmap != null) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Bitmap generated! Setting to image view...");
                    }
                    receiptImage.setImageBitmap(bitmap);
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Memory Set bitmap: " + Utils.getAllocatedMemory());
                    }
                }
            }
        }

    }

    //TODO: pass in upload url for now
    private void sendReceiptWithProgressDialog(String uploadUrl) {
        progressDialog = new ProgressDialog(this);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setMessage(getString(com.sessionm.receipt.R.string.uploading) + 0 + "/" + uris.size());
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        //Session.getSession().uploadReceiptImage(uploadUrl, Util.convertBitmapToBase64String(bitmap), payloadJSONObject);
        List<Bitmap> bitmapList = new ArrayList<>();
        bitmapList.add(bitmap);
        if (ReceiptsManager.getInstance().uploadReceiptImages(uris, campaignID, placementID, null, false) == null) {
            updateProgressDialogWithResult(false, getString(com.sessionm.receipt.R.string.no_network_connection));
        } else if (!shouldSetListener) {
            updateProgressDialogWithResult(true, getString(com.sessionm.receipt.R.string.uploading));
        }
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Memory uploading image: " + Utils.getAllocatedMemory());
        }
    }

    private void updateProgressDialogWithResult(boolean isSuccess, String errorMessage) {
        //TODO: differentiate uploading failed / attributes invalid error
        if (progressDialog != null) {
            progressDialog.dismiss();
            ReceiptEndDialogFragment receiptEndDialogFragment = ReceiptEndDialogFragment.newInstance(isSuccess, errorMessage, campaignID);
            receiptEndDialogFragment.show(getFragmentManager(), "receipt_end_fragment");
        }
    }

    private void generateBitmap(Uri selectedImagePath) {
        uris.add(selectedImagePath);
        if (selectedImagePath == null)
            return;
        bitmap = Util.getThumbnail(getApplicationContext(), selectedImagePath);

        ExifInterface ei;
        try {
            String filePath = "";
            if (Utils.mCameraPhotoPath != null && Utils.mCameraPhotoPath.length() > 4)
                filePath = Utils.mCameraPhotoPath.substring(5);
            ei = new ExifInterface(filePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = Utils.rotateBitmap(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = Utils.rotateBitmap(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = Utils.rotateBitmap(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Cannot get exif info from file path!" + selectedImagePath.toString());
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void forceImageRecycle() {
        receiptImage.setImageBitmap(null);
        if (bitmap != null)
            bitmap.recycle();
        System.gc();
    }
}
