package com.example.tornado_ip_access

import com.google.gson.annotations.SerializedName
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

public class ApiClient {
    companion object {
        private const val BASE_URL = "http://10.0.2.2:5000"

        fun sendRequest(endpoint: String, callback: (String?, Exception?) -> Unit) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(BASE_URL + endpoint)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null, e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    callback(responseBody, null)
                }
            })
        }

        fun sendChangeRequest(endpoint: String, features:String,mode:String,plasma:String,fan:String,h_louvre:String,v_louvre:String, timer_state:String, timer_hours:String , temp_user:String, callback: (String?, Exception?) -> Unit) {
            val client = OkHttpClient()

            val json = JSONObject().apply {
                put("features", features)
                put("mode", mode)
                put("plasma", plasma)
                put("fan", fan)
                put("h_louvre", h_louvre)
                put("v_louvre", v_louvre)
                put("timer_state", timer_state)
                put("timer_hours", timer_hours)
                put("temp_user", temp_user)
            }

            val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            val request = Request.Builder()
                .url(BASE_URL + endpoint)
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null, e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    callback(responseBody, null)
                }
            })
        }
    }
}
