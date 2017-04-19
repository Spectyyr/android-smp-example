package com.sessionm.smp_auth.webauth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sessionm.api.SessionMError;
import com.sessionm.api.identity.IdentityListener;
import com.sessionm.api.identity.IdentityManager;
import com.sessionm.api.identity.UserListener;
import com.sessionm.api.identity.UserManager;
import com.sessionm.api.identity.data.SMPUser;
import com.sessionm.smp_auth.R;
import com.sessionm.smp_auth.UserDetailsActivity;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Displays the authorized state of the user. This activity is provided with the outcome of the
 * authorization flow, which it uses to negotiate the final authorized state,
 * by performing an authorization code exchange if necessary. After this, the activity provides
 * additional post-authorization operations if available, such as fetching user info and refreshing
 * access tokens.
 */
public class TokenActivity extends AppCompatActivity {
    private static final String TAG = "TokenActivity";

    private static final String KEY_USER_INFO = "userInfo";

    private AuthorizationService mAuthService;
    private AuthStateManager mStateManager;
    private final AtomicReference<JSONObject> mUserInfoJson = new AtomicReference<>();
    private ExecutorService mExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStateManager = AuthStateManager.getInstance(this);
        mExecutor = Executors.newSingleThreadExecutor();

        Configuration config = Configuration.getInstance(this);
        if (config.hasConfigurationChanged()) {
            Toast.makeText(
                    this,
                    "Configuration change detected",
                    Toast.LENGTH_SHORT)
                    .show();
            signOut();
            return;
        }

        mAuthService = new AuthorizationService(
                this,
                new AppAuthConfiguration.Builder()
                        .setConnectionBuilder(config.getConnectionBuilder())
                        .build());

        setContentView(R.layout.activity_token);
        displayLoading("Restoring state...");

        if (savedInstanceState != null) {
            try {
                mUserInfoJson.set(new JSONObject(savedInstanceState.getString(KEY_USER_INFO)));
            } catch (JSONException ex) {
                Log.e(TAG, "Failed to parse saved user info JSON, discarding", ex);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        IdentityManager.getInstance().setListener(identityListener);
        UserManager.getInstance().setListener(userListener);

        if (mExecutor.isShutdown()) {
            mExecutor = Executors.newSingleThreadExecutor();
        }

        if (mStateManager.getCurrent().isAuthorized()) {
            displayAuthorized();
            return;
        }

        // the stored AuthState is incomplete, so check if we are currently receiving the result of
        // the authorization flow from the browser.
        AuthorizationResponse response = AuthorizationResponse.fromIntent(getIntent());
        AuthorizationException ex = AuthorizationException.fromIntent(getIntent());

        if (response != null || ex != null) {
            mStateManager.updateAfterAuthorization(response, ex);
        }

        displayAuthorized();
//        if (response != null && response.authorizationCode != null) {
//            // authorization code exchange is required
//            mStateManager.updateAfterAuthorization(response, ex);
//            exchangeAuthorizationCode(response);
//        } else if (ex != null) {
//            displayNotAuthorized("Authorization flow failed: " + ex.getMessage());
//        } else {
//            displayNotAuthorized("No authorization state retained - reauthorization required");
//        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        // user info is retained to survive activity restarts, such as when rotating the
        // device or switching apps. This isn't essential, but it helps provide a less
        // jarring UX when these events occur - data does not just disappear from the view.
        if (mUserInfoJson.get() != null) {
            state.putString(KEY_USER_INFO, mUserInfoJson.toString());
        }
    }

    UserListener userListener = new UserListener() {
        @Override
        public void onUserUpdated(SMPUser smpUser, Set<String> set) {
            findViewById(R.id.loading_container).setVisibility(View.GONE);
            if (smpUser != null)
                startActivity(new Intent(TokenActivity.this, UserDetailsActivity.class));
        }

        @Override
        public void onFailure(SessionMError sessionMError) {
            findViewById(R.id.loading_container).setVisibility(View.GONE);
            Toast.makeText(TokenActivity.this, "Failed! " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    IdentityListener identityListener = new IdentityListener() {
        @Override
        public void onAuthStateUpdated(IdentityManager.AuthState authState) {
            if (authState.equals(IdentityManager.AuthState.Authenticated)) {
                Toast.makeText(TokenActivity.this, "Authenticated! " + authState.toString(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TokenActivity.this, "Failed! " + authState.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(SessionMError sessionMError) {
            findViewById(R.id.loading_container).setVisibility(View.GONE);
            Toast.makeText(TokenActivity.this, "Failed! " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuthService.dispose();
        mExecutor.shutdownNow();
    }

    @MainThread
    private void displayNotAuthorized(String explanation) {
        findViewById(R.id.not_authorized).setVisibility(View.VISIBLE);
        findViewById(R.id.authorized).setVisibility(View.GONE);
        findViewById(R.id.loading_container).setVisibility(View.GONE);

        ((TextView) findViewById(R.id.explanation)).setText(explanation);
        findViewById(R.id.reauth).setOnClickListener((View view) -> signOut());
    }

    @MainThread
    private void displayLoading(String message) {
        findViewById(R.id.loading_container).setVisibility(View.VISIBLE);
        findViewById(R.id.authorized).setVisibility(View.GONE);
        findViewById(R.id.not_authorized).setVisibility(View.GONE);

        ((TextView) findViewById(R.id.loading_description)).setText(message);
    }

    @MainThread
    private void displayAuthorized() {
        findViewById(R.id.authorized).setVisibility(View.VISIBLE);
        findViewById(R.id.not_authorized).setVisibility(View.GONE);
        findViewById(R.id.loading_container).setVisibility(View.GONE);

        AuthState state = mStateManager.getCurrent();

        TextView refreshTokenInfoView = (TextView) findViewById(R.id.refresh_token_info);
        refreshTokenInfoView.setText((state.getRefreshToken() == null)
                ? R.string.no_refresh_token_returned
                : R.string.refresh_token_returned);

        TextView idTokenInfoView = (TextView) findViewById(R.id.id_token_info);
        idTokenInfoView.setText((state.getIdToken()) == null
                ? R.string.no_id_token_returned
                : R.string.id_token_returned);

        TextView accessTokenInfoView = (TextView) findViewById(R.id.access_token_info);
        if (state.getAccessToken() == null) {
            accessTokenInfoView.setText(R.string.no_access_token_returned);
        } else {
            Long expiresAt = state.getAccessTokenExpirationTime();
            if (expiresAt == null) {
                accessTokenInfoView.setText(R.string.no_access_token_expiry);
            } else if (expiresAt < System.currentTimeMillis()) {
                accessTokenInfoView.setText(R.string.access_token_expired);
            } else {
                String template = getResources().getString(R.string.access_token_expires_at);
                accessTokenInfoView.setText(String.format(template,
                        DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss ZZ").print(expiresAt)));
            }
        }

        Button refreshTokenButton = (Button) findViewById(R.id.refresh_token);
        refreshTokenButton.setVisibility(state.getRefreshToken() != null
                ? View.VISIBLE
                : View.GONE);
        refreshTokenButton.setOnClickListener((View view) -> refreshAccessToken());

        Button viewProfileButton = (Button) findViewById(R.id.view_profile);

//        AuthorizationServiceDiscovery discoveryDoc =
//                state.getAuthorizationServiceConfiguration().discoveryDoc;
//        if (discoveryDoc == null || discoveryDoc.getUserinfoEndpoint() == null) {
//            viewProfileButton.setVisibility(View.GONE);
//        } else {
        viewProfileButton.setVisibility(View.VISIBLE);
        viewProfileButton.setOnClickListener((View view) -> fetchUserInfo());
//        }

        ((Button) findViewById(R.id.sign_out)).setOnClickListener((View view) -> signOut());

        View userInfoCard = findViewById(R.id.userinfo_card);
        JSONObject userInfo = mUserInfoJson.get();
        if (userInfo == null) {
            userInfoCard.setVisibility(View.INVISIBLE);
        } else {
            try {
                String name = "???";
                if (userInfo.has("name")) {
                    name = userInfo.getString("name");
                }
                ((TextView) findViewById(R.id.userinfo_name)).setText(name);

//                if (userInfo.has("picture")) {
//                    Glide.with(TokenActivity.this)
//                            .load(Uri.parse(userInfo.getString("picture")))
//                            .fitCenter()
//                            .into((ImageView) findViewById(R.id.userinfo_profile));
//                }

                ((TextView) findViewById(R.id.userinfo_json)).setText(mUserInfoJson.toString());
                userInfoCard.setVisibility(View.VISIBLE);
            } catch (JSONException ex) {
                Log.e(TAG, "Failed to read userinfo JSON", ex);
            }
        }
    }

    @MainThread
    private void refreshAccessToken() {
        displayLoading("Refreshing access token");
        performTokenRequest(
                mStateManager.getCurrent().createTokenRefreshRequest(),
                this::handleAccessTokenResponse);
    }

    @MainThread
    private void exchangeAuthorizationCode(AuthorizationResponse authorizationResponse) {
        displayLoading("Exchanging authorization code");
        performTokenRequest(
                authorizationResponse.createTokenExchangeRequest(),
                this::handleCodeExchangeResponse);
    }

    @MainThread
    private void performTokenRequest(
            TokenRequest request,
            AuthorizationService.TokenResponseCallback callback) {
        ClientAuthentication clientAuthentication;
        try {
            clientAuthentication = mStateManager.getCurrent().getClientAuthentication();
        } catch (ClientAuthentication.UnsupportedAuthenticationMethod ex) {
            Log.d(TAG, "Token request cannot be made, client authentication for the token "
                    + "endpoint could not be constructed (%s)", ex);
            displayNotAuthorized("Client authentication method is unsupported");
            return;
        }

        mAuthService.performTokenRequest(
                request,
                clientAuthentication,
                callback);
    }

    @WorkerThread
    private void handleAccessTokenResponse(
            @Nullable TokenResponse tokenResponse,
            @Nullable AuthorizationException authException) {
        mStateManager.updateAfterTokenResponse(tokenResponse, authException);
        runOnUiThread(this::displayAuthorized);
    }

    @WorkerThread
    private void handleCodeExchangeResponse(
            @Nullable TokenResponse tokenResponse,
            @Nullable AuthorizationException authException) {

        mStateManager.updateAfterTokenResponse(tokenResponse, authException);
        if (!mStateManager.getCurrent().isAuthorized()) {
            final String message = "Authorization Code exchange failed"
                    + ((authException != null) ? authException.error : "");

            // WrongThread inference is incorrect for lambdas
            //noinspection WrongThread
            runOnUiThread(() -> displayNotAuthorized(message));
        } else {
            runOnUiThread(this::displayAuthorized);
        }
    }

    /**
     * Demonstrates the use of {@link AuthState#performActionWithFreshTokens} to retrieve
     * user info from the IDP's user info endpoint. This callback will negotiate a new access
     * token / id token for use in a follow-up action, or provide an error if this fails.
     */
    @MainThread
    private void fetchUserInfo() {
        AuthState state = mStateManager.getCurrent();
        IdentityManager.getInstance().authenticateWithToken(state.getAccessToken());
        findViewById(R.id.loading_container).setVisibility(View.VISIBLE);
//        displayLoading("Fetching user info");
//        mStateManager.getCurrent().performActionWithFreshTokens(mAuthService, this::fetchUserInfo);
    }

    @MainThread
    private void signOut() {
        // discard the authorization and token state, but retain the configuration and
        // dynamic client registration (if applicable), to save from retrieving them again.
        AuthState currentState = mStateManager.getCurrent();
        AuthState clearedState =
                new AuthState(currentState.getAuthorizationServiceConfiguration());
        if (currentState.getLastRegistrationResponse() != null) {
            clearedState.update(currentState.getLastRegistrationResponse());
        }
        mStateManager.replace(clearedState);

        IdentityManager.getInstance().logOutUser();

        Intent mainIntent = new Intent(this, WebAuthActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }
}
