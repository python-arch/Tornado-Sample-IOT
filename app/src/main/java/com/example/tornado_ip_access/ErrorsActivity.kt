package com.example.tornado_ip_access

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_errors)

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

        // Fetch and display initial display value
        fetchData()

        // repeat the task
        handler.post(runnable)
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
                val faultData = jsonObject.getJSONObject("fault_data")


                runOnUiThread {
                    // Update TextViews with extracted data
                    // fault data
                    shortCircuitTextView.text = faultData.getString("Short_circuit_in_outdoor_temperature_sensors")
                    compressorTextView.text =  faultData.getString("Compressor_ambient_High_Temperature")
                    outdoorUnitTextView.text = faultData.getString("Outdoor_unit_stop")
                    openCircuitTextView.text = faultData.getString("Open_circuit_in_outdoor_temperature_sensors")
                    increaseOutdoorUnitDcCurrent6TextView.text =  faultData.getString("Increase_in_outdoor_unit_AC_current")
                    increaseOutdoorUnitDcCurrent7TextView.text =  faultData.getString("Increase_in_outdoor_unit_DC_current")
                    cycleTempTextView.text = faultData.getString("Cycle_temperature_error")
                    gasLeakTextView.text = faultData.getString("Gas_leakage")
                    error9TextView.text =  faultData.getString("Outdoor_PCB_Corrupted")
                    outdoorFanTextView.text = faultData.getString("Outdoor_fan_motor")
                    outdoorPcbTextView.text =  faultData.getString("Outdoor_PCB_fuse")
                    speedErrorTextView.text =  faultData.getString("Compressor_speed_error")
                    voltTextView.text =  faultData.getString("Voltage_problem")
                    missConnectionTextView.text =  faultData.getString("Miss_connection_between_indoor_and_outdoor")
                    badConnectionTextView.text =  faultData.getString("Bad_connection_between_indoor_and_outdoor")
                    indoorFanTextView.text = faultData.getString("Indoor_fan_motor")
                    wrongEepromTextView.text = faultData.getString("Wrong_EEPROM")
                    wifiErrorTextView.text = faultData.getString("Wifi_error")
                    roomTempError26_1TextView.text =  faultData.getString("Room_temperature_error")
                    roomTempError26_2TextView.text =  faultData.getString("Evaporator_temperature_error")

                }
//                Log.d("display", response)
            } catch (e: Exception) {
                // Handle JSON parsing error
                Log.e("AzureConnectionTest", "Error parsing JSON", e)
            }
        }

    }
}