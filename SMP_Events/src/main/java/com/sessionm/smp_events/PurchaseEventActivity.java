package com.sessionm.smp_events;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.core.api.SessionMError;
import com.sessionm.event.api.EventsListener;
import com.sessionm.event.api.EventsManager;
import com.sessionm.event.api.data.EventPostedResponse;
import com.sessionm.event.api.data.ProgressFetchedResponse;
import com.sessionm.event.api.data.builders.purchase.PurchaseEventBuilder;
import com.sessionm.event.api.data.builders.purchase.PurchaseEventItemBuilder;
import com.sessionm.event.api.data.events.base.EventItem;
import com.sessionm.event.api.data.events.purchase.PurchaseEventItem;
import com.sessionm.smp_events.support.BehaviorList;
import com.sessionm.smp_events.support.BehaviorPagerAdapter;

public class PurchaseEventActivity extends AppCompatActivity {

    private TextView _eventsListTextView;

    private EditText _name;
    private EditText _quantity;
    private EditText _amount;
    private EditText _store;

    private final PurchaseEventBuilder _purchaseBuilder = new PurchaseEventBuilder();
    private EventsManager _eventManager = EventsManager.getInstance();

    private TabLayout _tabs;
    private ViewPager _pager;
    private BehaviorPagerAdapter _adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_events_purchase);

        _eventsListTextView = findViewById(R.id.eventsListTextView);

        _name = findViewById(R.id.nameText);
        _quantity = findViewById(R.id.qtyText);
        _amount = findViewById(R.id.amountText);
        _store = findViewById(R.id.storeText);

        _tabs = findViewById(R.id.tabs);
        _pager = findViewById(R.id.pager);

        _adapter = new BehaviorPagerAdapter(getSupportFragmentManager());
        _pager.setAdapter(_adapter);
        _tabs.setupWithViewPager(_pager);
    }

    @Override
    protected void onResume() {
        super.onResume();

        _eventManager.setListener(_listener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        _eventManager.setListener(null);
    }

    private EventsListener _listener = new EventsListener() {
        public BehaviorList _behaviorList;

        @Override
        public void onProgressFetched(ProgressFetchedResponse progressFetchedResponse) {}

        @Override
        public void onEventPosted(EventPostedResponse response) {
            _behaviorList = new BehaviorList(response);
            _adapter.setBehaviors(_behaviorList);
        }

        @Override
        public void onFailure(SessionMError error) {
            Toast.makeText(PurchaseEventActivity.this, error.toString(), Toast.LENGTH_LONG).show();
        }
    };

    public void onAddItem(View view) {
        PurchaseEventItemBuilder builder = new PurchaseEventItemBuilder();
        boolean add = false;

        if (_name.getText().length() > 0) {
            builder.name(String.valueOf(_name.getText()));
            add = true;
        }
        if ((_quantity.getText().length() > 0) && (Integer.parseInt(String.valueOf(_quantity.getText())) > 0)) {
            try {
            builder.quantity(Integer.parseInt(_quantity.getText().toString()));
            add = true;
            } catch (Throwable t) {
                Toast.makeText(PurchaseEventActivity.this, "Please enter a number for Quantity", Toast.LENGTH_LONG).show();
                return;
            }
        }
        if (_amount.getText().length() > 0) {
            try {
                Double.parseDouble(String.valueOf(_amount.getText()));
                builder.amount(Double.parseDouble(String.valueOf(_amount.getText())) * 100);
                add = true;
            } catch (Throwable t) {
                Toast.makeText(PurchaseEventActivity.this, "Please enter a number for Amount", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (_store.getText().length() > 0) {
            builder.store(String.valueOf(_store.getText()));
            add = true;
        }

        if (add) {
            _purchaseBuilder.addItemBuilder(builder);
            for (EventItem item : _purchaseBuilder.getItems()) {
                PurchaseEventItem pi = (PurchaseEventItem) item;
                _eventsListTextView.setText(String.format("%s\nItem: %s\t%s\t%s\t%s", _eventsListTextView.getText(), pi.getName(), pi.getQuantity(), pi.getAmount(), pi.getStore()).replace("null", ""));
            }
        }
    }

    public void onPostEvent(View v) {

        Log.d("TAG", String.valueOf(_purchaseBuilder));

        //
        // Post a Purchase Event
        //

        _eventManager.postEvent(_purchaseBuilder, EventsManager.WhenToSend.ASAP);
    }

}