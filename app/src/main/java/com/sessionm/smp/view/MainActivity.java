/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.sessionm.api.AchievementData;
import com.sessionm.api.SessionListener;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.User;
import com.sessionm.api.identity.IdentityListener;
import com.sessionm.api.identity.data.MMCUser;
import com.sessionm.api.identity.data.SMSVerification;
import com.sessionm.api.message.data.Message;
import com.sessionm.api.message.notification.data.NotificationMessage;
import com.sessionm.api.receipt.ReceiptsManager;
import com.sessionm.smp.R;
import com.sessionm.smp.service.ReceiptUploadingService;
import com.sessionm.smp.util.LocationObserver;
import com.sessionm.smp.util.PermissionUtils;
import com.sessionm.smp.util.Utility;

import java.util.Map;

//Having the MainActivity implement the SessionM SessionListener allows the developer to listen on the SessionM Session State and update the activity:
//- when the Session.State changes (Starting, Started_online, Started_offline, Stopped, Stopping)
//- if the session fails to start (Started_offline)
//- when the SessionM User object is updated
//- when the Feed Message Data list has been updated
//- when the User Activities Data list has been updated
//- when a receipt image has been updated or uploaded
//- if a push notification is available
//- when a user has unclaimed achievements

public class MainActivity extends AppCompatActivity implements SessionListener, ViewPager.OnPageChangeListener,
        CampaignsFragment.OnDeepLinkTappedListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private MaterialViewPager materialViewPager;
    private Toolbar toolbar;
    private ViewPager pager;

    private CampaignsFragment messageFragment;
    private RewardsFragment rewardsFragment;
    private TransactionsFragment transactionsFragment;
    private ReceiptsFragment receiptsFragment;
    private OrdersFragment ordersFragment;
    private ReferralsFragment referralsFragment;
    private LoyaltyFragment loyaltyFragment;
    private PlacesFragment placesFragment = PlacesFragment.newInstance();
    private TextView userNameTextView;
    private TextView userPointsTextView;
    private FloatingActionsMenu actionsMenu;
    private com.getbase.floatingactionbutton.FloatingActionButton newUploadButton;
    private com.getbase.floatingactionbutton.FloatingActionButton linkCardButton;
    private com.getbase.floatingactionbutton.FloatingActionButton referAFriendButton;
    ProgressDialog progressDialog;

    SessionM sessionM = SessionM.getInstance();
    private LocationObserver locationObserver;

    NotificationMessage notificationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Gets pending push notification
        Bundle extras = getIntent().getExtras();
        notificationMessage = sessionM.getMessageManager().getPendingNotification(extras);

        materialViewPager = (MaterialViewPager) findViewById(R.id.pager);

        materialViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    default:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.colorPrimary,
                                "https://2zembb3yz9dn2vihvw2zflvc-wpengine.netdna-ssl.com/wp-content/uploads/2016/06/SessionMhompagev2.png");
                }
            }
        });

        toolbar = materialViewPager.getToolbar();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            Toolbar toolbar = materialViewPager.getToolbar();

            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        pager = materialViewPager.getViewPager();
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.addOnPageChangeListener(this);
        materialViewPager.getPagerTitleStrip().setViewPager(materialViewPager.getViewPager());
        actionsMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);

        userPointsTextView = (TextView) findViewById(R.id.header_points_textview);
        userNameTextView = (TextView) findViewById(R.id.header_name_textview);
        userNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MMCUserActivity.class));
            }
        });

        newUploadButton = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.action_upload_receipt);
        linkCardButton = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.action_link_card);
        referAFriendButton = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.action_refer_a_friend);

        newUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionsMenu.collapse();
                checkHasIncompleteReceipts();
            }
        });

        linkCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionsMenu.collapse();
                startActivity(new Intent(MainActivity.this, LoyaltyCardActivity.class));
            }
        });

        referAFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionsMenu.collapse();
                //7 for refer a friend fragment
                pager.setCurrentItem(7);
                referralsFragment.popUpCreateReferralDialog();
            }
        });

        // Create an instance of location observer.
        enableMyLocation();
    }

    @Override
    protected void onStart() {
        if (locationObserver != null)
            locationObserver.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (locationObserver != null)
            locationObserver.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (notificationMessage != null) {
            sessionM.getMessageManager().executePendingNotificationFromPush(notificationMessage);
        }
        sessionM.getIdentityManager().setListener(_identifyListener);
        User user = sessionM.getUser();
        if (user != null) {
            sessionM.getIdentityManager().fetchMMCUser();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_portal) {
            sessionM.presentActivity(SessionM.ActivityType.PORTAL);
            return true;
        }

        if (id == R.id.menu_item_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final String[] TITLES = {"Opportunities", "Rewards", "Places", "Transactions", "Loyalty Card", "Receipts", "Orders", "Referrals"};

    //Handle deep link from campaigns fragment
    @Override
    public void onDeepLinkTapped(Message.MessageActionType actionType, String actionURL) {
        if (actionURL == null)
            return;
        //Handle external link
        if (actionType.equals(Message.MessageActionType.EXTERNAL_LINK)) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(actionURL));
            try {
                startActivity(i);
            } catch (ActivityNotFoundException e) {
            }
            return;
        }
        //Handle deep link
        if (actionType.equals(Message.MessageActionType.DEEP_LINK)) {
            if (actionURL.contains("places")) {
                //2 for places fragment
                pager.setCurrentItem(2);
                if (placesFragment.isResumed())
                    placesFragment.fetchPlaces(parseActionID(actionURL));
            } else if (actionURL.contains("receipt")) {
                checkHasIncompleteReceipts();
            } else if (actionURL.contains("history")) {
                //3 for redeem fragment
                pager.setCurrentItem(3);
            } else if (actionURL.contains("redeem")) {
                //1 for redeem fragment
                pager.setCurrentItem(1);
            }
        }
    }

    private String parseActionID(String typeURL) {
        String actionID = "";
        if (typeURL == null)
            return actionID;
        if (typeURL.toLowerCase().contains("id"))
            actionID = typeURL.toLowerCase().substring(typeURL.indexOf('=') + 1);
        return actionID;
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        //TODO: Hide promotions/submissions tabs for now, not fully supported

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public long getItemId(int position) {
            Log.d("getItemId", String.format("%d", position));
            return super.getItemId(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("getItem", String.format("%d", position));
            //TODO: Hide promotions/submissions tabs for now, not fully supported
            Fragment fragment = CampaignsFragment.newInstance();
            switch (position) {
                case 0:
                    messageFragment = CampaignsFragment.newInstance();
                    fragment = messageFragment;
                    break;
                case 1:
                    rewardsFragment = RewardsFragment.newInstance();
                    fragment = rewardsFragment;
                    break;
                case 2:
                    fragment = placesFragment;
                    break;
                case 3:
                    transactionsFragment = TransactionsFragment.newInstance();
                    fragment = transactionsFragment;
                    break;
                case 4:
                    loyaltyFragment = LoyaltyFragment.newInstance();
                    fragment = loyaltyFragment;
                    break;
                case 5:
                    receiptsFragment = ReceiptsFragment.newInstance();
                    fragment = receiptsFragment;
                    break;
                case 6:
                    ordersFragment = OrdersFragment.newInstance();
                    fragment = ordersFragment;
                    break;
                case 7:
                    referralsFragment = ReferralsFragment.newInstance();
                    fragment = referralsFragment;
                    break;
            }
            return fragment;
        }
    }

    //Listen for changes in the Session State
    @Override
    public void onSessionStateChanged(SessionM sessionM, SessionM.State state) {
    }

    @Override
    public void onSessionFailed(SessionM sessionM, int i) {

    }

    //Listen for changes to the user
    @Override
    public void onUserUpdated(SessionM sessionM, User user) {
        if (user != null) {
            SessionM.EnrollmentResultType resultType = sessionM.getEnrollmentResult();
            //Authentication failed
            if (resultType.equals(SessionM.EnrollmentResultType.FAILURE)) {
                String errorMessage = SessionM.getInstance().getResponseErrorMessage();
                Toast.makeText(this, "Authentication Failed! " + errorMessage, Toast.LENGTH_SHORT).show();
            }
            sessionM.getIdentityManager().fetchMMCUser();
        }
    }

    @Override
    public void onUnclaimedAchievement(SessionM sessionM, AchievementData achievementData) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == SessionM.RECEIPT_UPLOAD_RESULT_CODE) {
            if (resultCode == RESULT_OK)
                Toast.makeText(this, "Receipt uploaded!", Toast.LENGTH_SHORT).show();
            else if (resultCode == RESULT_CANCELED) {
                String errorMessage = "Back button";
                if (intent != null)
                    errorMessage = intent.getStringExtra("result");
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
            String firstName = mmcUser.getFirstName();
            String lastName = mmcUser.getLastName();
            String name = "Anonymous";
            if ((firstName != null) || (lastName != null)) {
                name = String.format("%s %s", firstName != null ? firstName : "",
                        lastName != null ? lastName : "");
            }
            userNameTextView.setText(name);
            userPointsTextView.setText(mmcUser.getAvailablePoints() + " pts");
        }

        @Override
        public void onMMCUserUpdated(MMCUser mmcUser) {

        }

        @Override
        public void onMMCUserTagsFetched(Map map) {

        }

        @Override
        public void onMMCUserTagsUpdated(Map map) {

        }

        @Override
        public void onMMCUserMetadataFetched(Map map) {

        }

        @Override
        public void onMMCUserMetadataUpdated(Map map) {

        }

        @Override
        public void onFailure(SessionMError sessionMError) {

        }
    };

    private void checkHasIncompleteReceipts() {
        ReceiptsManager receiptsManager = SessionM.getInstance().getReceiptsManager();
        if (receiptsManager.hasIncompleteReceipts()) {
            popUpUploadIncompleteReceiptsDialog();
        } else {
            sessionM.getReceiptsManager().setUploadReceiptActivityColors(null, null, null, "#A3BE5F", null);
            if (Utility.getLocalStatusBoolean(Utility.BACKGROUND_RECEIPT_UPLOADING_ENABLED_KEY))
                sessionM.getReceiptsManager().startUploadReceiptActivityWithoutListener(this, null, null, null);
            else
                sessionM.getReceiptsManager().startUploadReceiptActivity(this, null, null, null);
            Intent startIntent = new Intent(MainActivity.this, ReceiptUploadingService.class);
            startService(startIntent);
        }
    }

    protected void popUpUploadIncompleteReceiptsDialog() {

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_upload_incomplete_receipts, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog = new ProgressDialog(MainActivity.this);
                sessionM.getReceiptsManager().uploadIncompleteReceipt(null, false);
                progressDialog.setMessage(getString(R.string.uploading));
                progressDialog.show();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Incomplete receipt uploading canceled!", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setView(dialogLayout);
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    private void handlePushNotification(NotificationMessage message) {
        if (message != null) {
            Message.MessageActionType actionType = message.getActionType();
            String actionURL = message.getActionURL();
            if (actionType != null && actionURL != null) {
                onDeepLinkTapped(actionType, actionURL);
            }
        }
        notificationMessage = null;
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else {
            locationObserver = LocationObserver.getInstance(this);
            locationObserver.connect();
        }
    }
}
