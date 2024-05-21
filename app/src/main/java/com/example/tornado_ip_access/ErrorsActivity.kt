package com.example.tornado_ip_access

import android.annotation.SuppressLint
import android.graphics.Color
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
            handler.postDelayed(this, 500) // Repeat every 1 second
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
    private  lateinit var  temp_val:String
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

//        // Fetch and display initial display value
//        fetchData()
//
//        // repeat the task
//        handler.post(runnable)

        temp_val = intent.getStringExtra("temp")!!
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
        ApiClient.sendChangeRequest("/app_open" , temp_val){ response , err->
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
        ApiClient.sendChangeRequest("/app_closed" , temp_val){ response , err->
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
                    shortCircuitTextView.text = "1. " + faultData.getString("Short_circuit_in_outdoor_temperature_sensors")
                    if (faultData.getString("Short_circuit_in_outdoor_temperature_sensors").equals("OUTDOOR_SENSOR_HAPPEN")) {
                        shortCircuitTextView.setTextColor(Color.RED);  // Setting the text color to red
                    }
                    compressorTextView.text = "2. " + faultData.getString("Compressor_ambient_High_Temperature")
                    if (faultData.getString("Compressor_ambient_High_Temperature").equals("COMPRESSOR_AMBIENT_HIGH_TEMP_HAPPEN")) {
                        compressorTextView.setTextColor(Color.RED)  // Setting the text color to red
                    }
                    outdoorUnitTextView.text = "3. " + faultData.getString("Outdoor_unit_stop")
                    if (faultData.getString("Outdoor_unit_stop").equals("OUTDOOR_UNIT_STOP_HAPPEN")) {
                        outdoorUnitTextView.setTextColor(Color.RED)  // Setting the text color to red
                    }
                    openCircuitTextView.text = "4. "+ faultData.getString("Open_circuit_in_outdoor_temperature_sensors")
                    if (faultData.getString("Open_circuit_in_outdoor_temperature_sensors").equals("OUTDOOR_SENSOR_OPEN_CIRCUIT_HAPPEN")) {
                        openCircuitTextView.setTextColor(Color.RED)  // Setting the text color to red
                    }
                    increaseOutdoorUnitDcCurrent6TextView.text = "5. " +  faultData.getString("Increase_in_outdoor_unit_AC_current")
                    if (faultData.getString("Increase_in_outdoor_unit_AC_current").equals("OUTDOOR_UNIT_AC_CURRENT_INCREASE_HAPPEN")) {
                        increaseOutdoorUnitDcCurrent6TextView.setTextColor(Color.RED)  // Setting the text color to red
                    }
                    increaseOutdoorUnitDcCurrent7TextView.text =  "6. "+ faultData.getString("Increase_in_outdoor_unit_DC_current")
                    if (faultData.getString("Increase_in_outdoor_unit_DC_current").equals("OUTDOOR_UNIT_DC_CURRENT_INCREASE_HAPPEN")) {
                        increaseOutdoorUnitDcCurrent7TextView.setTextColor(Color.RED)  // Setting the text color to red
                    }
                    cycleTempTextView.text = "7. "+faultData.getString("Cycle_temperature_error")
                    if (faultData.getString("Cycle_temperature_error").equals("CYCLE_TEMP_ERROR_HAPPEN")) {
                        cycleTempTextView.setTextColor(Color.RED)  // Setting the text color to red
                    }
                    gasLeakTextView.text = "8. "+ faultData.getString("Gas_leakage")
                    if (faultData.getString("Gas_leakage").equals("GAS_LEAKAGE_HAPPEN")) {
                        gasLeakTextView.setTextColor(Color.RED)  // Setting the text color to red
                    }
                    error9TextView.text = "9. "+ faultData.getString("Outdoor_PCB_Corrupted")
                    if (faultData.getString("Outdoor_PCB_Corrupted").equals("OUTDOOR_PCB_CORRUPTED_HAPPEN")) {
                        error9TextView.setTextColor(Color.RED)  // Setting the text color to red
                    }
                    outdoorFanTextView.text ="10. " +faultData.getString("Outdoor_fan_motor")
                    if (faultData.getString("Outdoor_fan_motor").equals("OUTDOOR_FAN_MOTOR_HAPPEN")) {
                        outdoorFanTextView.setTextColor(Color.RED)  // Setting the text color to red
                    }
                    outdoorPcbTextView.text = "11. " + faultData.getString("Outdoor_PCB_fuse")
                    if (faultData.getString("Outdoor_PCB_fuse").equals("OUTDOOR_PCB_FUSE_HAPPEN")) {
                        outdoorPcbTextView.setTextColor(Color.RED)  // Setting the text color to red
                    }
                    speedErrorTextView.text = "12. "+ faultData.getString("Compressor_speed_error")
                    if (faultData.getString("Compressor_speed_error").equals("COMPRESSOR_SPEED_ERROR_HAPPEN")) {
                        speedErrorTextView.setTextColor(Color.RED)  // Setting the text color to red
                    }
                    voltTextView.text = "13. "+  faultData.getString("Voltage_problem")
                    if (faultData.getString("Voltage_problem").equals("VOLTAGE_PROBLEM_HAPPEN")) {
                        voltTextView.setTextColor(Color.RED)  // Setting the text color to red
                    }
                    missConnectionTextView.text = "14. "+ faultData.getString("Miss_connection_between_indoor_and_outdoor")
                    if (faultData.getString("Miss_connection_between_indoor_and_outdoor").equals("MISSING_CONNECTION_HAPPEN")) {
                        missConnectionTextView.setTextColor(Color.RED)  // Setting the text color to red
                    }
                    badConnectionTextView.text =  "15. "+ faultData.getString("Bad_connection_between_indoor_and_outdoor")
                    if (faultData.getString("Bad_connection_between_indoor_and_outdoor").equals("BAD_CONNECTION_HAPPEN")) {
                        badConnectionTextView.setTextColor(Color.RED)  // Setting the text color to red
                    }
                    indoorFanTextView.text = "16. "+ faultData.getString("Indoor_fan_motor")
                    if (faultData.getString("Indoor_fan_motor").equals("INDOOR_FAN_MOTOR_HAPPEN")) {
                        indoorFanTextView.setTextColor(Color.RED)  // Setting the text color to red
                    }
                    wrongEepromTextView.text = "17. "+ faultData.getString("Wrong_EEPROM")
                    if (faultData.getString("Wrong_EEPROM").equals("WRONG_EEPROM_HAPPEN")) {
                        wrongEepromTextView.setTextColor(Color.RED)  // Setting the text color to red
                    }
                    wifiErrorTextView.text = "18. "+ faultData.getString("Wifi_error")
                    if (faultData.getString("Wifi_error").equals("WIFI_ERROR_HAPPEN")) {
                        wifiErrorTextView.setTextColor(Color.RED)  // Setting the text color to red
                    }
                    roomTempError26_1TextView.text = "19. "+ faultData.getString("Room_temperature_error")
                    if (faultData.getString("Room_temperature_error").equals("ROOM_TEMP_ERROR_HAPPEN")) {
                        roomTempError26_1TextView.setTextColor(Color.RED)  // Setting the text color to red
                    }

                    roomTempError26_2TextView.text =  "20. "+ faultData.getString("Evaporator_temperature_error")
                    if (faultData.getString("Evaporator_temperature_error").equals("EVAPORATOR_TEMP_ERROR_HAPPEN")) {
                        roomTempError26_2TextView.setTextColor(Color.RED)  // Setting the text color to red
                    }
                }
//                Log.d("display", response)
            } catch (e: Exception) {
                // Handle JSON parsing error
                Log.e("AzureConnectionTest", "Error parsing JSON", e)
            }
        }

    }
}