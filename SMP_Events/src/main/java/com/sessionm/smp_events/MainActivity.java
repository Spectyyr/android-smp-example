package com.sessionm.smp_events;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.SessionMError;
import com.sessionm.api.events.EventsListener;
import com.sessionm.api.events.EventsManager;
import com.sessionm.api.events.data.EventPostedResponse;
import com.sessionm.api.events.data.EventsResponse;
import com.sessionm.api.events.data.ProgressFetchedResponse;
import com.sessionm.api.events.data.builders.activity.ActivityEventBuilder;
import com.sessionm.api.events.data.builders.activity.ActivityItemBuilder;
import com.sessionm.api.events.data.events.activity.ActivityEvent;
import com.sessionm.api.identity.IdentityListener;
import com.sessionm.api.identity.IdentityManager;
import com.sessionm.api.identity.UserListener;
import com.sessionm.api.identity.UserManager;
import com.sessionm.api.identity.data.SMPUser;
import com.sessionm.smp_events.support.BehaviorList;
import com.sessionm.smp_events.support.BehaviorPagerAdapter;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA5LTI3IDE1OjMwOjU1ICswMDAwIiwiZXhwIjoiMjAxNy0xMC0xMSAxNTozMDo1NSArMDAwMCIsImRhdGEiOnsiaWQiOiJkYTYxZGNkYS1hMzk4LTExZTctODcxZi05ZjZkNTQzYmUwNDAifX0.iBrHv9-INszE-SSL9rsuNnLDv7DBBaIUuqM6XDUvecxzap2CuoN4v3juXPvw-dZWuzbiHY2H3TPJJlRcI5_fZPxH2FjDqGA1S5nwEwEYVn9D1oMvnXUB6jLIq3ev4omE7ZUj5zVytsn_rKdryllfHro_8g5TneiOUoFBa_1N_RcC9AK_8640xbYPtZaNWhxsJiCwTsKWaLSYQ6RQv_xo1M4reL56dbjJ16Y-50HUy6Pxax6biKVvpjNRDizrkY0bka07lHMLAHMZD5-D3OYnxpxyg9aVX2kJd36iZuwsKaXVMtrCzwmzzGuhQD1PUUhC43wkNUbYw9z2d94v0FDxvQ";

    private EventsManager _eventManager = EventsManager.getInstance();
    private UserManager _userManager = UserManager.getInstance();
    private IdentityManager _identityManager = IdentityManager.getInstance();

    private SMPUser _smpUser;

    private TextView _eventName;

    private Button authenticateButton;
    private TextView userTextView;
    private TextView userBalanceTextView;

    private TabLayout _tabs;
    private ViewPager _pager;
    private BehaviorList _behaviorList;
    private BehaviorPagerAdapter _adapter;
    private ImageButton _pb;

    public void doFetchProgress(View view) {

        //
        // Fetch Behaviors for current User
        //

        _eventManager.fetchBehaviorProgress();
    }

    public void doPostEvent(View view) {
        if (_eventName.getText().length() > 0) {

            //
            // Posting an event using an Item
            //

            ActivityItemBuilder itemBuilder = new ActivityItemBuilder();

            ActivityEvent builder = new ActivityEventBuilder(_eventName.getText().toString())
                    .addItemBuilder(itemBuilder).build();

            _eventManager.postEvent(builder, EventsManager.WhenToSend.ASAP);
        } else {
            Toast.makeText(MainActivity.this, "Please enter an event name", Toast.LENGTH_LONG).show();
        }
    }

    public void doQuickPostEvent(View view) {
        if (_eventName.getText().length() > 0) {

            //
            // Posting an event using the SimpleEventBuilder  (Makes single item events easier)
            //

            EventsManager.SimpleEventBuilder builder = new EventsManager.SimpleEventBuilder(_eventName.getText().toString());
            _eventManager.postEvent(builder, EventsManager.WhenToSend.ASAP);
        } else {
            Toast.makeText(MainActivity.this, "Please enter an event name", Toast.LENGTH_LONG).show();
        }
    }

    public void doPurchaseEvent(View view) {
        startActivity(new Intent(this, PurchaseEventActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar();

        _eventName = (TextView) findViewById(R.id.event);             // Name of event to Send

        _tabs = (TabLayout) findViewById(R.id.tabs);
        _pager = (ViewPager)findViewById(R.id.pager);

        _adapter = new BehaviorPagerAdapter(getSupportFragmentManager());
        _pager.setAdapter(_adapter);
        _tabs.setupWithViewPager(_pager);

        _pb = (ImageButton) findViewById(R.id.progressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        _identityManager.setListener(_identityListener);
        _userManager.setListener(_userListener);
        _eventManager.setListener(_eventsListener);

        if (_smpUser == null) {
            _identityManager.authenticateCoalitionWithToken(SAMPLE_USER_TOKEN);
        } else {
            _eventManager.fetchBehaviorProgress();
        }

        showUser();
    }

    @Override
    protected void onPause() {
        super.onPause();

        _userManager.setListener(null);
        _eventManager.setListener(null);
    }

    private void showUser() {
        if (_smpUser == null) {
            userBalanceTextView.setText("");
            userTextView.setText("");
            authenticateButton.setText("Sign In");
        } else {
            userTextView.setText(_smpUser.getEmail());
            userBalanceTextView.setText(_smpUser.getAvailablePoints() + " pts");
            authenticateButton.setText("Log Out");
        }
    }

    IdentityListener _identityListener = new IdentityListener() {
        @Override
        public void onAuthStateUpdated(IdentityManager.AuthState authState) {
            if (authState != IdentityManager.AuthState.Authenticated) {
                _smpUser = null;
                showUser();
                Toast.makeText(MainActivity.this, "State: " + authState, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(SessionMError error) {
            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
        }
    };

    // Class to listen to User events from UserManager
    UserListener _userListener = new UserListener() {
        @Override
        public void onUserUpdated(SMPUser smpUser, Set<String> set) {
            _smpUser = smpUser;
            if (smpUser != null) {
                if (set.size() > 0) {

                    //
                    // Fetch the list of Behaviors (Status) for the current User
                    //

                    _eventManager.fetchBehaviorProgress();
                }
            } else {
                authenticateButton.setText("Sign In");
            }
            showUser();
        }

        @Override
        public void onFailure(SessionMError error) {
            _smpUser = null;
            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
        }
    };

    // Listen for the EventsManager callbacks
    private EventsListener _eventsListener = new EventsListener() {
        public EventsResponse _response;

        @Override
        public void onProgressFetched(ProgressFetchedResponse response) {
            _response = response;
            _behaviorList = new BehaviorList(response);
            _adapter.setBehaviors(_behaviorList);
        }

        @Override
        public void onEventPosted(EventPostedResponse response) {
            _response = response;
             _behaviorList = new BehaviorList(_response);
            _adapter.setBehaviors(_behaviorList);
        }

        @Override
        public void onFailure(SessionMError error) {
            _response = null;
            _behaviorList = null;
            _adapter.setBehaviors(_behaviorList);
            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
        }
    };

    private void actionBar() {
        Toolbar actionBar = (Toolbar) findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);

        authenticateButton = (Button)findViewById(R.id.authenticate_button);
        authenticateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_smpUser == null) {
                    _identityManager.authenticateCoalitionWithToken(SAMPLE_USER_TOKEN);
                } else {
                    _identityManager.logOutUser();
                }
            }
        });

        userTextView = (TextView)findViewById(R.id.user_textview);
        userBalanceTextView = (TextView) findViewById(R.id.user_balance_textview);
    }
}
