package com.sessionm.mmc.view;

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

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.reward.RewardsListener;
import com.sessionm.api.reward.RewardsManager;
import com.sessionm.api.reward.data.offer.Offer;
import com.sessionm.api.reward.data.order.Address;
import com.sessionm.api.reward.data.order.Order;
import com.sessionm.api.reward.data.order.OrderRequest;
import com.sessionm.api.reward.data.skill.SkillChallenge;
import com.sessionm.api.reward.data.skill.SkillQuestion;
import com.sessionm.mmc.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class OfferDetailsActivity extends AppCompatActivity {

    private List<Offer> offers;
    private Offer currentOffer;
    private Button placeOrderButton;
    private RewardsManager rewardsManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);

        Intent getOfferIntent = getIntent();
        ImageView imageView = (ImageView) findViewById(R.id.offer_detail_image);
        TextView header = (TextView) findViewById(R.id.offer_detail_header);
        WebView subheader = (WebView) findViewById(R.id.offer_detail_subheader);
        TextView desc = (TextView) findViewById(R.id.offer_detail_description);
        placeOrderButton = (Button) findViewById(R.id.place_order_button);
        progressDialog = new ProgressDialog(this);

        rewardsManager = SessionM.getInstance().getRewardsManager();
        offers = rewardsManager.getOffers();
        final String offerID = getOfferIntent.getStringExtra("offer_id");
        for (int i = 0; i < offers.size(); i++) {
            if (offers.get(i).getId().equals(offerID)) {
                currentOffer = offers.get(i);
            }
        }

        if (currentOffer == null) {
            Toast.makeText(this, "Expired!", Toast.LENGTH_SHORT).show();
            finish();
        } else {

            if (currentOffer.getLogo() != null && !currentOffer.getLogo().equals("null")) {
                Picasso.with(this).load(currentOffer.getLogo()).into(imageView);
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
            }

            header.setText(currentOffer.getName());

            Map<String, Object> data = currentOffer.getData();
            if (data != null) {
                String description = (String) data.get("long_description");
                String shipping = (String) data.get("shipping_information");
                subheader.loadDataWithBaseURL("", description.replace("\n", "").replace("\\n", ""), "text/html", "UTF-8", "");
                desc.setText(shipping);
            } else {
                desc.setText(String.format("Status: %s, Type: %s", currentOffer.getStatus(), currentOffer.getType()));
            }
        }

        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rewardsManager.fetchSkillQuestion();
                progressDialog.show();
            }
        });
    }

    private RewardsListener _rewardsListener = new RewardsListener() {
        @Override
        public void onOffersFetched(List<Offer> list) {

        }

        @Override
        public void onOrderPlaced(Order order) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Success: " + order.getID(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onOrdersFetched(List<Order> list) {

        }

        @Override
        public void onSkillQuestionFetched(SkillQuestion skillQuestion) {
            progressDialog.dismiss();
            popUpSkillChallengeDialog(skillQuestion.getID(), skillQuestion.getQuestion());
        }

        @Override
        public void onSkillQuestionAnswered(SkillChallenge skillChallenge) {
            OrderRequest request = makeOrderRequest(skillChallenge.getID(), currentOffer.getId(), 1);
            rewardsManager.placeOrder(request);
            progressDialog.show();
        }

        @Override
        public void onFailure(SessionMError error) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        rewardsManager.setListener(_rewardsListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        rewardsManager.setListener(null);
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

    protected void popUpSkillChallengeDialog(final String questionID, String question) {

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_skill_challenge, null);
        TextView questionTextView = (TextView) dialogLayout.findViewById(R.id.skill_challenge_question);
        final EditText answerEditText = (EditText) dialogLayout.findViewById(R.id.skill_challenge_answer_edittext);
        questionTextView.setText(question);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String answer = answerEditText.getText().toString();
                if (!answer.isEmpty())
                    rewardsManager.answerSkillQuestion(currentOffer.getId(), questionID, answer);
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
