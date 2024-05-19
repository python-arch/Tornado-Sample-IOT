package com.example.tornado_ip_access

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class After_Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_login)

        var test_button = findViewById<Button>(R.id.test)

        test_button.setOnClickListener {
//            throw RuntimeException("Test Crash") // Force a crash
            startActivity(Intent(this , MainActivity::class.java))
        }

        val network_buttpn = findViewById<Button>(R.id.network_mode)

        network_buttpn.setOnClickListener {
            startActivity(Intent(this , AzureConnectionTest::class.java ))
        }
    }
}