package com.sessionm.smp_kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

class DeepLinkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deep_link)

        val url = intent.getStringExtra("url")

        val textView = findViewById<TextView>(R.id.deep_link_textview) as TextView
        textView.text = url
    }
}
