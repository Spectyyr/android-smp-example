package com.sessionm.mmc.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.SessionMError;
import com.sessionm.api.loyaltycard.LoyaltyCardsListener;
import com.sessionm.api.loyaltycard.LoyaltyCardsManager;
import com.sessionm.api.loyaltycard.data.LoyaltyCard;
import com.sessionm.api.loyaltycard.data.Retailer;
import com.sessionm.mmc.R;
import com.sessionm.mmc.controller.RetailerListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pmattheis on 3/22/16.
 */
public class LoyaltyActivity extends AppCompatActivity {

    private static final String TAG = "SessionM.LoyalActiv";

    private ListView _listView;
    private LoyaltyCardsManager _loyaltyManager;
    private List<Retailer> _retailers;
    private RetailerListAdapter _listAdapter;
    private Retailer _pickedRetailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loyalty_card);

        _listView = (ListView) findViewById(R.id.retailer_list);
        _loyaltyManager = new LoyaltyCardsManager();
        _retailers = _loyaltyManager.getRetailers();
        if (_retailers == null) {
            _retailers = new ArrayList<>();
        }
        _listAdapter = new RetailerListAdapter(this, _retailers);
        _listView.setAdapter(_listAdapter);

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _pickedRetailer = (Retailer)_listAdapter.getItem(position);
                TextView rt = (TextView)findViewById(R.id.retailer_text);
                rt.setText(_pickedRetailer.getCard());
                return;
            }
        });

        final EditText et = (EditText)findViewById(R.id.card_number);
        Button link = (Button) findViewById(R.id.link);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_pickedRetailer == null) {
                    Toast.makeText(LoyaltyActivity.this, "Enter the Retailer!", Toast.LENGTH_LONG).show();
                    return;
                }
                String cardNumber = et.getText().toString();
                if ((cardNumber == null) || (cardNumber.length() <= 0)) {
                    Toast.makeText(LoyaltyActivity.this, "Enter the Card Number!", Toast.LENGTH_LONG).show();
                    return;
                }
                linkCard(_pickedRetailer.getId(), et.getText().toString());
            }
        });
        et.setText("MK95467086294");
    }

    private void linkCard(String retailerId, String cardNumber) {
        _loyaltyManager.linkLoyaltyCard(retailerId, cardNumber);
    }

    @Override
    protected void onPause() {
        super.onPause();
        _loyaltyManager.setListener(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _loyaltyManager.setListener(_loyaltyListener);
        _loyaltyManager.fetchRetailers();
    }

    private LoyaltyCardsListener _loyaltyListener = new LoyaltyCardsListener() {
        @Override
        public void onRetailersFetched(List<Retailer> retailers) {
            _retailers.clear();
            if (retailers == null) {
                retailers = new ArrayList<>();
            }
            _retailers.addAll(retailers);
            if (_listAdapter == null) {
                _listAdapter = new RetailerListAdapter(LoyaltyActivity.this, _retailers);
                _listView.setAdapter(_listAdapter);
            }
            _listAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoyaltyCardLinked(String cardNumber) {
            Log.d(TAG, String.format("Card: %s", cardNumber));
        }

        @Override
        public void onLoyaltyCardUnlinked() {
            Log.d(TAG, String.format("Card Unlinked"));
        }

        @Override public void onLoyaltyCardsFetched(List<LoyaltyCard>cards) { }

        @Override
        public void onFailure(SessionMError error) {
            Toast.makeText(LoyaltyActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
}
