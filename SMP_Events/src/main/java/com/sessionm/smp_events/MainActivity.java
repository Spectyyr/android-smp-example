package com.sessionm.smp_events;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sessionm.api.SessionMError;
import com.sessionm.api.common.data.behavior.Behavior;
import com.sessionm.api.common.data.behavior.CompositeBehavior;
import com.sessionm.api.common.data.behavior.CountBehavior;
import com.sessionm.api.common.data.behavior.Goal;
import com.sessionm.api.common.data.behavior.Group;
import com.sessionm.api.common.data.behavior.UniqueBehavior;
import com.sessionm.api.events.EventsListener;
import com.sessionm.api.events.EventsManager;
import com.sessionm.api.events.data.EventPostedResponse;
import com.sessionm.api.events.data.EventsResponse;
import com.sessionm.api.events.data.ProgressFetchedResponse;
import com.sessionm.api.events.data.builders.activity.ActivityEventBuilder;
import com.sessionm.api.events.data.builders.activity.ActivityItemBuilder;
import com.sessionm.api.events.data.builders.purchase.PurchaseEventBuilder;
import com.sessionm.api.events.data.builders.purchase.PurchaseEventItemBuilder;
import com.sessionm.api.identity.IdentityManager;
import com.sessionm.api.identity.UserListener;
import com.sessionm.api.identity.UserManager;
import com.sessionm.api.identity.data.SMPUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA3LTE0IDE4OjM4OjIwICswMDAwIiwiZXhwIjoiMjAxNy0wNy0yOCAxODozODoyMCArMDAwMCJ9.wXLHwQYWtfXA4_Kn4mBrdPXFsMvrCdHaLr4GK67CoPUx3jDwKXX4Wg0HPDjY5RFPzLdOAZGnPXhSna0rVkIkxEzEi0I6gzx_6CggUluxMJnDMUW5HHG0yo040e6tgqIl99VAZZZFbIwCF7qiDnIH01H7IdZz8e0uokq2TaHTKLoo16sUJCJIgSNfOkaRfS9uvlcwFftdH-wqZl5KZ3kUqscAW0lqEVcLdxUaA76Oc0bUFEuvpIRX7iWzAM-nIZcLPCCpRqtqaN3LnuorMxytcgYNUmec6F5228wK7X1mN3C8NbMD24SHRQnVtV4hsTNzycA23CnlwjZJhiye4n7FqQ";

    private TextView userBalanceTextView;

    private TextView _result;
    private TextView _event;
    private boolean _byBehavior;
    private EventsResponse _response;
    private ToggleButton _toggle;

    private TextView _logTextView;
    private EditText _name;
    private EditText _quantity;
    private EditText _price;
    private EditText _store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar actionBar = (Toolbar) findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);

        userBalanceTextView = (TextView) findViewById(R.id.user_balance_textview);
        userBalanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserManager.getInstance().getCurrentUser() == null)
                    IdentityManager.getInstance().authenticateCoalitionWithToken(SAMPLE_USER_TOKEN);
                else
                    IdentityManager.getInstance().logOutUser();
            }
        });


        _result = (TextView)findViewById(R.id.result);
        _event = (TextView)findViewById(R.id.event);
        _toggle = (ToggleButton)findViewById(R.id.by_event_name);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserManager.getInstance().setListener(_userListener);
        EventsManager.getInstance().setListener(_eventsListener);
        _byBehavior = _toggle.isChecked();
        EventsManager.getInstance().fetchBehaviorProgress();
    }

    UserListener _userListener = new UserListener() {
        @Override
        public void onUserUpdated(SMPUser smpUser, Set<String> set) {
            if (smpUser != null) {
                userBalanceTextView.setText(smpUser.getAvailablePoints() + "pts");
            } else
                userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
            EventsManager.getInstance().fetchBehaviorProgress();
        }

        @Override
        public void onFailure(SessionMError sessionMError) {

        }
    };

    public void doPostEvent(View view) {
        if (_event.getText().length() > 0) {
            ActivityItemBuilder itemBuilder = new ActivityItemBuilder();
            EventsManager.getInstance().postEvent(new ActivityEventBuilder(_event.getText().toString()).addItemBuilder(itemBuilder).build(), EventsManager.WhenToSend.ASAP);
        } else {
            Toast.makeText(MainActivity.this, "Please enter an event name", Toast.LENGTH_LONG).show();
        }
    }

    public void doPurchaseEvent(View view) {
        final boolean[] done = {false};
        AlertDialog dialog;

        final ActivityEventBuilder eventBuilder = new ActivityEventBuilder(_event.getText().toString());
        final PurchaseEventBuilder purchaseBuilder = new PurchaseEventBuilder();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.events_purchase, null)).setCancelable(false)
                .setNeutralButton("Add Item", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {}
                })
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

        final AlertDialog dilog = builder.create();
        dilog.show();
        dilog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PurchaseEventItemBuilder builder = new PurchaseEventItemBuilder();
                boolean add = false;

                _logTextView.setText(String.format("%s\nAdd Item: %s:%s:%s:%s", _logTextView.getText(), _name.getText(), _quantity.getText(), _price.getText(), _store.getText()));
                if (_name.getText().length() > 0) {
                    builder.name(String.valueOf(_name.getText()));
                    add = true;
                }
                if ((_quantity.getText().length() > 0) && (Integer.parseInt(String.valueOf(_quantity.getText())) > 0)) {
                    builder.quantity(Integer.parseInt(_quantity.getText().toString()));
                    add = true;
                }
                if ((_price.getText().length() > 0) && (!Double.isNaN(Double.parseDouble(String.valueOf(_price.getText()))))){
                    builder.priceAmount(Double.parseDouble(String.valueOf(_price.getText())));
                    add = true;
                }
                if (_store.getText().length() > 0) {
                    builder.store(String.valueOf(_store.getText()));
                    add = true;
                }

                if (add) {
                    purchaseBuilder.addItemBuilder(builder);
                }
            }
        });
        dilog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PurchaseEventBuilder evnt = purchaseBuilder;
                Log.d("TAG", String.valueOf(purchaseBuilder));
                EventsManager.getInstance().postEvent(purchaseBuilder, EventsManager.WhenToSend.ASAP);
            }
        });

        _logTextView = (TextView)dilog.findViewById(R.id.logTextView);
        _name = (EditText) dilog.findViewById(R.id.nameText);
        _quantity = (EditText) dilog.findViewById(R.id.qtyText);
        _price = (EditText) dilog.findViewById(R.id.priceText);
        _store = (EditText) dilog.findViewById(R.id.storeText);
    }

    public void doQuickPostEvent(View view) {
        if (_event.getText().length() > 0) {
            EventsManager.SimpleEventBuilder builder = new EventsManager.SimpleEventBuilder(_event.getText().toString());
            EventsManager.getInstance().postEvent(builder, EventsManager.WhenToSend.ASAP);
        } else {
            Toast.makeText(MainActivity.this, "Please enter an event name", Toast.LENGTH_LONG).show();
        }
    }

    public void doFetchProgress(View view) {
        EventsManager.getInstance().fetchBehaviorProgress();
    }

    private EventsListenerImpl _eventsListener = new EventsListenerImpl();

    public void doFetchShowByEventName(View view) {
        _byBehavior = _toggle.isChecked();
        buildList(_byBehavior, "Event", _response);
    }

    private class EventsListenerImpl implements EventsListener {
        @Override
        public void onProgressFetched(ProgressFetchedResponse response) {
            _response = response;
            buildList(_byBehavior, "Progress", _response);
        }

        @Override
        public void onFailure(SessionMError error) {
            _response = null;
            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onEventPosted(EventPostedResponse response) {
            _response = response;
            buildList(_byBehavior, "Event Posted", _response);
        }
    }

    private void buildList(boolean _byBehavior, String what, EventsResponse response) {
        if (_byBehavior) {
            showByBehavior(what, response);
        } else {
            showByEventName(what, response);
        }
    }

    private class Each {

    }

    private void showByEventName(String what, EventsResponse response) {
        StringBuilder out = new StringBuilder();
        if (response == null) {

        } else {
            out.append(String.format("(%s) Points: %d\n\n", what, response.getAvailablePoints()));

            Map<String, Each> events = new HashMap<>();
            for (Map.Entry<String, Behavior> behavior : response.getBehaviors().entrySet()) {
                switch (behavior.getValue().getType()) {
                    case Composite:
                        CompositeBehavior composite = (CompositeBehavior) behavior.getValue();
                        for (Map.Entry<String, Group> group : composite.getGroups().entrySet()) {
                            for (Map.Entry<String, Goal> goale : group.getValue().getGoals().entrySet()) {
                                Goal goal = goale.getValue();
                            }
                        }
                        break;
                    case Count:
                        CountBehavior count = (CountBehavior) behavior.getValue();
                        break;
                    case Unique:
                        UniqueBehavior unique = (UniqueBehavior) behavior.getValue();
                        break;
                    default:
                        break;
                }
            }
        }
        out.append("Behaviors: \n");
        out.append("Notifications: \n");
        _result.setText(out.toString());
    }

    private void showByBehavior(String what, EventsResponse response) {
        StringBuilder out = new StringBuilder(String.format("(%s) Points: %d\n\n", what, response.getAvailablePoints()));
        out.append("Behaviors: \n");
        for (Map.Entry<String, Behavior>behavior : response.getBehaviors().entrySet()) {
            out.append(String.format("  '%s' (%s)\n", behavior.getKey(), behavior.getValue().getType()));
            switch (behavior.getValue().getType()) {
                case Composite:
                    CompositeBehavior composite = (CompositeBehavior)behavior.getValue();
                    for (Map.Entry<String, Group> group : composite.getGroups().entrySet()) {
                        for (Map.Entry<String, Goal> goale : group.getValue().getGoals().entrySet()) {
                            Goal goal = goale.getValue();

                            if (goal.getProgress().getType() == Behavior.BehaviorType.Count) {
                                CountBehavior progress = (CountBehavior) goal.getProgress();
                                out.append(String.format("     %s %d of %d '%s'\n", ((CompositeBehavior) behavior.getValue()).getAchieved(), progress.getCurrentCount(), progress.getTotalCount(), progress.getEventName()));
                            } else {
                                UniqueBehavior progress = (UniqueBehavior) goal.getProgress();
                                out.append(String.format("     %s %d of %d '%s'\n", ((CompositeBehavior) behavior.getValue()).getAchieved(), progress.getCurrentCount(), progress.getTotalCount(), progress.getPoints()));
                            }
                        }
                    }
                    break;
                case Count:
                    CountBehavior count = (CountBehavior)behavior.getValue();
                    out.append(String.format("       %d of %d '%s'\n", count.getCurrentCount(), count.getTotalCount(), count.getEventName()));
                    break;
                case Unique:
                    UniqueBehavior unique = (UniqueBehavior)behavior.getValue();
                    out.append(String.format("       %d of %d '%s'\n", unique.getCurrentCount(), unique.getTotalCount(), unique.getCompletedUniques()));
                    break;
                default:
                    out.append("A different kind of Behavior\n");
            }
        }
        _result.setText(out.toString());
    }
}
