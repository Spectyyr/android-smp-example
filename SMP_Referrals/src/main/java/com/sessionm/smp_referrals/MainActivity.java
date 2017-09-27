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

import com.sessionm.api.SessionM;
import com.sessionm.api.SessionMError;
import com.sessionm.api.identity.IdentityManager;
import com.sessionm.api.identity.UserListener;
import com.sessionm.api.identity.UserManager;
import com.sessionm.api.identity.data.SMPUser;
import com.sessionm.api.referral.ReferralsManager;
import com.sessionm.api.referral.data.Referral;
import com.sessionm.api.referral.data.ReferralRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA5LTI3IDE1OjMwOjU1ICswMDAwIiwiZXhwIjoiMjAxNy0xMC0xMSAxNTozMDo1NSArMDAwMCIsImRhdGEiOnsiaWQiOiJkYTYxZGNkYS1hMzk4LTExZTctODcxZi05ZjZkNTQzYmUwNDAifX0.iBrHv9-INszE-SSL9rsuNnLDv7DBBaIUuqM6XDUvecxzap2CuoN4v3juXPvw-dZWuzbiHY2H3TPJJlRcI5_fZPxH2FjDqGA1S5nwEwEYVn9D1oMvnXUB6jLIq3ev4omE7ZUj5zVytsn_rKdryllfHro_8g5TneiOUoFBa_1N_RcC9AK_8640xbYPtZaNWhxsJiCwTsKWaLSYQ6RQv_xo1M4reL56dbjJ16Y-50HUy6Pxax6biKVvpjNRDizrkY0bka07lHMLAHMZD5-D3OYnxpxyg9aVX2kJd36iZuwsKaXVMtrCzwmzzGuhQD1PUUhC43wkNUbYw9z2d94v0FDxvQ";

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
                if (UserManager.getInstance().getCurrentUser() == null)
                    IdentityManager.getInstance().authenticateCoalitionWithToken(SAMPLE_USER_TOKEN);
                else
                    IdentityManager.getInstance().logOutUser();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserManager.getInstance().setListener(_userListener);
    }

    UserListener _userListener = new UserListener() {
        @Override
        public void onUserUpdated(SMPUser smpUser, Set<String> set) {
            if (smpUser != null) {
                userBalanceTextView.setText(smpUser.getAvailablePoints() + "pts");
            } else
                userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
            ReferralsManager.getInstance().fetchReferrals();
        }

        @Override
        public void onFailure(SessionMError sessionMError) {

        }
    };

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
