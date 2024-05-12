package com.example.tornado_ip_access

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

public class APiClientWifi {
    companion object {
        private const val BASE_URL = "http://192.168.4.1"

        fun sendRequest(method: String, endpoint: String, data: Map<String, String>, callback: (String?, Exception?) -> Unit) {
            val client = OkHttpClient()

            val urlBuilder = "$BASE_URL/$endpoint".toHttpUrlOrNull()?.newBuilder()
            val url = urlBuilder?.build()

            val json = Gson().toJson(data)
            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(url!!)
                .method(method, requestBody)
                .addHeader("Content-Type", "application/json")
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
