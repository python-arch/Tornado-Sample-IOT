package com.example.tornado_ip_access

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject


class AzureConnectionTest : AppCompatActivity() {

    private lateinit var errorDialog: Dialog
    private lateinit var errorAdapter: ErrorAdapter


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
//            val temp_val = findViewById<TextView>(R.id.TEMP_CELSIUS_USER).text.toString()
//            val regex = Regex("""\d+""")
//            val matchResult = regex.find(temp_val)
//            val numericValue = matchResult?.value?.toIntOrNull()
//            val intent = Intent(this , ErrorsActivity::class.java)
//            val features_val = findViewById<TextView>(R.id.features).text.toString()
//            val mode_val = findViewById<TextView>(R.id.mode).text.toString()
//            val plasma_val = findViewById<TextView>(R.id.plasma).text.toString()
//            val fan_val = findViewById<TextView>(R.id.fan).text.toString()
//            val h_louvre_val = findViewById<TextView>(R.id.h_louvre).text.toString()
//            val v_louvre_val  = findViewById<TextView>(R.id.v_louvre).text.toString()
//            val timer_state_val = findViewById<TextView>(R.id.timer_state).text.toString()
//            val timer_hours_val = findViewById<TextView>(R.id.timer_hours).text.toString()
//            intent.putExtra("temp" , numericValue.toString())
//            intent.putExtra("features",features_val)
//            intent.putExtra("mode" , mode_val)
//            intent.putExtra("plasma" , plasma_val)
//            intent.putExtra("fan" , fan_val)
//            intent.putExtra("h_louvre" , h_louvre_val)
//            intent.putExtra("v_louvre" , v_louvre_val)
//            intent.putExtra("timer_state" , timer_state_val)
//            intent.putExtra("timer_hours" , timer_hours_val)
//
//            startActivity(intent)

            errorDialog.show()
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


        setupErrorDialog()



        onActivityOpened()
        // Start the runnable when the activity starts
        handler.post(runnable)
    }

    private fun setupErrorDialog() {
        errorDialog = Dialog(this)
        errorDialog.setContentView(R.layout.dialog_error_list)

        val errorRecyclerView = errorDialog.findViewById<RecyclerView>(R.id.errorRecyclerView)
        val dialogButton = errorDialog.findViewById<Button>(R.id.dialogButton)

        errorAdapter = ErrorAdapter(emptyList())
        errorRecyclerView.layoutManager = LinearLayoutManager(this)
        errorRecyclerView.adapter = errorAdapter

        dialogButton.setOnClickListener {
            errorDialog.dismiss()
        }
    }


    private fun onActivityOpened() {
        val features_val = findViewById<TextView>(R.id.features).text.toString()
        val mode_val = findViewById<TextView>(R.id.mode).text.toString()
        val plasma_val = findViewById<TextView>(R.id.plasma).text.toString()
        val fan_val = findViewById<TextView>(R.id.fan).text.toString()
        val h_louvre_val = findViewById<TextView>(R.id.h_louvre).text.toString()
        val v_louvre_val  = findViewById<TextView>(R.id.v_louvre).text.toString()
        val timer_state_val = findViewById<TextView>(R.id.timer_state).text.toString()
        val timer_hours_val = findViewById<TextView>(R.id.timer_hours).text.toString()
        val temp_val = findViewById<TextView>(R.id.TEMP_CELSIUS_USER).text.toString()
        val numericValue = extractNumericValue(temp_val)
        ApiClient.sendChangeRequest("/app_open", features_val , mode_val,plasma_val,fan_val,h_louvre_val,v_louvre_val,timer_state_val,timer_hours_val,numericValue!!.toString()) { response, err ->
                if (err != null) {
                    Log.e("AzureConnectionTest", "Error sending OPEN State", err)
                } else if (response.isNullOrEmpty()) {
                    Log.e("AzureConnectionTest", "Empty response")
                } else {
                    Log.d("AzureConnectionTest", response)
                }
            }
    }

    private fun extractNumericValue(text: String): Int? {
        val regex = Regex("""\d+""")
        val matchResult = regex.find(text)
        return matchResult?.value?.toIntOrNull() ?: 0
    }


    private fun onActivityClosed() {
        val features_val = findViewById<TextView>(R.id.features).text.toString()
        val mode_val = findViewById<TextView>(R.id.mode).text.toString()
        val plasma_val = findViewById<TextView>(R.id.plasma).text.toString()
        val fan_val = findViewById<TextView>(R.id.fan).text.toString()
        val h_louvre_val = findViewById<TextView>(R.id.h_louvre).text.toString()
        val v_louvre_val  = findViewById<TextView>(R.id.v_louvre).text.toString()
        val timer_state_val = findViewById<TextView>(R.id.timer_state).text.toString()
        val timer_hours_val = findViewById<TextView>(R.id.timer_hours).text.toString()
        val temp_val = findViewById<TextView>(R.id.TEMP_CELSIUS_USER).text.toString()
        val numericValue = extractNumericValue(temp_val)
        // code to execute when the activity is closed
        ApiClient.sendChangeRequest("/app_closed" ,features_val , mode_val,plasma_val,fan_val,h_louvre_val,v_louvre_val,timer_state_val,timer_hours_val,numericValue!!.toString()){ response , err->
            if (err != null) {
                // Handle error
                Log.e("AzureConnectionTest", "Error sending CLOSED State", err)
            }

            if (response!!.isEmpty()) {
                // Handle empty response
                Log.e("AzureConnectionTest", "Empty response")
            }

            Log.d("Azure connection test" , response)
        }
    }


    private fun getAveragePower(){
        val features_val = findViewById<TextView>(R.id.features).text.toString()
        val mode_val = findViewById<TextView>(R.id.mode).text.toString()
        val plasma_val = findViewById<TextView>(R.id.plasma).text.toString()
        val fan_val = findViewById<TextView>(R.id.fan).text.toString()
        val h_louvre_val = findViewById<TextView>(R.id.h_louvre).text.toString()
        val v_louvre_val  = findViewById<TextView>(R.id.v_louvre).text.toString()
        val timer_state_val = findViewById<TextView>(R.id.timer_state).text.toString()
        val timer_hours_val = findViewById<TextView>(R.id.timer_hours).text.toString()
        val temp_val = findViewById<TextView>(R.id.TEMP_CELSIUS_USER).text.toString()
        val numericValue = extractNumericValue(temp_val)
        ApiClient.sendChangeRequest("/get_power" ,features_val , mode_val,plasma_val,fan_val,h_louvre_val,v_louvre_val,timer_state_val,timer_hours_val,numericValue!!.toString()){response , err->
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
        val features_val = findViewById<TextView>(R.id.features).text.toString()
        val mode_val = findViewById<TextView>(R.id.mode).text.toString()
        val plasma_val = findViewById<TextView>(R.id.plasma).text.toString()
        val fan_val = findViewById<TextView>(R.id.fan).text.toString()
        val h_louvre_val = findViewById<TextView>(R.id.h_louvre).text.toString()
        val v_louvre_val  = findViewById<TextView>(R.id.v_louvre).text.toString()
        val timer_state_val = findViewById<TextView>(R.id.timer_state).text.toString()
        val timer_hours_val = findViewById<TextView>(R.id.timer_hours).text.toString()
        val temp_val = findViewById<TextView>(R.id.TEMP_CELSIUS_USER).text.toString()
        val numericValue = extractNumericValue(temp_val)
        ApiClient.sendChangeRequest("/reset_wifi" ,features_val , mode_val,plasma_val,fan_val,h_louvre_val,v_louvre_val,timer_state_val,timer_hours_val,numericValue!!.toString() ){ response , err ->
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
        val numericValue = extractNumericValue(findViewById<TextView>(R.id.TEMP_CELSIUS_USER).text.toString())
        val features_val = findViewById<TextView>(R.id.features).text.toString()
        val mode_val = findViewById<TextView>(R.id.mode).text.toString()
        val plasma_val = findViewById<TextView>(R.id.plasma).text.toString()
        val fan_val = findViewById<TextView>(R.id.fan).text.toString()
        val h_louvre_val = findViewById<TextView>(R.id.h_louvre).text.toString()
        val v_louvre_val  = findViewById<TextView>(R.id.v_louvre).text.toString()
        val timer_state_val = findViewById<TextView>(R.id.timer_state).text.toString()
        val timer_hours_val = findViewById<TextView>(R.id.timer_hours).text.toString()
        numericValue?.let {
            numberofUps++
            sendRequestupRunnable?.let { buttonupHandler.removeCallbacks(it) }
            sendRequestupRunnable = Runnable {
                var sentValue = it + numberofUps
                if (sentValue >= 99) sentValue = 99
                ApiClient.sendChangeRequest("/up", features_val,mode_val,plasma_val,fan_val,h_louvre_val,v_louvre_val,timer_state_val,timer_hours_val,sentValue.toString()) { response, error ->
                    if (error != null) {
                        Log.e("ApiClient", "Error: $error")
                    } else {
                        Log.d("ApiClient", "Response: $response")
                    }
                    numberofUps = 0
                }
            }
            buttonupHandler.postDelayed(sendRequestupRunnable!!, 500)
        }}

    fun onDownButtonClick(view: View) {
        val features_val = findViewById<TextView>(R.id.features).text.toString()
        val mode_val = findViewById<TextView>(R.id.mode).text.toString()
        val plasma_val = findViewById<TextView>(R.id.plasma).text.toString()
        val fan_val = findViewById<TextView>(R.id.fan).text.toString()
        val h_louvre_val = findViewById<TextView>(R.id.h_louvre).text.toString()
        val v_louvre_val  = findViewById<TextView>(R.id.v_louvre).text.toString()
        val timer_state_val = findViewById<TextView>(R.id.timer_state).text.toString()
        val timer_hours_val = findViewById<TextView>(R.id.timer_hours).text.toString()
        val numericValue = extractNumericValue(findViewById<TextView>(R.id.TEMP_CELSIUS_USER).text.toString())
        numericValue?.let {
            numberofDowns++
            sendRequestdownRunnable?.let { buttondownHandler.removeCallbacks(it) }
            sendRequestdownRunnable = Runnable {
                var sentValue = it - numberofDowns
                if (sentValue <= 0) sentValue = 0
                ApiClient.sendChangeRequest("/down",features_val,mode_val,plasma_val,fan_val,h_louvre_val,v_louvre_val,timer_state_val,timer_hours_val, sentValue.toString()) { response, error ->
                    if (error != null) {
                        Log.e("ApiClient", "Error: $error")
                    } else {
                        Log.d("ApiClient", "Response: $response")
                    }
                    numberofDowns = 0
                }
            }
            buttondownHandler.postDelayed(sendRequestdownRunnable!!, 500)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchData() {
        ApiClient.sendRequest("/get_data") { response, error ->
            if (error != null) {
                Log.e("AzureConnectionTest", "Error fetching display value", error)
                return@sendRequest
            }

            if (response.isNullOrEmpty()) {
                Log.e("AzureConnectionTest", "Empty response")
                return@sendRequest
            }

            try {
                val jsonObject = JSONObject(response)
                val componentData = jsonObject.getJSONObject("component_data")
                val timerData = jsonObject.getJSONObject("timer_data")
                val tempData = jsonObject.getJSONObject("temp_data")
                val humidity_data = jsonObject.getJSONObject("humidity_data")
                val power_data  = jsonObject.getJSONObject("power_data")
                val mode_data = jsonObject.getJSONObject("mode_data")
                val faultData = jsonObject.getJSONObject("fault_data")

                val errorList = mutableListOf<String>()

                val faultKeys = arrayOf(
                    "Short_circuit_in_outdoor_temperature_sensors",
                    "Compressor_ambient_High_Temperature",
                    "Outdoor_unit_stop",
                    "Open_circuit_in_outdoor_temperature_sensors",
                    "Increase_in_outdoor_unit_AC_current",
                    "Increase_in_outdoor_unit_DC_current",
                    "Cycle_temperature_error",
                    "Gas_leakage",
                    "Outdoor_PCB_Corrupted",
                    "Outdoor_fan_motor",
                    "Outdoor_PCB_fuse",
                    "Compressor_speed_error",
                    "Voltage_problem",
                    "Miss_connection_between_indoor_and_outdoor",
                    "Bad_connection_between_indoor_and_outdoor",
                    "Indoor_fan_motor",
                    "Wrong_EEPROM",
                    "Wifi_error",
                    "Room_temperature_error",
                    "Evaporator_temperature_error"
                )
                val faultDataKeys = arrayOf(
                    "OUTDOOR_SENSOR_HAPPEN",
                    "COMPRESSOR_AMBIENT_HIGH_TEMP_HAPPEN",
                    "OUTDOOR_UNIT_STOP_HAPPEN",
                    "OUTDOOR_SENSOR_OPEN_CIRCUIT_HAPPEN",
                    "OUTDOOR_UNIT_AC_CURRENT_INCREASE_HAPPEN",
                    "OUTDOOR_UNIT_DC_CURRENT_INCREASE_HAPPEN",
                    "CYCLE_TEMP_ERROR_HAPPEN",
                    "GAS_LEAKAGE_HAPPEN",
                    "OUTDOOR_PCB_CORRUPTED_HAPPEN",
                    "OUTDOOR_FAN_MOTOR_HAPPEN",
                    "OUTDOOR_PCB_FUSE_HAPPEN",
                    "COMPRESSOR_SPEED_ERROR_HAPPEN",
                    "VOLTAGE_PROBLEM_HAPPEN",
                    "MISSING_CONNECTION_HAPPEN",
                    "BAD_CONNECTION_HAPPEN",
                    "INDOOR_FAN_MOTOR_HAPPEN",
                    "WRONG_EEPROM_HAPPEN",
                    "WIFI_ERROR_HAPPEN",
                    "ROOM_TEMP_ERROR_HAPPEN",
                    "EVAPORATOR_TEMP_ERROR_HAPPEN"
                )

                for (i in 0 until faultKeys.size) {
                    val faultKey = faultKeys[i]
                    val faultDataKey = faultDataKeys[i]
                    appendToJsonStringList(
                        (i + 1).toString() + ". ",
                        faultData,
                        faultKey,
                        faultDataKey,
                        errorList
                    )
                }

                runOnUiThread {
                    findViewById<TextView>(R.id.plasma).text = componentData.getString("PLASMA")
                    findViewById<TextView>(R.id.fan).text = componentData.getString("FAN")
                    findViewById<TextView>(R.id.h_louvre).text = componentData.getString("H_LOUVRE")
                    findViewById<TextView>(R.id.v_louvre).text = componentData.getString("V_LOUVRE")
                    findViewById<TextView>(R.id.timer_state).text = timerData.getString("TIMER_STATE")
                    findViewById<TextView>(R.id.timer_hours).text = timerData.getString("TIMER_HOURS")
                    findViewById<TextView>(R.id.TEMP_CELSIUS_USER).text = tempData.getString("TEMP_CELSIUS_USER")
                    findViewById<TextView>(R.id.TEMP_CELSIUS_INDOOR).text =  tempData.getString("TEMP_CELSIUS_INDOOR")
                    findViewById<TextView>(R.id.TEMP_CELSIUS_OUTDOOR).text = tempData.getString("TEMP_CELSIUS_OUTDOOR")
                    findViewById<TextView>(R.id.HUMIDITY_RATIO).text = humidity_data.getString("HUMIDITY_RATIO")
                    findViewById<TextView>(R.id.CURRENT_POWER_WATT).text = power_data.getString("CURRENT_POWER_WATT")
                    findViewById<TextView>(R.id.DAILY_AVERAGE_POWER_WATT).text = power_data.getString("DAILY_AVERAGE_POWER_WATT")
                    findViewById<TextView>(R.id.mode).text = mode_data.getString("MODE")
                    findViewById<TextView>(R.id.features).text = mode_data.getString("FEATURES")

                    errorAdapter.updateErrors(errorList)

                }
            } catch (e: Exception) {
                Log.e("AzureConnectionTest", "Error parsing JSON", e)
            }
        }
    }

    fun appendToJsonStringList(prefix: String, faultData: JSONObject, key: String, happenKey: String, jsonStringList: MutableList<String>) {
            if(faultData.getString(key) == happenKey) {
                jsonStringList.add("$prefix ${faultData.getString(key)}")
            }

    }


    override fun onStop() {
        super.onStop()
        onActivityClosed()
    }
    override fun onDestroy() {
        super.onDestroy()
        // Safely remove any pending callbacks
        handler.removeCallbacks(runnable)
        sendRequestupRunnable?.let { buttonupHandler.removeCallbacks(it) }
        sendRequestdownRunnable?.let { buttondownHandler.removeCallbacks(it) }

        // Null check before accessing any variables that might be null
        if (numberofUps != null) {
            numberofUps = 0
        }
        if (numberofDowns != null) {
            numberofDowns = 0
        }

    }



}
