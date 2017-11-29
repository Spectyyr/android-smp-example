package com.sessionm.smp_kotlin

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.sessionm.api.SessionMError
import com.sessionm.api.campaign.CampaignsManager
import com.sessionm.api.campaign.data.FeedMessage
import com.sessionm.api.identity.IdentityManager
import com.sessionm.api.identity.UserListener
import com.sessionm.api.identity.UserManager
import com.sessionm.api.identity.data.SMPUser

class MainActivity : AppCompatActivity(), CampaignsFragment.OnDeepLinkTappedListener {
    companion object {
        private val SAMPLE_USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOiIyMDE3LTA5LTI3IDE1OjMwOjU1ICswMDAwIiwiZXhwIjoiMjAxNy0xMC0xMSAxNTozMDo1NSArMDAwMCIsImRhdGEiOnsiaWQiOiJkYTYxZGNkYS1hMzk4LTExZTctODcxZi05ZjZkNTQzYmUwNDAifX0.iBrHv9-INszE-SSL9rsuNnLDv7DBBaIUuqM6XDUvecxzap2CuoN4v3juXPvw-dZWuzbiHY2H3TPJJlRcI5_fZPxH2FjDqGA1S5nwEwEYVn9D1oMvnXUB6jLIq3ev4omE7ZUj5zVytsn_rKdryllfHro_8g5TneiOUoFBa_1N_RcC9AK_8640xbYPtZaNWhxsJiCwTsKWaLSYQ6RQv_xo1M4reL56dbjJ16Y-50HUy6Pxax6biKVvpjNRDizrkY0bka07lHMLAHMZD5-D3OYnxpxyg9aVX2kJd36iZuwsKaXVMtrCzwmzzGuhQD1PUUhC43wkNUbYw9z2d94v0FDxvQ"
    }

    private var userBalanceTextView: TextView? = null

    private var _userListener: UserListener = object : UserListener {
        @SuppressLint("SetTextI18n")
        override fun onUserUpdated(smpUser: SMPUser?, set: Set<String>) {
            if (smpUser != null) {
                userBalanceTextView!!.text = smpUser.availablePoints.toString() + "pts"
            } else
                userBalanceTextView!!.text = getString(R.string.click_here_to_log_in_user)
            CampaignsManager.getInstance().fetchFeedMessages()
        }

        override fun onFailure(sessionMError: SessionMError) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar = findViewById<Toolbar>(R.id.custom_action_bar) as Toolbar
        setSupportActionBar(actionBar)

        userBalanceTextView = findViewById<TextView>(R.id.user_balance_textview) as TextView
        userBalanceTextView!!.setOnClickListener {
            if (UserManager.getInstance().currentUser == null)
                IdentityManager.getInstance().authenticateCoalitionWithToken(SAMPLE_USER_TOKEN)
            else
                IdentityManager.getInstance().logOutUser()
        }
    }

    override fun onResume() {
        super.onResume()
        UserManager.getInstance().listener = _userListener
    }

    override fun onDeepLinkTapped(actionType: FeedMessage.MessageActionType, actionURL: String) {
        val uri = Uri.parse(actionURL)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.putExtra("url", actionURL)
        startActivity(intent)
    }
}
