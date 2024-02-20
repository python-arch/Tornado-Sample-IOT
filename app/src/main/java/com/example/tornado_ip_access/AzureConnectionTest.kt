package com.example.tornado_ip_access

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import android.util.Log
import android.widget.Toast


class AzureConnectionTest : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            fetchAndDisplayDisplayValue()
            handler.postDelayed(this, 1000) // Repeat every 1 second
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_azure_connection_test)

        // Fetch and display initial display value
        fetchAndDisplayDisplayValue()

        // repeat the task
        handler.post(runnable)
    }

    fun onUpButtonClick(view: View) {
        ApiClient.sendRequest("/up") { response, error ->
            if (error != null) {
                Log.e("error", error.toString())
                return@sendRequest
            }

            // log the response to check that every thing is fine
            Log.d("up" , response.toString())
            fetchAndDisplayDisplayValue()
        }
    }

    fun onDownButtonClick(view: View) {
        ApiClient.sendRequest("/down") { response, error ->
            if (error != null) {
                Log.e("error", error.toString())
                return@sendRequest
            }

            // log the response to check that every thing is fine
            Log.d("down" , response.toString())
            fetchAndDisplayDisplayValue()
        }
    }

    private fun fetchAndDisplayDisplayValue() {
        ApiClient.sendRequest("/get_display_value") { response, error ->
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
                val displayValue = jsonObject.getString("display_value")
                runOnUiThread {
                    findViewById<TextView>(R.id.displayValueTextView).text = displayValue
                }
                Log.d("display", response)
            } catch (e: Exception) {
                // Handle JSON parsing error
                Log.e("AzureConnectionTest", "Error parsing JSON", e)
            }
        }
    }

}
