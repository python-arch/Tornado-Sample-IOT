package com.example.tornado_ip_access

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import android.util.Log
import android.widget.Button
import android.widget.Toast


class AzureConnectionTest : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            fetchData()
            handler.postDelayed(this, 1000) // Repeat every 1 second
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_azure_connection_test)

        // Fetch and display initial display value
        fetchData()

        // repeat the task
        handler.post(runnable)

        val button_error: Button = findViewById(R.id.button_next_errors)

        button_error.setOnClickListener{
            startActivity(Intent(this , ErrorsActivity::class.java))
        }

        // reset_wifi
        val reset_wifi : Button = findViewById(R.id.btn_reset_wifi)
        reset_wifi.setOnClickListener {
            resetWifi()
        }
    }

    private fun resetWifi(){
        ApiClient.sendRequest("/reset_wifi"){ response , err ->
            if (err != null) {
                // Handle error
                Log.e("AzureConnectionTest", "Error fetching display value", err)
                return@sendRequest
            }

            if (response!!.isEmpty()) {
                // Handle empty response
                Log.e("AzureConnectionTest", "Empty response")
                return@sendRequest
            }

            startActivity(Intent(this , After_Login::class.java))
        }
    }

    fun onUpButtonClick(view: View) {
        val temp_val = findViewById<TextView>(R.id.TEMP_CELSIUS_USER).text.toString()
        val regex = Regex("""\d+""")
        val matchResult = regex.find(temp_val)
        val numericValue = matchResult?.value?.toIntOrNull()
        val sent_val = numericValue?.plus(1) ?: 0
        ApiClient.sendChangeRequest("/up", sent_val.toString()) { response, error ->
            if (error != null) {
                Log.e("ApiClient", "Error: $error")
                // Handle error
            } else {
                Log.d("ApiClient", "Response: $response")
                // Handle response
            }
        }
    }

    fun onDownButtonClick(view: View) {
        val temp_val = findViewById<TextView>(R.id.TEMP_CELSIUS_USER).text.toString()
        val regex = Regex("""\d+""")
        val matchResult = regex.find(temp_val)
        val numericValue = matchResult?.value?.toIntOrNull()
        val sent_val = numericValue?.minus(1) ?: 0
        ApiClient.sendChangeRequest("/down", sent_val.toString()) { response, error ->
            if (error != null) {
                Log.e("ApiClient", "Error: $error")
                Toast.makeText(this,"Error: $error", Toast.LENGTH_LONG).show()
                // Handle error
            } else {
                Log.d("ApiClient", "Response: $response")
                // Handle response
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchData() {
        ApiClient.sendRequest("/get_data") { response, error ->
            if (error != null) {
                // Handle error
                Log.e("AzureConnectionTest", "Error fetching display value", error)
                return@sendRequest
            }

            if (response!!.isEmpty()) {
                // Handle empty response
                Log.e("AzureConnectionTest", "Empty response")
                return@sendRequest
            }

            try {
                val jsonObject = JSONObject(response)
                // Extract individual fields
                val componentData = jsonObject.getJSONObject("component_data")
                val timerData = jsonObject.getJSONObject("timer_data")
                val tempData = jsonObject.getJSONObject("temp_data")
                val humidity_data = jsonObject.getJSONObject("humidity_data")
                val power_data  = jsonObject.getJSONObject("power_data")
                val mode_data = jsonObject.getJSONObject("mode_data")


                runOnUiThread {
                    // Update TextViews with extracted data
                    // component data
                    findViewById<TextView>(R.id.plasma).text = componentData.getString("PLASMA")
                    findViewById<TextView>(R.id.fan).text ="FAN: " + componentData.getString("FAN")
                    findViewById<TextView>(R.id.h_louvre).text = "H_LOUVRE: "+ componentData.getString("H_LOUVRE")
                    findViewById<TextView>(R.id.v_louvre).text = "V_LOUVRE: "+ componentData.getString("V_LOUVRE")
                    //timer_data
                    findViewById<TextView>(R.id.timer_state).text ="TIMER_STATE: "+ timerData.getString("TIMER_STATE")
                    findViewById<TextView>(R.id.timer_hours).text = "TIMER_HOURS: "+ timerData.getString("TIMER_HOURS")
                    // TEMP_DATA
                    findViewById<TextView>(R.id.TEMP_CELSIUS_USER).text ="TEMP_USER: "+ tempData.getString("TEMP_CELSIUS_USER")
                    findViewById<TextView>(R.id.TEMP_CELSIUS_INDOOR).text = "TEMP_INDOOR: "+ tempData.getString("TEMP_CELSIUS_INDOOR")
                    findViewById<TextView>(R.id.TEMP_CELSIUS_OUTDOOR).text = "TEMP_OUTDOOR: "+ tempData.getString("TEMP_CELSIUS_OUTDOOR")
                    // humidity data
                    findViewById<TextView>(R.id.HUMIDITY_RATIO).text ="HUMIDITY_RATIO: "+ humidity_data.getString("HUMIDITY_RATIO")
                    // power data
                    findViewById<TextView>(R.id.DAILY_AVERAGE_POWER_WATT).text = power_data.getString("DAILY_AVERAGE_POWER_WATT")
                    // mode data
                    findViewById<TextView>(R.id.mode).text = mode_data.getString("MODE")
                    findViewById<TextView>(R.id.features).text = mode_data.getString("FEATURES")
                }
//                Log.d("display", response)
            } catch (e: Exception) {
                // Handle JSON parsing error
                Log.e("AzureConnectionTest", "Error parsing JSON", e)
            }
        }


    }

}
