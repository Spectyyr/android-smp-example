/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.sessionm.api.AchievementData;
import com.sessionm.api.SessionListener;
import com.sessionm.api.SessionM;
import com.sessionm.api.User;
import com.sessionm.api.message.data.Message;
import com.sessionm.api.message.feed.ui.ActivityFeedActivity;
import com.sessionm.api.receipt.ui.ReceiptActivity;
import com.sessionm.mmc.R;

//Having the MainActivity implement the SessionM SessionListener allows the developer to listen on the SessionM Session State and update the activity:
//- when the Session.State changes (Starting, Started_online, Started_offline, Stopped, Stopping)
//- if the session fails to start (Started_offline)
//- when the SessionM User object is updated
//- when the Feed Message Data list has been updated
//- when the User Activities Data list has been updated
//- when a receipt image has been updated or uploaded
//- if a push notification is available
//- when a user has unclaimed achievements

public class MainActivity extends AppCompatActivity implements SessionListener, ViewPager.OnPageChangeListener {

    private ViewPager pager;

    private CampaignsFragment messageFragment;
    private RewardsFragment rewardsFragment;
    private TransactionsFragment transactionsFragment;
    private ReceiptsFragment receiptsFragment;
    private OrdersFragment ordersFragment;
    private Fragment loyaltyFragment;
    private ActionBar actionBar;
    private static FloatingActionsMenu actionsMenu;
    private static com.getbase.floatingactionbutton.FloatingActionButton newUploadButton;
    private static com.getbase.floatingactionbutton.FloatingActionButton linkCardButton;

    SessionM sessionM = SessionM.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.addOnPageChangeListener(this);
        actionsMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        newUploadButton = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.action_upload_receipt);
        linkCardButton = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.action_link_card);

        newUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionsMenu.collapse();
                sessionM.getReceiptManager().setUploadReceiptActivityColors(null, null, null, "#A3BE5F", null);
                startActivity(new Intent(MainActivity.this, ReceiptActivity.class));
            }
        });

        linkCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionsMenu.collapse();
                startActivity(new Intent(MainActivity.this, LoyaltyCardActivity.class));
            }
        });

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setIndicatorColor(Color.WHITE);
        tabs.setViewPager(pager);
    }

    private final String[] TITLES = {"Opportunities", "Rewards", "Transactions", "Loyalty Card", "Receipts", "Orders"};

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
                    transactionsFragment = TransactionsFragment.newInstance();
                    fragment = transactionsFragment;
                    break;
                case 3:
                    loyaltyFragment = LoyaltyFragment.newInstance();
                    fragment = loyaltyFragment;
                    break;
                case 4:
                    receiptsFragment = ReceiptsFragment.newInstance();
                    fragment = receiptsFragment;
                    break;
                case 5:
                    ordersFragment = OrdersFragment.newInstance();
                    fragment = ordersFragment;
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
        }
    }

    @Override
    public void onUnclaimedAchievement(SessionM sessionM, AchievementData achievementData) {
    }

    @Override
    public void onNotificationMessage(SessionM sessionM, Message message) {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.portal:
                sessionM.presentActivity(SessionM.ActivityType.PORTAL);
                return true;
            case R.id.feed:
                startActivity(new Intent(this, ActivityFeedActivity.class));
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void hideFAB() {
        actionsMenu.collapse();
    }

    public static void showFAB() {
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
}
