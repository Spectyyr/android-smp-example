package com.sessionm.smp_kotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView
import android.widget.Toast
import com.sessionm.campaign.api.data.FeedMessage
import com.sessionm.core.api.SessionM
import com.sessionm.identity.api.UserManager
import com.sessionm.identity.api.provider.SessionMOauthEmailProvider
import com.sessionm.identity.api.provider.SessionMOauthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CampaignsFragment.OnDeepLinkTappedListener {
    private var userBalanceTextView: TextView? = null
    private var _sessionMOauthEmailProvider: SessionMOauthEmailProvider? = null
    private var _userManager: UserManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar = findViewById<Toolbar>(R.id.custom_action_bar)
        setSupportActionBar(actionBar)

        _sessionMOauthEmailProvider = SessionMOauthEmailProvider()
        SessionM.setAuthenticationProvider(_sessionMOauthEmailProvider) { }
        _userManager = UserManager.getInstance()

        userBalanceTextView = findViewById(R.id.user_balance_textview)
        userBalanceTextView!!.setOnClickListener {
            if (UserManager.getInstance().currentUser == null)
                _sessionMOauthEmailProvider!!.authenticateUser("test@sessionm.com", "aaaaaaaa1") { authenticatedState, sessionMError ->
                    if (sessionMError != null) {
                        Toast.makeText(this@MainActivity, sessionMError.message, Toast.LENGTH_SHORT).show()
                    } else {
                        _userManager!!.fetchUser { smpUser, set, sessionMError ->
                            if (sessionMError != null) {
                                Toast.makeText(this@MainActivity, sessionMError.message, Toast.LENGTH_SHORT).show()
                            } else {
                                if (smpUser != null) {
                                    userBalanceTextView!!.text = smpUser.availablePoints.toString() + "pts"
                                } else
                                    userBalanceTextView!!.text = getString(R.string.click_here_to_log_in_user)
                            }
                        }
                    }
                }
            else
                _sessionMOauthEmailProvider!!.logoutUser { authenticatedState, sessionMError ->
                    if (authenticatedState == SessionMOauthProvider.AuthenticatedState.NotAuthenticated)
                        userBalanceTextView!!.text = getString(R.string.click_here_to_log_in_user)
                }
        }
    }

    override fun onDeepLinkTapped(actionType: FeedMessage.MessageActionType, actionURL: String) {
        val uri = Uri.parse(actionURL)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.putExtra("url", actionURL)
        startActivity(intent)
    }
}
