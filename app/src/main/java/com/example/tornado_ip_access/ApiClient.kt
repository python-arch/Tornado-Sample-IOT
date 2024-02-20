package com.example.tornado_ip_access

import com.google.gson.annotations.SerializedName
import okhttp3.*
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
    }
}
