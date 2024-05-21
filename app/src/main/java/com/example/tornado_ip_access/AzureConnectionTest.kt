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
import com.google.android.gms.common.api.Api


class AzureConnectionTest : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            fetchData()
            handler.postDelayed(this, 500) // Repeat every 1 second
        }
    }

    private val buttonupHandler = Handler(Looper.getMainLooper())
    private var sendRequestupRunnable: Runnable? = null

    private val buttondownHandler = Handler(Looper.getMainLooper())
    private var sendRequestdownRunnable: Runnable? = null

    private var numberofUps = 0
    private var numberofDowns =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_azure_connection_test)

        // send request that the app is open

//        // Fetch and display initial display value
//        fetchData()
//
//        // repeat the task
//        handler.post(runnable)

        val button_error: Button = findViewById(R.id.button_next_errors)

        button_error.setOnClickListener{
            val temp_val = findViewById<TextView>(R.id.TEMP_CELSIUS_USER).text.toString()
            val regex = Regex("""\d+""")
            val matchResult = regex.find(temp_val)
            val numericValue = matchResult?.value?.toIntOrNull()
            val intent = Intent(this , ErrorsActivity::class.java)
            intent.putExtra("temp" , numericValue.toString())
            startActivity(intent)
        }

        // reset_wifi
        val reset_wifi : Button = findViewById(R.id.btn_reset_wifi)
        reset_wifi.setOnClickListener {
            resetWifi()
        }

        // add button to get the average power data
        val average_power_button:Button = findViewById(R.id.button_get_power)
        average_power_button.setOnClickListener {
           getAveragePower()
        }
    }

    override fun onStart() {
        super.onStart()

        onActivityOpened()
        // Start the runnable when the activity starts
        handler.post(runnable)
    }

    override fun onStop() {
        super.onStop()
        onActivityClosed()
        // Stop the runnable when the activity stops
        handler.removeCallbacks(runnable)
    }

    private fun onActivityOpened() {
        // code to execute when the activity is opened
        val temp_val = findViewById<TextView>(R.id.TEMP_CELSIUS_USER).text.toString()
        val regex = Regex("""\d+""")
        val matchResult = regex.find(temp_val)
        val numericValue = matchResult?.value?.toIntOrNull()
        ApiClient.sendChangeRequest("/app_open" , numericValue.toString()){ response , err->
            if (err != null) {
                // Handle error
                Log.e("AzureConnectionTest", "Error sending OPEN State", err)
            }

            if (response!!.isEmpty()) {
                // Handle empty response
                Log.e("AzureConnectionTest", "Empty response")
            }
        }
    }

    private fun onActivityClosed() {
        val temp_val = findViewById<TextView>(R.id.TEMP_CELSIUS_USER).text.toString()
        val regex = Regex("""\d+""")
        val matchResult = regex.find(temp_val)
        val numericValue = matchResult?.value?.toIntOrNull()
        // code to execute when the activity is closed
        ApiClient.sendChangeRequest("/app_closed" , numericValue.toString()){ response , err->
            if (err != null) {
                // Handle error
                Log.e("AzureConnectionTest", "Error sending CLOSED State", err)
            }

            if (response!!.isEmpty()) {
                // Handle empty response
                Log.e("AzureConnectionTest", "Empty response")
            }
        }
    }


    private fun getAveragePower(){
        val temp_val = findViewById<TextView>(R.id.TEMP_CELSIUS_USER).text.toString()
        val regex = Regex("""\d+""")
        val matchResult = regex.find(temp_val)
        val numericValue = matchResult?.value?.toIntOrNull()
        ApiClient.sendChangeRequest("/get_power" , numericValue.toString()){response , err->
            if (err != null) {
                // Handle error
                Log.e("AzureConnectionTest", "Error fetching POWER value", err)
            }

            if (response!!.isEmpty()) {
                // Handle empty response
                Log.e("AzureConnectionTest", "Empty response")
            }

        }
    }

    private fun resetWifi(){
        val temp_val = findViewById<TextView>(R.id.TEMP_CELSIUS_USER).text.toString()
        val regex = Regex("""\d+""")
        val matchResult = regex.find(temp_val)
        val numericValue = matchResult?.value?.toIntOrNull()
        ApiClient.sendChangeRequest("/reset_wifi" ,numericValue.toString() ){ response , err ->
            if (err != null) {
                // Handle error
                Log.e("AzureConnectionTest", "Error Resetting WIFI", err)
            }

            if (response!!.isEmpty()) {
                // Handle empty response
                Log.e("AzureConnectionTest", "Empty response")
            }

            startActivity(Intent(this , After_Login::class.java))
        }
    }

    fun onUpButtonClick(view: View) {
        val temp_val = findViewById<TextView>(R.id.TEMP_CELSIUS_USER).text.toString()
        val regex = Regex("""\d+""")
        val matchResult = regex.find(temp_val)
        numberofUps+=1
        val numericValue = matchResult?.value?.toIntOrNull()

        // Cancel any previously posted runnable
        sendRequestupRunnable?.let { buttonupHandler.removeCallbacks(it) }

        // Create a new runnable to send the request after a delay
        sendRequestupRunnable = Runnable {
            var sent_value = numericValue?.plus(numberofUps)!!.toInt()
            if(sent_value>99){
                sent_value =99
            }
            ApiClient.sendChangeRequest("/up", sent_value.toString()) { response, error ->
                if (error != null) {
                    Log.e("ApiClient", "Error: $error")
                    // Handle error
                } else {
                    Log.d("ApiClient", "Response: $response")
                    // Handle response
                }
                numberofUps=0
            }
        }

        // Post the new runnable with a delay of 300 milliseconds (adjust as needed)
        buttonupHandler.postDelayed(sendRequestupRunnable!!, 500)
    }

    fun onDownButtonClick(view: View) {
        val temp_val = findViewById<TextView>(R.id.TEMP_CELSIUS_USER).text.toString()
        val regex = Regex("""\d+""")
        val matchResult = regex.find(temp_val)
        val numericValue = matchResult?.value?.toIntOrNull()
       numberofDowns+=1
        // Cancel any previously posted runnable
        sendRequestdownRunnable?.let { buttondownHandler.removeCallbacks(it) }

        // Create a new runnable to send the request after a delay
        sendRequestdownRunnable = Runnable {
            var sent_value = numericValue?.minus(numberofDowns)!!.toInt()
            if(sent_value<=0){
                sent_value =0
            }
            ApiClient.sendChangeRequest("/down", sent_value.toString()) { response, error ->
                if (error != null) {
                    Log.e("ApiClient", "Error: $error")
                    // Handle error
                } else {
                    Log.d("ApiClient", "Response: $response")
                    // Handle response
                }

                numberofDowns = 0
            }
        }

        // Post the new runnable with a delay of 300 milliseconds (adjust as needed)
        buttondownHandler.postDelayed(sendRequestdownRunnable!!, 500)
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
                    // current power data
                    findViewById<TextView>(R.id.CURRENT_POWER_WATT).text = power_data.getString("CURRENT_POWER_WATT")
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
