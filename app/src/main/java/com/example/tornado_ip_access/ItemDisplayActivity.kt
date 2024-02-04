package com.example.tornado_ip_access

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ItemDisplayActivity : AppCompatActivity() {

    lateinit var product_text : TextView
    lateinit var model_text : TextView
    lateinit var device_text : TextView
    lateinit var panel_button : Button
    lateinit var config_button : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_display)


        product_text = findViewById(R.id.product)
        model_text = findViewById(R.id.model)
        device_text = findViewById(R.id.device)

        var intent = intent

        product_text.setText(intent.getStringExtra("product"))
        model_text.setText(intent.getStringExtra("model"))
        device_text.setText(intent.getStringExtra("device"))

        panel_button = findViewById(R.id.panel)
        config_button = findViewById(R.id.config)

        panel_button.setOnClickListener {
            startActivity(Intent(this, ConfigActivity::class.java ))
        }

        config_button.setOnClickListener {
            startActivity(Intent(this , PanelActivity::class.java))
        }

    }
}