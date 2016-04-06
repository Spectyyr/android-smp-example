/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc.view.custom;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sessionm.mmc.R;

public class CustomLoaderView extends com.sessionm.api.CustomLoaderView {

    private RelativeLayout _loadingLayout;
    private RelativeLayout _failedLayout;
    private RelativeLayout _unavailableLayout;
    Context _context;

    @IdRes
    int resIdOne = 1, resIdTwo = 2, resIdThree = 3;

    public CustomLoaderView(Context context) {
        super();
        this._context = context;
        createCustomLoaderLayout();
    }

    public void createCustomLoaderLayout() {
        //Main container layout
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout loadingContainerLayout = new RelativeLayout(_context);
        loadingContainerLayout.setLayoutParams(params);

        //Loading layout in LOADING state
        if (_loadingLayout == null)
            _loadingLayout = new RelativeLayout(_context);
        _loadingLayout.setLayoutParams(params);
        //Set custom loader background if you want
        _loadingLayout.setBackgroundColor(_context.getResources().getColor(R.color.colorPrimaryDark));

        RelativeLayout.LayoutParams titleLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        titleLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        TextView titleTextView = new TextView(_context);
        titleTextView.setText("Loading...");
        titleTextView.setLayoutParams(titleLayoutParams);

        titleTextView.setTextColor(Color.BLACK);
        titleTextView.setId(resIdOne);

        RelativeLayout.LayoutParams progrssBarLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        progrssBarLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        progrssBarLayoutParams.addRule(RelativeLayout.BELOW, titleTextView.getId());
        ProgressBar progressBar = new ProgressBar(_context);
        progressBar.setLayoutParams(progrssBarLayoutParams);
        _loadingLayout.addView(titleTextView);
        _loadingLayout.addView(progressBar);

        //Failed layout in FAILED state
        if (_failedLayout == null)
            _failedLayout = new RelativeLayout(_context);
        _failedLayout.setLayoutParams(params);

        TextView failedTextView = new TextView(_context);
        failedTextView.setText("Failed");
        failedTextView.setLayoutParams(titleLayoutParams);
        failedTextView.setTextSize(40);
        failedTextView.setTextColor(Color.BLACK);
        failedTextView.setId(resIdTwo);

        RelativeLayout.LayoutParams retryButtonLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        retryButtonLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        retryButtonLayoutParams.addRule(RelativeLayout.BELOW, failedTextView.getId());
        Button retryBtn = new Button(_context);
        retryBtn.setText("Retry");
        retryBtn.setLayoutParams(retryButtonLayoutParams);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call reload method if failed.
                reloadPortalContent();
            }
        });
        _failedLayout.addView(failedTextView);
        _failedLayout.addView(retryBtn);
        _failedLayout.setVisibility(View.GONE);

        //Unavailable layout in UNAVAILABLE state
        if (_unavailableLayout == null)
            _unavailableLayout = new RelativeLayout(_context);
        _unavailableLayout.setLayoutParams(params);

        TextView unavailableTextView = new TextView(_context);
        unavailableTextView.setText("Unavailable");
        unavailableTextView.setLayoutParams(titleLayoutParams);
        unavailableTextView.setTextSize(40);
        unavailableTextView.setTextColor(Color.BLACK);
        unavailableTextView.setId(resIdThree);

        RelativeLayout.LayoutParams dismissButtonLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dismissButtonLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        dismissButtonLayoutParams.addRule(RelativeLayout.BELOW, unavailableTextView.getId());
        Button dismissBtn = new Button(_context);
        dismissBtn.setText("Close");
        dismissBtn.setLayoutParams(dismissButtonLayoutParams);
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Dismiss the custom loader if unavailable.
                dismissPortal();
            }
        });

        _unavailableLayout.addView(unavailableTextView);
        _unavailableLayout.addView(dismissBtn);
        _unavailableLayout.setVisibility(View.GONE);

        //Close button
        RelativeLayout.LayoutParams closeButtonLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        closeButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        closeButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        Button closeBtn = new Button(_context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            closeBtn.setBackground(_context.getDrawable(R.drawable.close));
        }
        else
            closeBtn.setBackground(_context.getResources().getDrawable(R.drawable.close));
        closeBtn.setLayoutParams(closeButtonLayoutParams);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPortal();
            }
        });

        loadingContainerLayout.addView(_loadingLayout);
        loadingContainerLayout.addView(_failedLayout);
        loadingContainerLayout.addView(_unavailableLayout);
        loadingContainerLayout.addView(closeBtn);

        //Call setCustomLoader() method to set up custom loader view.
        setCustomLoader(loadingContainerLayout);
    }

    @Override
    public void updateLoaderViewOnStatusChanged(LoaderViewStatus status) {
        super.updateLoaderViewOnStatusChanged(status);
        if (status.equals(LoaderViewStatus.LOADING)) {
            _loadingLayout.setVisibility(View.VISIBLE);
            _failedLayout.setVisibility(View.GONE);
            _unavailableLayout.setVisibility(View.GONE);
        } else if (status.equals(LoaderViewStatus.FAILED)) {
            _loadingLayout.setVisibility(View.GONE);
            _failedLayout.setVisibility(View.VISIBLE);
            _unavailableLayout.setVisibility(View.GONE);
        } else if (status.equals(LoaderViewStatus.UNAVAILABLE)) {
            _loadingLayout.setVisibility(View.GONE);
            _failedLayout.setVisibility(View.GONE);
            _unavailableLayout.setVisibility(View.VISIBLE);
        }
    }
}
