/*
 * Copyright (c) 2018 SessionM. All rights reserved.
 */

package com.sessionm.smp_loyalty_card;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.core.api.SessionMError;
import com.sessionm.loyaltycard.api.LoyaltyCardsManager;
import com.sessionm.loyaltycard.api.data.Retailer;

import java.util.ArrayList;
import java.util.List;

public class LoyaltyCardActivity extends AppCompatActivity {

    private static final String TAG = "SessionM.LoyalActiv";

    private ListView _listView;
    private LoyaltyCardsManager _loyaltyManager = LoyaltyCardsManager.getInstance();
    private List<Retailer> _retailers;
    private EditText _searchView;
    private RetailerListAdapter _listAdapter;
    private Retailer _pickedRetailer;
    private ProgressDialog _progressDialog;
    private ImageButton _clearSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loyalty_card);

        _searchView = findViewById(R.id.search);
        _listView = findViewById(R.id.retailer_list);
        _clearSearch = findViewById(R.id.clear_search);
        _retailers = new ArrayList<>(_loyaltyManager.getRetailers());
        _listAdapter = new RetailerListAdapter(this, _retailers);
        _listView.setAdapter(_listAdapter);

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _pickedRetailer = (Retailer) _listAdapter.getItem(position);
                TextView rt = findViewById(R.id.retailer_text);
                rt.setText(_pickedRetailer.getCard());
                return;
            }
        });

        _clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _searchView.setText("");
            }
        });

        _searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                _listAdapter.filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final EditText et = findViewById(R.id.card_number);
        Button link = findViewById(R.id.link);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_pickedRetailer == null) {
                    Toast.makeText(LoyaltyCardActivity.this, "Please choose a Retailer!", Toast.LENGTH_LONG).show();
                    return;
                }
                String cardNumber = et.getText().toString();
                if ((cardNumber.isEmpty())) {
                    Toast.makeText(LoyaltyCardActivity.this, "Please enter the Card Number!", Toast.LENGTH_LONG).show();
                    return;
                }
                linkCard(et.getText().toString(), _pickedRetailer.getId());
                _progressDialog.setTitle("Linking Card...");
                _progressDialog.show();
            }
        });

        _progressDialog = new ProgressDialog(this);
    }

    private void linkCard(String cardNumber, String retailerId) {
        _loyaltyManager.linkLoyaltyCard(cardNumber, retailerId, new LoyaltyCardsManager.OnLoyaltyCardLinkedListener() {
            @Override
            public void onLinked(String s, SessionMError sessionMError) {
                _progressDialog.dismiss();
                if (sessionMError != null) {
                    Toast.makeText(LoyaltyCardActivity.this, sessionMError.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, String.format("Card: %s", s));
                    Toast.makeText(LoyaltyCardActivity.this, String.format("Linked Card: %s", s), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        _loyaltyManager.fetchRetailers(new LoyaltyCardsManager.OnRetailersFetchedListener() {
            @Override
            public void onFetched(List<Retailer> retailers, SessionMError sessionMError) {
                _progressDialog.dismiss();
                if (sessionMError != null) {
                    Toast.makeText(LoyaltyCardActivity.this, sessionMError.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    _retailers.clear();
                    if (retailers == null) {
                        retailers = new ArrayList<>();
                    }
                    _retailers.addAll(retailers);
                    if (_listAdapter == null) {
                        _listAdapter = new RetailerListAdapter(LoyaltyCardActivity.this, _retailers);
                        _listView.setAdapter(_listAdapter);
                    } else {
                        _listAdapter.setRetailers(_retailers);
                    }
                    _listAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
