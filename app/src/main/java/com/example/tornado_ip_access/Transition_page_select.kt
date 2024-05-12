package com.example.tornado_ip_access

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Transition_page_select : AppCompatActivity() {
    private lateinit var continue_btn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transition_page_select)


        val responseBody = intent.getStringExtra("responseBody")

        val text_device = findViewById<TextView>(R.id.text_device)
        text_device.text = "Paired With Device "+ responseBody
        continue_btn = findViewById(R.id.continue_btn)

        continue_btn.setOnClickListener {
            startActivity(Intent(this , AzureConnectionTest::class.java))
        }
    }
}