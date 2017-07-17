package com.sessionm.smp_referrals;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sessionm.api.AchievementData;
import com.sessionm.api.SessionListener;
import com.sessionm.api.SessionM;
import com.sessionm.api.User;
import com.sessionm.api.identity.IdentityManager;
import com.sessionm.api.referral.ReferralsManager;
import com.sessionm.api.referral.data.Referral;
import com.sessionm.api.referral.data.ReferralRequest;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SessionListener {

    private static final String SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA3LTE0IDE4OjM4OjIwICswMDAwIiwiZXhwIjoiMjAxNy0wNy0yOCAxODozODoyMCArMDAwMCJ9.wXLHwQYWtfXA4_Kn4mBrdPXFsMvrCdHaLr4GK67CoPUx3jDwKXX4Wg0HPDjY5RFPzLdOAZGnPXhSna0rVkIkxEzEi0I6gzx_6CggUluxMJnDMUW5HHG0yo040e6tgqIl99VAZZZFbIwCF7qiDnIH01H7IdZz8e0uokq2TaHTKLoo16sUJCJIgSNfOkaRfS9uvlcwFftdH-wqZl5KZ3kUqscAW0lqEVcLdxUaA76Oc0bUFEuvpIRX7iWzAM-nIZcLPCCpRqtqaN3LnuorMxytcgYNUmec6F5228wK7X1mN3C8NbMD24SHRQnVtV4hsTNzycA23CnlwjZJhiye4n7FqQ";

    private TextView userBalanceTextView;
    private FloatingActionButton referAFriendButton;

    private SessionM sessionM = SessionM.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar actionBar = (Toolbar) findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);


        referAFriendButton = (FloatingActionButton) findViewById(R.id.action_refer_a_friend);
        referAFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpCreateReferralDialog();
            }
        });

        userBalanceTextView = (TextView) findViewById(R.id.user_balance_textview);
        userBalanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sessionM.getUser().isRegistered())
                    IdentityManager.getInstance().authenticateCoalitionWithToken(SAMPLE_USER_TOKEN);
                else
                    IdentityManager.getInstance().logOutUser();
            }
        });
    }

    @Override
    public void onSessionStateChanged(SessionM sessionM, SessionM.State state) {

    }

    @Override
    public void onSessionFailed(SessionM sessionM, int i) {

    }

    @Override
    public void onUserUpdated(SessionM sessionM, User user) {
        if (user.isRegistered())
            userBalanceTextView.setText(user.getPointBalance() + "pts");
        else
            userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
        sessionM.getReferralsManager().fetchReferrals();
    }

    @Override
    public void onUnclaimedAchievement(SessionM sessionM, AchievementData achievementData) {

    }

    public void popUpCreateReferralDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_create_referral, null);

        final EditText refereeEdittext = (EditText) dialogLayout.findViewById(R.id.dialog_referral_referee);
        final EditText emailEdittext = (EditText) dialogLayout.findViewById(R.id.dialog_referral_email);
        final EditText phoneNumberEdittext = (EditText) dialogLayout.findViewById(R.id.dialog_referral_phone_number);
        final EditText originEdittext = (EditText) dialogLayout.findViewById(R.id.dialog_referral_origin);
        final EditText sourceEdittext = (EditText) dialogLayout.findViewById(R.id.dialog_referral_source);
        final EditText clientDataEdittext = (EditText) dialogLayout.findViewById(R.id.dialog_referral_client_data);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNeutralButton("Create Random", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SessionM.getInstance().getReferralsManager().sendReferrals(createRandomReferralRequest());
            }
        });
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SessionM.getInstance().getReferralsManager().sendReferrals(
                        createReferralRequest(refereeEdittext.getText().toString(),
                                emailEdittext.getText().toString(),
                                phoneNumberEdittext.getText().toString(),
                                originEdittext.getText().toString(),
                                sourceEdittext.getText().toString(),
                                clientDataEdittext.getText().toString()
                        ));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setTitle("Refer A Friend");

        AlertDialog dialog = builder.create();

        dialog.setView(dialogLayout);
        dialog.show();
    }


    private ReferralRequest createRandomReferralRequest() {
        Referral referral1 = ReferralsManager.referralBuilder().referee("" + System.currentTimeMillis()).email(System.currentTimeMillis() + "2@test.com")
                .phoneNumber("5081111111").origin("hello").clientData("test").source("ete").build();
        Referral referral2 = ReferralsManager.referralBuilder().referee("" + System.currentTimeMillis() + 1).email(System.currentTimeMillis() + "@test.com")
                .phoneNumber("6171111111").origin("hi").clientData("sss").source("eve").build();
        List<Referral> referrals = new ArrayList<>();
        referrals.add(referral1);
        referrals.add(referral2);
        return ReferralsManager.referralRequestBuilder().referrer("randomUser").referrals(referrals).build();
    }

    private ReferralRequest createReferralRequest(String referee, String email, String phoneNumber, String origin, String source, String clientData) {
        Referral referral1 = ReferralsManager.referralBuilder().referee(referee).email(email)
                .phoneNumber(phoneNumber).origin(origin).clientData(clientData).source(source).build();
        List<Referral> referrals = new ArrayList<>();
        referrals.add(referral1);
        return ReferralsManager.referralRequestBuilder().referrer("currentUser").referrals(referrals).build();
    }
}
