/*
 * Copyright (c) 2018 SessionM. All rights reserved.
 */

package com.sessionm.smp_receipt.upload;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sessionm.smp_receipt.R;

import org.json.JSONObject;

public class ReceiptEndDialogFragment extends DialogFragment {

    private static final String TAG = "SessionM.ReceiptDialog";
    private static final String isSuccessKey = "isSuccess";
    private static final String errorKey = "error";
    private static final String campaignIDKey = "campaignID";
    private JSONObject payloadJSONObject;

    public static ReceiptEndDialogFragment newInstance(boolean isSuccess, String errorMessage, String campaignID) {
        ReceiptEndDialogFragment f = new ReceiptEndDialogFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putBoolean(isSuccessKey, isSuccess);
        args.putString(errorKey, errorMessage);
        args.putString(campaignIDKey, campaignID);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.receipt_upload_end_dialog);

        final boolean isSuccess = getArguments().getBoolean(isSuccessKey);
        String errorMessage = getArguments().getString(errorKey);
        final String campaignID = getArguments().getString(campaignIDKey);
        String title = getString(com.sessionm.receipt.R.string.thank_you);
        String description = getString(com.sessionm.receipt.R.string.your_receipt_has_been_uploaded);

        if (!isSuccess) {
            title = getActivity().getString(com.sessionm.receipt.R.string.we_are_sorry);
            description = errorMessage;
        }

        TextView titleTextView = dialog.findViewById(R.id.receipt_upload_end_title);
        titleTextView.setText(title);

        TextView descriptionTextView = dialog.findViewById(R.id.receipt_upload_end_description);
        descriptionTextView.setText(description);

        Button submitButton = dialog.findViewById(R.id.receipt_upload_end_ok_button);
        getActivity().setResult(Activity.RESULT_OK);
        if (!isSuccess) {
            submitButton.setText(getString(com.sessionm.receipt.R.string.try_again_later));
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", "Upload failed.");
            getActivity().setResult(Activity.RESULT_CANCELED, returnIntent);
        }
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSuccess) {
                }
                dismiss();
                //TODO: Add retry option
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        //Disable back button for current fragment
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });

        dialog.show();
        return dialog;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        getActivity().finish();
    }
}
