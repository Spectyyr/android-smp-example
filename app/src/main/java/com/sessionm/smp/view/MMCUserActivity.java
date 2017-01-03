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
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.User;
import com.sessionm.api.identity.IdentityListener;
import com.sessionm.api.identity.data.MMCUser;
import com.sessionm.api.identity.data.MMCUserUpdate;
import com.sessionm.api.identity.data.SMSVerification;
import com.sessionm.smp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    TableLayout metadataTable;

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

        metadataTable = (TableLayout) findViewById(R.id.mmc_metadata_table_layout);
        Button updateMetadataButton = (Button) findViewById(R.id.mmc_user_metadata_update_btn);
        updateMetadataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpUpdateUserMetadataDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionM.getIdentityManager().setListener(_identifyListener);
        User user = sessionM.getUser();
        if (user != null) {
            sessionM.getIdentityManager().fetchMMCUser();
            sessionM.getIdentityManager().fetchMMCUserTags();
            sessionM.getIdentityManager().fetchMMCUserMetadata();
        }
    }

    IdentityListener _identifyListener = new IdentityListener() {
        @Override
        public void onSMSVerificationMessageSent(SMSVerification smsVerification) {
        }

        @Override
        public void onSMSVerificationCodeChecked(SMSVerification smsVerification) {
        }

        @Override
        public void onSMSVerificationFetched(SMSVerification smsVerification) {
        }

        @Override
        public void onMMCUserFetched(MMCUser mmcUser) {
            refreshUI(mmcUser);
        }

        @Override
        public void onMMCUserUpdated(MMCUser mmcUser) {
            Toast.makeText(MMCUserActivity.this, "Success!", Toast.LENGTH_SHORT).show();
            refreshUI(mmcUser);
        }

        @Override
        public void onMMCUserTagsFetched(Map tags) {
            refreshUserTagsTable(tags);
        }

        @Override
        public void onMMCUserTagsUpdated(Map tags) {
            refreshUserTagsTable(tags);
        }

        @Override
        public void onMMCUserMetadataFetched(Map metadata) {
            refreshUserMetadataTable(metadata);
        }

        @Override
        public void onMMCUserMetadataUpdated(Map metadata) {
            refreshUserMetadataTable(metadata);
        }

        @Override
        public void onFailure(SessionMError sessionMError) {
            Toast.makeText(MMCUserActivity.this, "Failed! " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void refreshUI(MMCUser mmcUser) {
        id.setText("ID: " + mmcUser.getID());
        externalID.setText("External ID: " + mmcUser.getExternalID());
        points.setText("Points: " + mmcUser.getAvailablePoints());
        availableAchievementsCount.setText("Unclaimed Achievements Count: " + mmcUser.getUnclaimedAchievementCount());
        testPoints.setText("Test Points: " + mmcUser.getTestPoints());
        email.setText("Email: " + mmcUser.getEmail());
        firstName.setText("First Name: " + mmcUser.getFirstName());
        lastName.setText("Last Name: " + mmcUser.getLastName());
        gender.setText("Gender: " + mmcUser.getGender());
        dob.setText("DOB: " + mmcUser.getDateOfBirth());
        createdTime.setText("Created Time: " + mmcUser.getCreatedTime());
        updatedTime.setText("Updated Time: " + mmcUser.getUpdatedTime());
        zip.setText("Zip: " + mmcUser.getZipCode());
        dma.setText("DMA: " + mmcUser.getDMA());
        state.setText("State: " + mmcUser.getState());
        country.setText("Country: " + mmcUser.getCountry());
        latitude.setText("Latitude: " + mmcUser.getLatitude());
        longitude.setText("Longitude: " + mmcUser.getLongitude());
        nextTierPercentage.setText("Next Tier Percentage: " + mmcUser.getNextTierPercentage());
        accountStatus.setText("Account Status: " + mmcUser.getAccountStatus());
        currentZip.setText("Current Zip: " + mmcUser.getCurrentZipCode());
        currentDMA.setText("Current DMA: " + mmcUser.getCurrentDMA());
        currentState.setText("Current State: " + mmcUser.getCurrentState());
        currentCountry.setText("Current Country: " + mmcUser.getCurrentCountry());
        if (mmcUser.getProxyIDs() != null)
            proxyIDs.setText("Proxy IDs: " + mmcUser.getProxyIDs().toString());
        if (mmcUser.getUserProfile() != null)
            profile.setText("User Profile: " + mmcUser.getUserProfile().toString());
        isSuspended.setText("Suspended: " + mmcUser.isSuspended());
        isTestAccount.setText("Test Account: " + mmcUser.isTestAccount());
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

    private void refreshUserMetadataTable(Map metadata) {
        metadataTable.removeAllViews();
        for (Object key : metadata.keySet()) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView textView = new TextView(this);
            textView.setText(key + ": " + metadata.get(key));
            row.addView(textView);
            metadataTable.addView(row);
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
                MMCUserUpdate mmcUserRequest = new MMCUserUpdate();
                mmcUserRequest.setExternalID(updateNonEmptyField(externalIDView));
                mmcUserRequest.setEmail(updateNonEmptyField(emailView));
                mmcUserRequest.setGender(updateNonEmptyField(genderView));
                mmcUserRequest.setFirstName(updateNonEmptyField(firstNameView));
                mmcUserRequest.setLastName(updateNonEmptyField(lastNameView));
                mmcUserRequest.setDateOfBirth(updateNonEmptyField(dobView));
                mmcUserRequest.setZipCode(updateNonEmptyField(zipView));
                mmcUserRequest.setDMA(updateNonEmptyField(dmaView));
                mmcUserRequest.setState(updateNonEmptyField(stateView));
                mmcUserRequest.setCountry(updateNonEmptyField(countryView));
                mmcUserRequest.setLatitude(updateNonEmptyFieldDouble(latitudeView));
                mmcUserRequest.setLongitude(updateNonEmptyFieldDouble(longitudeView));
                mmcUserRequest.setIPAddress(updateNonEmptyField(ipAddressView));
                mmcUserRequest.setLocale(Locale.getDefault());
                /* Also supports additional user profile filed with customized key, such as children
                Map<String, Map[]> childrenMap = new HashMap<>();
                Map<String, String> child1 = new HashMap<>();
                child1.put("gender", "m");
                child1.put("dob", "2016-04-01");
                Map<String, String> child2 = new HashMap<>();
                child2.put("gender", "f");
                child2.put("dob", "2016-04-01");
                Map[] children = {child1, child2};
                childrenMap.put("children", children);
                mmcUserRequest.addUserProfile(childrenMap);
                */
                sessionM.getIdentityManager().updateMMCUser(mmcUserRequest);
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
                    sessionM.getIdentityManager().updateMMCUserTags(tags, ttl);
                } else
                    sessionM.getIdentityManager().updateMMCUserTags(tags);
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

    private void popUpUpdateUserMetadataDialog() {
        LayoutInflater inflater = getLayoutInflater();
        final View dialogLayout = inflater.inflate(R.layout.dialog_update_user_metadata, null);

        final List<EditText> keyViews = new ArrayList<>();
        final List<EditText> valueViews = new ArrayList<>();

        final TableLayout newTagsTable = (TableLayout) dialogLayout.findViewById(R.id.update_metadata_table_layout);

        final EditText keyEditView = (EditText) dialogLayout.findViewById(R.id.update_metadata_add_key_edittext);
        final EditText valueEditView = (EditText) dialogLayout.findViewById(R.id.update_metadata_add_value_edittext);

        keyViews.add(keyEditView);
        valueViews.add(valueEditView);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setNeutralButton("Add Pair", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String, Object> pairs = new HashMap<>();
                for (int i = 0; i < keyViews.size(); i++) {
                    EditText keyView = keyViews.get(i);
                    EditText valueView = valueViews.get(i);
                    pairs.put(keyView.getText().toString(), valueView.getText().toString());
                }
                sessionM.getIdentityManager().updateMMCUserMetadata(pairs);
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
                LinearLayout linearLayout = new LinearLayout(dialogLayout.getContext());

                EditText keyEditText = new EditText(dialogLayout.getContext());
                keyEditText.setHint("Key");
                linearLayout.addView(keyEditText);
                keyViews.add(keyEditText);

                EditText valueEditText = new EditText(dialogLayout.getContext());
                valueEditText.setHint("Value");
                linearLayout.addView(valueEditText);
                valueViews.add(valueEditText);

                row.addView(linearLayout);
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
