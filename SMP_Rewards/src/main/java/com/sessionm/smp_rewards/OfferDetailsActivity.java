/*
 * Copyright (c) 2018 SessionM. All rights reserved.
 */

package com.sessionm.smp_rewards;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.sessionm.core.api.SessionMError;
import com.sessionm.identity.api.data.SMSVerification;
import com.sessionm.identity.api.sms.SMSVerificationManager;
import com.sessionm.reward.api.RewardsManager;
import com.sessionm.reward.api.data.offer.Offer;
import com.sessionm.reward.api.data.order.Address;
import com.sessionm.reward.api.data.order.Order;
import com.sessionm.reward.api.data.order.OrderRequest;
import com.sessionm.reward.api.data.skill.SkillChallenge;
import com.sessionm.reward.api.data.skill.SkillQuestion;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OfferDetailsActivity extends AppCompatActivity {

    private Offer _currentOffer;
    private RewardsManager _rewardsManager;
    private ProgressDialog _progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);

        Intent getOfferIntent = getIntent();
        ImageView imageView = findViewById(R.id.offer_detail_image);
        TextView header = findViewById(R.id.offer_detail_header);
        WebView subheader = findViewById(R.id.offer_detail_subheader);
        TextView desc = findViewById(R.id.offer_detail_description);
        Button placeOrderButton = findViewById(R.id.place_order_button);
        _progressDialog = new ProgressDialog(this);

        _rewardsManager = RewardsManager.getInstance();
        List<Offer> offers = new ArrayList<>(_rewardsManager.getOffers());
        final String offerID = getOfferIntent.getStringExtra("offer_id");
        for (int i = 0; i < offers.size(); i++) {
            if (offers.get(i).getID().equals(offerID)) {
                _currentOffer = offers.get(i);
            }
        }

        if (_currentOffer == null) {
            Toast.makeText(this, "Expired!", Toast.LENGTH_SHORT).show();
            finish();
        } else {

            if (_currentOffer.getLogoURL() != null && !_currentOffer.getLogoURL().equals("null")) {
                Picasso.with(this).load(_currentOffer.getLogoURL()).into(imageView);
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
            }

            header.setText(_currentOffer.getName());

            Map<String, Object> data = _currentOffer.getData();
            if (data != null) {
                String description = (String) data.get("long_description");
                String shipping = (String) data.get("shipping_information");
                if (description != null)
                    subheader.loadDataWithBaseURL("", description.replace("\n", "").replace("\\n", ""), "text/html", "UTF-8", "");
                desc.setText(shipping);
            } else {
                desc.setText(String.format("Status: %s, Type: %s", _currentOffer.getStatus(), _currentOffer.getType()));
            }
        }

        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SMSVerificationManager.getInstance().fetchSMSVerification(new SMSVerificationManager.OnSMSVerificationFetchedListener() {
                    @Override
                    public void onFetched(SMSVerification smsVerification, SessionMError sessionMError) {
                        _progressDialog.dismiss();
                        if (sessionMError != null)
                            Toast.makeText(OfferDetailsActivity.this, "Failed: " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                        else {
                            if (smsVerification.isValid()) {
                                if (_currentOffer.isSkillChallengeRequired()) {
                                    fetchSkillQuestion();
                                } else {
                                    OrderRequest request = makeOrderRequest(null, _currentOffer.getID(), 1);
                                    placeOrder(request);
                                }
                                _progressDialog.show();
                            } else {
                                popUpSMSVerificationDialog("send_code");
                            }
                        }
                    }
                });
                _progressDialog.show();
            }
        });
    }

    private OrderRequest makeOrderRequest(String challengeID, String offerID, int quantity) {
        Address address = RewardsManager.addressBuilder()
                .addressee("Mr. Tim O'Reilly")
                .street1("215 Hanover St.")
                .street2("Suite 100")
                .city("Boston")
                .state_province("MA")
                .country("US")
                .postal_code("01752")
                .build();

        return RewardsManager.orderRequestBuilder(offerID, quantity)
                .email("")
                .ip("127.0.0.1")
                .address(address)
                .challengeID(challengeID)
                .build();
    }

    private void placeOrder(OrderRequest request) {
        _rewardsManager.placeOrder(request, new RewardsManager.OnOrderPlacedListener() {
            @Override
            public void onOrderPlaced(Order order, SessionMError sessionMError) {
                _progressDialog.dismiss();
                if (sessionMError != null) {
                    Toast.makeText(OfferDetailsActivity.this, "Failed: " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Success: " + order.getID(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchSkillQuestion() {
        _rewardsManager.fetchSkillQuestion(new RewardsManager.OnSkillQuestionFetchedListener() {
            @Override
            public void onQuestionFetched(SkillQuestion skillQuestion, SessionMError sessionMError) {
                _progressDialog.dismiss();
                if (sessionMError != null) {
                    Toast.makeText(OfferDetailsActivity.this, "Failed: " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    popUpSkillChallengeDialog(skillQuestion.getID(), skillQuestion.getQuestion());
                }
            }
        });
    }

    protected void popUpSMSVerificationDialog(final String type) {

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_sms_validation, null);
        TextView descriptionTextView = dialogLayout.findViewById(R.id.sms_validation_description);
        final EditText inputEditText = dialogLayout.findViewById(R.id.sms_validation_edittext);

        if (type.equals("send_code")) {
            descriptionTextView.setText("Please enter your phone number.");
            inputEditText.setHint("Phone Number");
        } else if (type.equals("verify_code")) {
            descriptionTextView.setText("Please enter your verification code.");
            inputEditText.setHint("Code");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (type.equals("send_code")) {
                    String phone = inputEditText.getText().toString();
                    SMSVerificationManager.getInstance().sendSMSVerificationMessage(phone, new SMSVerificationManager.OnSMSVerificationMessageSentListener() {
                        @Override
                        public void onMessageSent(SMSVerification smsVerification, SessionMError sessionMError) {
                            _progressDialog.dismiss();
                            if (sessionMError != null)
                                Toast.makeText(OfferDetailsActivity.this, "Failed: " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                            else {
                                popUpSMSVerificationDialog("verify_code");
                            }
                        }
                    });
                } else if (type.equals("verify_code")) {
                    String code = inputEditText.getText().toString();
                    SMSVerificationManager.getInstance().checkSMSVerificationCode(code, new SMSVerificationManager.OnSMSVerificationCodeCheckedListener() {
                        @Override
                        public void onCodeChecked(SMSVerification smsVerification, SessionMError sessionMError) {
                            if (sessionMError != null)
                                Toast.makeText(OfferDetailsActivity.this, "Failed: " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                            else {
                                Toast.makeText(OfferDetailsActivity.this, smsVerification.toString(), Toast.LENGTH_SHORT).show();
                                if (_currentOffer.isSkillChallengeRequired()) {
                                    fetchSkillQuestion();
                                } else {
                                    OrderRequest request = makeOrderRequest(null, _currentOffer.getID(), 1);
                                    placeOrder(request);
                                }
                                _progressDialog.show();
                            }
                        }
                    });
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setView(dialogLayout);
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    protected void popUpSkillChallengeDialog(final String questionID, String question) {

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_skill_challenge, null);
        TextView questionTextView = dialogLayout.findViewById(R.id.skill_challenge_question);
        final EditText answerEditText = dialogLayout.findViewById(R.id.skill_challenge_answer_edittext);
        questionTextView.setText(question);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String answer = answerEditText.getText().toString();
                if (!answer.isEmpty())
                    _rewardsManager.answerSkillQuestion(_currentOffer.getID(), questionID, answer, new RewardsManager.OnSkillQuestionAnsweredListener() {
                        @Override
                        public void onAnswered(SkillChallenge skillChallenge, SessionMError sessionMError) {
                            if (sessionMError != null)
                                Toast.makeText(OfferDetailsActivity.this, "Failed: " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                            else {
                                OrderRequest request = makeOrderRequest(skillChallenge.getID(), _currentOffer.getID(), 1);
                                placeOrder(request);
                                _progressDialog.show();
                            }
                        }
                    });
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setView(dialogLayout);
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }
}
