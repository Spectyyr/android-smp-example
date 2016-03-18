/*
 * Copyright (c) 2015 SessionM. All rights reserved.
 */

package com.sessionm.mmc.view;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.sessionm.mmc.R;
import com.squareup.picasso.Picasso;

public class BaseScrollAndRefreshFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ObservableScrollViewCallbacks {

    //Scroll list methods to show/hide tool bar
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            if (scrollState == ScrollState.UP) {
                if (ab.isShowing()) {
                    ab.hide();
                }
            } else if (scrollState == ScrollState.DOWN) {
                if (!ab.isShowing()) {
                    ab.show();
                }
            }
        }
    }

    @Override
    public void onRefresh() {
    }

    protected void popUpImageDialog(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_image, null);
        ImageView imageView = (ImageView) dialogLayout.findViewById(R.id.dialog_imageview);
        Picasso.with(getActivity()).load(url).into(imageView);

        dialog.setView(dialogLayout);
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();
    }
}
