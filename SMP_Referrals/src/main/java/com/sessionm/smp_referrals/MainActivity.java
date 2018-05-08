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
import android.widget.Toast;

import com.sessionm.core.api.SessionM;
import com.sessionm.core.api.SessionMError;
import com.sessionm.core.api.provider.AuthenticationProvider;
import com.sessionm.identity.api.UserManager;
import com.sessionm.identity.api.data.SMPUser;
import com.sessionm.identity.api.provider.SessionMOauthProvider;
import com.sessionm.referral.api.ReferralsManager;
import com.sessionm.referral.api.data.Referral;
import com.sessionm.referral.api.data.ReferralError;
import com.sessionm.referral.api.data.ReferralRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA5LTI3IDE1OjMwOjU1ICswMDAwIiwiZXhwIjoiMjAxNy0xMC0xMSAxNTozMDo1NSArMDAwMCIsImRhdGEiOnsiaWQiOiJkYTYxZGNkYS1hMzk4LTExZTctODcxZi05ZjZkNTQzYmUwNDAifX0.iBrHv9-INszE-SSL9rsuNnLDv7DBBaIUuqM6XDUvecxzap2CuoN4v3juXPvw-dZWuzbiHY2H3TPJJlRcI5_fZPxH2FjDqGA1S5nwEwEYVn9D1oMvnXUB6jLIq3ev4omE7ZUj5zVytsn_rKdryllfHro_8g5TneiOUoFBa_1N_RcC9AK_8640xbYPtZaNWhxsJiCwTsKWaLSYQ6RQv_xo1M4reL56dbjJ16Y-50HUy6Pxax6biKVvpjNRDizrkY0bka07lHMLAHMZD5-D3OYnxpxyg9aVX2kJd36iZuwsKaXVMtrCzwmzzGuhQD1PUUhC43wkNUbYw9z2d94v0FDxvQ";
    private TextView userBalanceTextView;
    private SessionMOauthProvider _sessionMOauthProvider;
    private UserManager _userManager;
    private FloatingActionButton referAFriendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar actionBar = findViewById(R.id.custom_action_bar);
        setSupportActionBar(actionBar);
        referAFriendButton = findViewById(R.id.action_refer_a_friend);
        referAFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpCreateReferralDialog();
            }
        });

        _sessionMOauthProvider = new SessionMOauthProvider();
        SessionM.setAuthenticationProvider(_sessionMOauthProvider, new AuthenticationProvider.OnAuthenticationProviderSetFromAuthenticationProvider() {
            @Override
            public void onUpdated(SessionMError sessionMError) {

            }
        });
        _userManager = UserManager.getInstance();

        userBalanceTextView = findViewById(R.id.user_balance_textview);
        userBalanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserManager.getInstance().getCurrentUser() == null)
                    _sessionMOauthProvider.authenticateUser("test@sessionm.com", "aaaaaaaa1", new SessionMOauthProvider.SessionMOauthProviderListener() {
                        @Override
                        public void onAuthorize(SessionMOauthProvider.AuthenticatedState authenticatedState, SessionMError sessionMError) {
                            if (sessionMError != null) {
                                Toast.makeText(MainActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                fetchUser();
                            }
                        }
                    });
                else
                    _sessionMOauthProvider.logoutUser(new SessionMOauthProvider.SessionMOauthProviderListener() {
                        @Override
                        public void onAuthorize(SessionMOauthProvider.AuthenticatedState authenticatedState, SessionMError sessionMError) {
                            if (authenticatedState.equals(SessionMOauthProvider.AuthenticatedState.NotAuthenticated)) {
                                userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
                                refreshUI();
                            }
                        }
                    });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
        if (_sessionMOauthProvider.isAuthenticated())
            fetchUser();
    }

    private void fetchUser() {
        _userManager.fetchUser(new UserManager.OnUserFetchedListener() {
            @Override
            public void onFetched(SMPUser smpUser, Set<String> set, SessionMError sessionMError) {
                if (sessionMError != null) {
                    Toast.makeText(MainActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    if (smpUser != null) {
                        userBalanceTextView.setText(smpUser.getAvailablePoints() + "pts");
                    } else
                        userBalanceTextView.setText(getString(R.string.click_here_to_log_in_user));
                }
                refreshUI();
            }
        });
    }

    private void refreshUI() {
        ReferralsFragment f = (ReferralsFragment) getSupportFragmentManager().findFragmentById(R.id.referrals_fragment);
        f.onRefresh();
    }

    public void popUpCreateReferralDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_create_referral, null);

        final EditText refereeEdittext = dialogLayout.findViewById(R.id.dialog_referral_referee);
        final EditText emailEdittext = dialogLayout.findViewById(R.id.dialog_referral_email);
        final EditText phoneNumberEdittext = dialogLayout.findViewById(R.id.dialog_referral_phone_number);
        final EditText originEdittext = dialogLayout.findViewById(R.id.dialog_referral_origin);
        final EditText sourceEdittext = dialogLayout.findViewById(R.id.dialog_referral_source);
        final EditText clientDataEdittext = dialogLayout.findViewById(R.id.dialog_referral_client_data);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNeutralButton("Create Random", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SessionMError sessionMError = ReferralsManager.getInstance().sendReferrals(createRandomReferralRequest(), new ReferralsManager.OnReferralsSentListener() {
                    @Override
                    public void onSent(List<Referral> list, List<ReferralError> list1, SessionMError sessionMError) {
                        if (sessionMError != null) {
                            Toast.makeText(MainActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "New referral created! ", Toast.LENGTH_SHORT).show();
                            refreshUI();
                        }
                    }
                });
                if (sessionMError != null) {
                    Toast.makeText(MainActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SessionMError sessionMError = ReferralsManager.getInstance().sendReferrals(
                        createReferralRequest(refereeEdittext.getText().toString(),
                                emailEdittext.getText().toString(),
                                phoneNumberEdittext.getText().toString(),
                                originEdittext.getText().toString(),
                                sourceEdittext.getText().toString(),
                                clientDataEdittext.getText().toString()
                        ), new ReferralsManager.OnReferralsSentListener() {
                            @Override
                            public void onSent(List<Referral> list, List<ReferralError> list1, SessionMError sessionMError) {
                                if (sessionMError != null) {
                                    Toast.makeText(MainActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "New referral created! ", Toast.LENGTH_SHORT).show();
                                    refreshUI();
                                }
                            }
                        });
                if (sessionMError != null) {
                    Toast.makeText(MainActivity.this, sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                }
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
