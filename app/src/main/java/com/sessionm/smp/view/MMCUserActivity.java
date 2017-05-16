package com.sessionm.smp.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.User;
import com.sessionm.api.identity.UserListener;
import com.sessionm.api.identity.UserManager;
import com.sessionm.api.identity.data.SMPUser;
import com.sessionm.api.identity.data.SMPUserUpdate;
import com.sessionm.api.identity.tag.UserTagsListener;
import com.sessionm.api.identity.tag.UserTagsManager;
import com.sessionm.smp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MMCUserActivity extends AppCompatActivity {

    SessionM sessionM = SessionM.getInstance();

    TextView id;
    TextView externalID;
    TextView points;
    TextView availableAchievementsCount;
    TextView testPoints;
    TextView email;
    TextView firstName;
    TextView lastName;
    TextView gender;
    TextView dob;
    TextView createdTime;
    TextView updatedTime;
    TextView zip;
    TextView dma;
    TextView state;
    TextView country;
    TextView latitude;
    TextView longitude;
    TextView nextTierPercentage;
    TextView accountStatus;
    TextView currentZip;
    TextView currentDMA;
    TextView currentState;
    TextView currentCountry;
    TextView proxyIDs;
    TextView profile;
    TextView isSuspended;
    TextView isTestAccount;

    TableLayout tagsTable;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmc_user);

        id = (TextView) findViewById(R.id.mmc_user_id);
        externalID = (TextView) findViewById(R.id.mmc_user_external_id);
        points = (TextView) findViewById(R.id.mmc_user_points);
        availableAchievementsCount = (TextView) findViewById(R.id.mmc_user_unclaimed_achievement_count);
        testPoints = (TextView) findViewById(R.id.mmc_user_test_points);
        email = (TextView) findViewById(R.id.mmc_user_email);
        firstName = (TextView) findViewById(R.id.mmc_user_first_name);
        lastName = (TextView) findViewById(R.id.mmc_user_last_name);
        gender = (TextView) findViewById(R.id.mmc_user_gender);
        dob = (TextView) findViewById(R.id.mmc_user_dob);
        createdTime = (TextView) findViewById(R.id.mmc_user_created_time);
        updatedTime = (TextView) findViewById(R.id.mmc_user_updated_time);
        zip = (TextView) findViewById(R.id.mmc_user_zip);
        dma = (TextView) findViewById(R.id.mmc_user_dma);
        state = (TextView) findViewById(R.id.mmc_user_state);
        country = (TextView) findViewById(R.id.mmc_user_country);
        latitude = (TextView) findViewById(R.id.mmc_user_latitude);
        longitude = (TextView) findViewById(R.id.mmc_user_longitude);
        nextTierPercentage = (TextView) findViewById(R.id.mmc_user_next_tier_percentage);
        accountStatus = (TextView) findViewById(R.id.mmc_user_account_status);
        currentZip = (TextView) findViewById(R.id.mmc_user_current_zip);
        currentDMA = (TextView) findViewById(R.id.mmc_user_current_dma);
        currentState = (TextView) findViewById(R.id.mmc_user_current_state);
        currentCountry = (TextView) findViewById(R.id.mmc_user_current_country);
        proxyIDs = (TextView) findViewById(R.id.mmc_user_proxy_ids);
        profile = (TextView) findViewById(R.id.mmc_user_profile);
        isSuspended = (TextView) findViewById(R.id.mmc_user_is_suspended);
        isTestAccount = (TextView) findViewById(R.id.mmc_user_is_test_account);

        Button updateButton = (Button) findViewById(R.id.mmc_user_update_btn);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpUpdateUserDialog();
            }
        });

        tagsTable = (TableLayout) findViewById(R.id.mmc_tags_table_layout);
        Button updateTagsButton = (Button) findViewById(R.id.mmc_user_tags_update_btn);
        updateTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpUpdateUserTagsDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserManager.getInstance().setListener(_userListener);
        UserTagsManager.getInstance().setListener(_userTagsListener);
        User user = sessionM.getUser();
        if (user != null) {
            UserManager.getInstance().fetchUser();
            UserTagsManager.getInstance().fetchUserTags();
        }
    }

    UserListener _userListener = new UserListener() {
        @Override
        public void onUserUpdated(SMPUser smpUser, Set<String> set) {
            refreshUI(smpUser);
        }

        @Override
        public void onFailure(SessionMError sessionMError) {
            Toast.makeText(MMCUserActivity.this, "Failed! " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    UserTagsListener _userTagsListener = new UserTagsListener() {
        @Override
        public void onUserTagsFetched(Map map) {
            refreshUserTagsTable(map);
        }

        @Override
        public void onUserTagsUpdated(Map map) {
            refreshUserTagsTable(map);
        }

        @Override
        public void onFailure(SessionMError sessionMError) {

        }
    };

    private void refreshUI(SMPUser smpUser) {
        id.setText("ID: " + smpUser.getID());
        externalID.setText("External ID: " + smpUser.getExternalID());
        points.setText("Points: " + smpUser.getAvailablePoints());
        availableAchievementsCount.setText("Unclaimed Achievements Count: " + smpUser.getUnclaimedAchievementCount());
        testPoints.setText("Test Points: " + smpUser.getTestPoints());
        email.setText("Email: " + smpUser.getEmail());
        firstName.setText("First Name: " + smpUser.getFirstName());
        lastName.setText("Last Name: " + smpUser.getLastName());
        gender.setText("Gender: " + smpUser.getGender());
        dob.setText("DOB: " + smpUser.getDateOfBirth());
        createdTime.setText("Created Time: " + smpUser.getCreatedTime());
        updatedTime.setText("Updated Time: " + smpUser.getUpdatedTime());
        zip.setText("Zip: " + smpUser.getZipCode());
        dma.setText("DMA: " + smpUser.getDMA());
        state.setText("State: " + smpUser.getState());
        country.setText("Country: " + smpUser.getCountry());
        latitude.setText("Latitude: " + smpUser.getLatitude());
        longitude.setText("Longitude: " + smpUser.getLongitude());
        nextTierPercentage.setText("Next Tier Percentage: " + smpUser.getNextTierPercentage());
        accountStatus.setText("Account Status: " + smpUser.getAccountStatus());
        currentZip.setText("Current Zip: " + smpUser.getCurrentZipCode());
        currentDMA.setText("Current DMA: " + smpUser.getCurrentDMA());
        currentState.setText("Current State: " + smpUser.getCurrentState());
        currentCountry.setText("Current Country: " + smpUser.getCurrentCountry());
        if (smpUser.getProxyIDs() != null)
            proxyIDs.setText("Proxy IDs: " + smpUser.getProxyIDs().toString());
        if (smpUser.getUserProfile() != null)
            profile.setText("User Profile: " + smpUser.getUserProfile().toString());
        isSuspended.setText("Suspended: " + smpUser.isSuspended());
        isTestAccount.setText("Test Account: " + smpUser.isTestAccount());
    }

    private void refreshUserTagsTable(Map tags) {
        tagsTable.removeAllViews();
        for (Object key : tags.keySet()) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView textView = new TextView(this);
            textView.setText(key + ": " + tags.get(key));
            row.addView(textView);
            tagsTable.addView(row);
        }
    }

    private void popUpUpdateUserDialog() {

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_update_user, null);

        final EditText externalIDView = (EditText) dialogLayout.findViewById(R.id.update_user_external_id);
        final EditText emailView = (EditText) dialogLayout.findViewById(R.id.update_user_email);
        final EditText genderView = (EditText) dialogLayout.findViewById(R.id.update_user_gender);
        final EditText lastNameView = (EditText) dialogLayout.findViewById(R.id.update_user_last_name);
        final EditText firstNameView = (EditText) dialogLayout.findViewById(R.id.update_user_first_name);
        final EditText dobView = (EditText) dialogLayout.findViewById(R.id.update_user_dob);
        final EditText dmaView = (EditText) dialogLayout.findViewById(R.id.update_user_dma);
        final EditText zipView = (EditText) dialogLayout.findViewById(R.id.update_user_zip);
        final EditText stateView = (EditText) dialogLayout.findViewById(R.id.update_user_state);
        final EditText countryView = (EditText) dialogLayout.findViewById(R.id.update_user_country);
        final EditText latitudeView = (EditText) dialogLayout.findViewById(R.id.update_user_latitude);
        final EditText longitudeView = (EditText) dialogLayout.findViewById(R.id.update_user_longitude);
        final EditText ipAddressView = (EditText) dialogLayout.findViewById(R.id.update_user_ip_address);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SMPUserUpdate.Builder smpUserRequest = new SMPUserUpdate.Builder()
                        .externalID(updateNonEmptyField(externalIDView))
                        .email(updateNonEmptyField(emailView))
                        .gender(updateNonEmptyField(genderView))
                        .firstName(updateNonEmptyField(firstNameView))
                        .lastName(updateNonEmptyField(lastNameView))
                        .dateOfBirth(updateNonEmptyField(dobView))
                        .zipCode(updateNonEmptyField(zipView))
                        .DMA(updateNonEmptyField(dmaView))
                        .state(updateNonEmptyField(stateView))
                        .country(updateNonEmptyField(countryView))
                        .latitude(updateNonEmptyFieldDouble(latitudeView))
                        .longitude(updateNonEmptyFieldDouble(longitudeView))
                        .ipAddress(updateNonEmptyField(ipAddressView))
                        .locale(Locale.getDefault());

                UserManager.getInstance().updateUser(smpUserRequest.build());
            }
        });

        AlertDialog dialog = builder.create();


        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });

        dialog.show();
    }

    private void popUpUpdateUserTagsDialog() {
        LayoutInflater inflater = getLayoutInflater();
        final View dialogLayout = inflater.inflate(R.layout.dialog_update_user_tags, null);

        final List<EditText> tagsViews = new ArrayList<>();
        final TableLayout newTagsTable = (TableLayout) dialogLayout.findViewById(R.id.update_tags_table_layout);

        final EditText addTagEditView = (EditText) dialogLayout.findViewById(R.id.update_tags_add_tag_edittext);
        final EditText ttlEditView = (EditText) dialogLayout.findViewById(R.id.update_tags_ttl_edittext);

        tagsViews.add(addTagEditView);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setNeutralButton("Add Tag", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<String> tags = new ArrayList<>();
                for (EditText editText : tagsViews) {
                    tags.add(editText.getText().toString());
                }
                if (!ttlEditView.getText().toString().isEmpty()) {
                    long ttl = Long.parseLong(ttlEditView.getText().toString());
                    UserTagsManager.getInstance().updateUserTags(tags, ttl);
                } else
                    UserTagsManager.getInstance().updateUserTags(tags);
            }
        });

        AlertDialog dialog = builder.create();

        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableRow row = new TableRow(dialogLayout.getContext());
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                EditText editText = new EditText(dialogLayout.getContext());
                editText.setHint("New Tag");
                row.addView(editText);
                tagsViews.add(editText);
                newTagsTable.addView(row, 0);
            }
        });
    }

    private String updateNonEmptyField(EditText editText) {
        if (!editText.getText().toString().isEmpty())
            return editText.getText().toString();
        return null;
    }

    private double updateNonEmptyFieldDouble(EditText editText) {
        if (!editText.getText().toString().isEmpty())
            return Double.parseDouble(editText.getText().toString());
        return 0;
    }
}
