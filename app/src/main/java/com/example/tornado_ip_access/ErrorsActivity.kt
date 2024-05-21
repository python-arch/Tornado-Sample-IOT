package com.example.tornado_ip_access

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.core.view.isVisible
import org.json.JSONObject

class ErrorsActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            fetchData()
            handler.postDelayed(this, 1000) // Repeat every 1 second
        }
    }

    private lateinit var shortCircuitTextView: TextView
    private lateinit var compressorTextView: TextView
    private lateinit var outdoorUnitTextView: TextView
    private lateinit var openCircuitTextView: TextView
    private lateinit var increaseOutdoorUnitDcCurrent6TextView: TextView
    private lateinit var increaseOutdoorUnitDcCurrent7TextView: TextView
    private lateinit var cycleTempTextView: TextView
    private lateinit var gasLeakTextView: TextView
    private lateinit var error9TextView: TextView
    private lateinit var outdoorFanTextView: TextView
    private lateinit var outdoorPcbTextView: TextView
    private lateinit var speedErrorTextView: TextView
    private lateinit var voltTextView: TextView
    private lateinit var missConnectionTextView: TextView
    private lateinit var badConnectionTextView: TextView
    private lateinit var indoorFanTextView: TextView
    private lateinit var wrongEepromTextView: TextView
    private lateinit var wifiErrorTextView: TextView
    private lateinit var roomTempError26_1TextView: TextView
    private lateinit var roomTempError26_2TextView: TextView
    private lateinit var temp_val: String
    private lateinit var features_val:String
    private lateinit var  mode_val:String
    private lateinit var plasma_val:String
    private lateinit var fan_val:String
    private lateinit var h_louvre_val:String
    private lateinit var v_louvre_val:String
    private lateinit var timer_state_val:String
    private lateinit var timer_hours_val:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_errors)

        // Initialize TextViews
        shortCircuitTextView = findViewById(R.id.Short_circuit)
        compressorTextView = findViewById(R.id.Compressor)
        outdoorUnitTextView = findViewById(R.id.outdoor_unit)
        openCircuitTextView = findViewById(R.id.open_circuit)
        increaseOutdoorUnitDcCurrent6TextView = findViewById(R.id.Increase_in_outdoor_unit_DC_current_6_0)
        increaseOutdoorUnitDcCurrent7TextView = findViewById(R.id.Increase_in_outdoor_unit_DC_current_7_0)
        cycleTempTextView = findViewById(R.id.cycle_temp)
        gasLeakTextView = findViewById(R.id.gas_leak)
        error9TextView = findViewById(R.id.error_9)
        outdoorFanTextView = findViewById(R.id.outdoor_fan)
        outdoorPcbTextView = findViewById(R.id.outdoor_pcb)
        speedErrorTextView = findViewById(R.id.speed_error)
        voltTextView = findViewById(R.id.volt)
        missConnectionTextView = findViewById(R.id.miss_connection)
        badConnectionTextView = findViewById(R.id.bad_coonnection)
        indoorFanTextView = findViewById(R.id.indoor_fan)
        wrongEepromTextView = findViewById(R.id.wrong_eeprom)
        wifiErrorTextView = findViewById(R.id.wifi_error)
        roomTempError26_1TextView = findViewById(R.id.room_temp_error_26_1)
        roomTempError26_2TextView = findViewById(R.id.room_temp_error_26_2)

        // get intent
        temp_val = intent.getStringExtra("temp")!!
        features_val = intent.getStringExtra("features")!!
        mode_val = intent.getStringExtra("mode")!!
        plasma_val = intent.getStringExtra("plasma")!!
        fan_val = intent.getStringExtra("fan")!!
        h_louvre_val = intent.getStringExtra("h_louvre")!!
        v_louvre_val = intent.getStringExtra("v_louvre")!!
        timer_state_val = intent.getStringExtra("timer_state")!!
        timer_hours_val = intent.getStringExtra("timer_hours")!!

        fetchData()
        handler.post(runnable)

    }


//    override fun onStart() {
//        super.onStart()
//        onActivityOpened()
//
//    }


    private fun onActivityOpened() {
        ApiClient.sendChangeRequest("/app_open", features_val , mode_val,plasma_val,fan_val,h_louvre_val,v_louvre_val,timer_state_val,timer_hours_val,temp_val) { response, err ->
            if (err != null) {
                Log.e("ErrorsActivity", "Error sending OPEN State", err)
            } else if (response.isNullOrEmpty()) {
                Log.e("ErrorsActivity", "Empty response")
            } else {
                Log.d("ErrorsActivity", response)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        onActivityClosed()
    }
    @SuppressLint("SetTextI18n")
    private fun fetchData() {
        ApiClient.sendRequest("/get_data") { response, error ->
            if (error != null) {
                Log.e("ErrorsActivity", "Error fetching display value", error)
                return@sendRequest
            }

            if (response!!.isEmpty()) {
                Log.e("ErrorsActivity", "Empty response")
                return@sendRequest
            }

            try {
                val jsonObject = JSONObject(response)
                val faultData = jsonObject.getJSONObject("fault_data")

                runOnUiThread {
                    updateTextView(shortCircuitTextView, "1. ", faultData, "Short_circuit_in_outdoor_temperature_sensors", "OUTDOOR_SENSOR_HAPPEN")
                    updateTextView(compressorTextView, "2. ", faultData, "Compressor_ambient_High_Temperature", "COMPRESSOR_AMBIENT_HIGH_TEMP_HAPPEN")
                    updateTextView(outdoorUnitTextView, "3. ", faultData, "Outdoor_unit_stop", "OUTDOOR_UNIT_STOP_HAPPEN")
                    updateTextView(openCircuitTextView, "4. ", faultData, "Open_circuit_in_outdoor_temperature_sensors", "OUTDOOR_SENSOR_OPEN_CIRCUIT_HAPPEN")
                    updateTextView(increaseOutdoorUnitDcCurrent6TextView, "5. ", faultData, "Increase_in_outdoor_unit_AC_current", "OUTDOOR_UNIT_AC_CURRENT_INCREASE_HAPPEN")
                    updateTextView(increaseOutdoorUnitDcCurrent7TextView, "6. ", faultData, "Increase_in_outdoor_unit_DC_current", "OUTDOOR_UNIT_DC_CURRENT_INCREASE_HAPPEN")
                    updateTextView(cycleTempTextView, "7. ", faultData, "Cycle_temperature_error", "CYCLE_TEMP_ERROR_HAPPEN")
                    updateTextView(gasLeakTextView, "8. ", faultData, "Gas_leakage", "GAS_LEAKAGE_HAPPEN")
                    updateTextView(error9TextView, "9. ", faultData, "Outdoor_PCB_Corrupted", "OUTDOOR_PCB_CORRUPTED_HAPPEN")
                    updateTextView(outdoorFanTextView, "10. ", faultData, "Outdoor_fan_motor", "OUTDOOR_FAN_MOTOR_HAPPEN")
                    updateTextView(outdoorPcbTextView, "11. ", faultData, "Outdoor_PCB_fuse", "OUTDOOR_PCB_FUSE_HAPPEN")
                    updateTextView(speedErrorTextView, "12. ", faultData, "Compressor_speed_error", "COMPRESSOR_SPEED_ERROR_HAPPEN")
                    updateTextView(voltTextView, "13. ", faultData, "Voltage_problem", "VOLTAGE_PROBLEM_HAPPEN")
                    updateTextView(missConnectionTextView, "14. ", faultData, "Miss_connection_between_indoor_and_outdoor", "MISSING_CONNECTION_HAPPEN")
                    updateTextView(badConnectionTextView, "15. ", faultData, "Bad_connection_between_indoor_and_outdoor", "BAD_CONNECTION_HAPPEN")
                    updateTextView(indoorFanTextView, "16. ", faultData, "Indoor_fan_motor", "INDOOR_FAN_MOTOR_HAPPEN")
                    updateTextView(wrongEepromTextView, "17. ", faultData, "Wrong_EEPROM", "WRONG_EEPROM_HAPPEN")
                    updateTextView(wifiErrorTextView, "18. ", faultData, "Wifi_error", "WIFI_ERROR_HAPPEN")
                    updateTextView(roomTempError26_1TextView, "19. ", faultData, "Room_temperature_error", "ROOM_TEMP_ERROR_HAPPEN")
                    updateTextView(roomTempError26_2TextView, "20. ", faultData, "Evaporator_temperature_error", "EVAPORATOR_TEMP_ERROR_HAPPEN")
                }
            } catch (e: Exception) {
                Log.e("Errors Activity", "Error parsing JSON", e)
            }
        }
    }

    private fun onActivityClosed() {

        ApiClient.sendChangeRequest("/app_closed", features_val , mode_val,plasma_val,fan_val,h_louvre_val,v_louvre_val,timer_state_val,timer_hours_val,temp_val) { response, err ->
            if (err != null) {
                Log.e("Errors Activity", "Error sending closed State", err)
            } else if (response.isNullOrEmpty()) {
                Log.e("Errors Activity", "Empty response")
            } else {
                Log.d("Errors Activity", response)
            }
        }
    }
    private fun updateTextView(textView: TextView, prefix: String, faultData: JSONObject, key: String, errorCondition: String) {
        textView.text = "$prefix ${faultData.getString(key)}"
        if (faultData.getString(key) == errorCondition) {
            textView.setTextColor(Color.RED)
        }else{
            textView.isVisible= false
        }
    }
}
