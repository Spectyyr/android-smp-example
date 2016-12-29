/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.AchievementData;
import com.sessionm.api.SessionListener;
import com.sessionm.api.SessionM;
import com.sessionm.api.User;
import com.sessionm.api.geofence.GeofenceManager;
import com.sessionm.api.message.notification.data.NotificationMessage;
import com.sessionm.mmc.BuildConfig;
import com.sessionm.mmc.R;
import com.sessionm.mmc.util.Utility;
import com.sessionm.mmc.view.custom.CustomLoaderView;

public class SettingsActivity extends Activity implements SessionListener {

    private static final String VERSION_NUM = BuildConfig.VERSION_NAME;
    private static final int BUILD_NUM = BuildConfig.VERSION_CODE;
    private static final int TEST_EVENT_TRIGGER_CAP = 5;

    private boolean _geofenceEnabled;
    private boolean _pushNotificationEnabled;
    private boolean _backgroundReceiptUploadingEnabled;
    private int _testEventTriggerCount = 0;

    private SessionM _sessionM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        _sessionM = SessionM.getInstance();
        _geofenceEnabled = Utility.getLocalStatusBoolean(Utility.GEOFENCE_ENABLED_KEY);
        _backgroundReceiptUploadingEnabled = Utility.getLocalStatusBoolean(Utility.BACKGROUND_RECEIPT_UPLOADING_ENABLED_KEY);
        _pushNotificationEnabled = _sessionM.getPushNotificationEnabled();
        String[] settingsList = getResources().getStringArray(R.array.settings_array);
        SettingsListArrayAdapter settingsListArrayAdapter = new SettingsListArrayAdapter(this, settingsList);
        ListView listView = (ListView) this.findViewById(R.id.settings_listview);
        listView.setAdapter(settingsListArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    @Override
    public void onSessionStateChanged(SessionM sessionM, SessionM.State state) {

    }

    @Override
    public void onSessionFailed(SessionM sessionM, int i) {

    }

    @Override
    public void onUserUpdated(SessionM sessionM, User user) {
        if (!user.isRegistered()) {
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            finishAffinity();
        }
    }

    @Override
    public void onUnclaimedAchievement(SessionM sessionM, AchievementData achievementData) {

    }

    public class SettingsListArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public SettingsListArrayAdapter(Context context, String[] values) {
            super(context, R.layout.settings_item_row, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.settings_item_row, parent, false);
            TextView nameTextView = (TextView) rowView.findViewById(R.id.settings_row_name_textView);
            final CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.settings_row_checkbox);

            nameTextView.setText(values[position]);
            switch (position) {
                //Geofence
                case 0:
                    checkBox.setChecked(_geofenceEnabled);
                    checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _geofenceEnabled = !_geofenceEnabled;
                            checkBox.setChecked(_geofenceEnabled);
                            if (_geofenceEnabled)
                                GeofenceManager.startGeofenceService(getApplicationContext(), null);
                            else
                                GeofenceManager.stopGeofenceService(getApplicationContext());
                        }
                    });
                    break;
                //Push notification
                case 1:
                    //TODO: update logic here
                    checkBox.setChecked(_pushNotificationEnabled);
                    checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _pushNotificationEnabled = !_pushNotificationEnabled;
                            checkBox.setChecked(_pushNotificationEnabled);
                            _sessionM.setPushNotificationEnabled(_pushNotificationEnabled);
                        }
                    });

                    nameTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _testEventTriggerCount++;
                            if (_testEventTriggerCount == TEST_EVENT_TRIGGER_CAP) {
                                _sessionM.logAction("test-receipt-failure-push");
                                _testEventTriggerCount = 0;
                                Toast.makeText(SettingsActivity.this, "Trigger push sent!", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });
                    break;
                //Background receipt uploading
                case 2:
                    checkBox.setChecked(_backgroundReceiptUploadingEnabled);
                    checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _backgroundReceiptUploadingEnabled = !_backgroundReceiptUploadingEnabled;
                            checkBox.setChecked(_backgroundReceiptUploadingEnabled);
                        }
                    });
                    break;
                //Custom loader view
                case 3:
                    checkBox.setChecked(SEApplication._sampleCustomLoaderView != null);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked)
                                SEApplication._sampleCustomLoaderView = new CustomLoaderView(getApplicationContext());
                            else {
                                SEApplication._sampleCustomLoaderView.removeCustomLoader();
                                SEApplication._sampleCustomLoaderView = null;
                            }
                        }
                    });
                    break;
                //User opt out
                case 4:
                    checkBox.setChecked(_sessionM.getUser().isOptedOut());
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked)
                                _sessionM.getUser().setOptedOut(getApplicationContext(), true);
                            else
                                _sessionM.getUser().setOptedOut(getApplicationContext(), false);
                        }
                    });
                    break;
                //SDK version
                case 5:
                    nameTextView.setText("SDK Version: " + _sessionM.getSDKVersion());
                    checkBox.setVisibility(View.GONE);
                    break;
                //App version
                case 6:
                    nameTextView.setText("App Version: " + VERSION_NUM);
                    checkBox.setVisibility(View.GONE);

                    break;
                //Log out
                case 7:
                    checkBox.setVisibility(View.GONE);
                    rowView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _sessionM.logOutUser();
                        }
                    });
                    break;
                //Exit
                case 8:
                    checkBox.setVisibility(View.GONE);
                    rowView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finishAffinity();
                        }
                    });
                    break;
            }
            return rowView;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Utility.persistStatusBoolean(Utility.PUSH_NOTIFICATION_ENABLED_KEY, _pushNotificationEnabled);
        Utility.persistStatusBoolean(Utility.GEOFENCE_ENABLED_KEY, _geofenceEnabled);
        Utility.persistStatusBoolean(Utility.BACKGROUND_RECEIPT_UPLOADING_ENABLED_KEY, _backgroundReceiptUploadingEnabled);
    }
}
