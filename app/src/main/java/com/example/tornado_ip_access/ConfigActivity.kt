package com.example.tornado_ip_access

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ConfigActivity : AppCompatActivity() {
    lateinit var  back_to_device : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        back_to_device = findViewById(R.id.back_to_device)

        back_to_device.setOnClickListener {
            startActivity(Intent(this , Dashboard_Activity::class.java))
        }
    }
}