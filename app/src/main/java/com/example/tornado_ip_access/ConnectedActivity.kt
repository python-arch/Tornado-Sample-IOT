package com.example.tornado_ip_access

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog

class ConnectedActivity : AppCompatActivity() {

    private val POP_DELAY = 3000L

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connected)

        val webView: WebView = findViewById(R.id.webView)

        // Enable JavaScript (optional, depending on the web page's requirements)
        webView.settings.javaScriptEnabled = true

        // Load a web page
        webView.loadUrl("192.168.4.1")

        // Optional: Handle links within the WebView
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url!!)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                if(url == "192.168.4.1/download_app" ){
                    showPopup()
                    startActivity(Intent(this@ConnectedActivity  , SignUpActivity::class.java))
                }
            }
        }
    }

    private fun showPopup() {
       val alertDialog =  AlertDialog.Builder(this)
            .setTitle("Finished Registeration")
            .setMessage("You have reached the end of the registeration")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()

        alertDialog.show()

        Handler().postDelayed({alertDialog.dismiss()},POP_DELAY)


    }
}