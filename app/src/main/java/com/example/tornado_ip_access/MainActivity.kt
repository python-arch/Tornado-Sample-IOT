package com.example.tornado_ip_access

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import LocationUtils
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.*
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.helper.widget.MotionEffect.TAG
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage



class MainActivity : AppCompatActivity() {

    // declare variables

    var connectedWifiName = ""
    private lateinit var passed_text : TextView
    private val WIFI_SETTINGS_REQUEST_CODE = 1001
    private lateinit var wifiManager: WifiManager
    private lateinit var button_connect: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_connect = findViewById(R.id.connect)
        passed_text = findViewById(R.id.passed)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // Check if the app has permission to access location

        Toast.makeText(this, "Checking permissions...", Toast.LENGTH_SHORT).show()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_WIFI_STATE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CHANGE_WIFI_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE
                ),
                PERMISSIONS_REQUEST_CODE
            )
        } else {
            Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
            openWifiSettings() // Only open settings if permissions are granted
        }


        val test_button:Button = findViewById(R.id.test_connect)

        test_button.setOnClickListener {
            // Send POST request
            val postData = mapOf("ssid" to "omarsamehsyam", "password" to "omar1996")
            APiClientWifi.sendRequest("POST", "config", postData) { responseBody, exception ->
                if (exception != null) {
                    // Display the response or handle it as needed
                    println(exception.toString())
                    return@sendRequest
                }

                // Process the response body
                if (responseBody != null) {
                    // Display the response or handle it as needed
                    println(responseBody)
                    val intent = Intent(this, Transition_page_select::class.java)
                    intent.putExtra("responseBody", responseBody)
                    startActivity(intent)
                }
            }
        }



    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with scanning for WiFi networks
                openWifiSettings()
            } else {
                Toast.makeText(this , "Error connecting to WIFI , permission denied" , Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WIFI_SETTINGS_REQUEST_CODE) {
            // The user has returned from Wi-Fi settings
            Toast.makeText(this, "Returned from Wi-Fi settings.", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Checking Wi-Fi connection...", Toast.LENGTH_SHORT).show()
                if (isConnectedToWifi()) {
                    // User is connected to a Wi-Fi network
                    // You can perform any additional actions here
                    setConnectedWifiName(this@MainActivity)

                    Toast.makeText(this, "Connected to ${connectedWifiName} Wi-Fi", Toast.LENGTH_SHORT)
                        .show()


                    // carry out the function to send the HTTP request
                    showNextScreenButton(connectedWifiName)
//                    // check if he is connected to valid SSID
//                    if (connectedWifiName.startsWith("A")) {
//                        // User is connected to a Wi-Fi network with the specified SSID prefix
//                        // Show a button to navigate to the next screen
//                        showNextScreenButton(connectedWifiName)
//                    } else {
//                        // User is not connected to a Wi-Fi network with the specified SSID prefix
//                        // Show a message asking the user to select the network again
//                        showNetworkSelectionMessage()
//                    }
                } else {
                    // User is not connected to a Wi-Fi network
                    Toast.makeText(this, "Not connected to Wi-Fi", Toast.LENGTH_SHORT).show()
                    // Optionally, you can prompt the user to connect to a Wi-Fi network again
                    openWifiSettings()
                }

        }
    }

    private fun showNetworkSelectionMessage() {
        passed_text.setText("Please Connect to Proper network! ")
        button_connect.setText("Open Wifi Settings Again!")
        button_connect.setOnClickListener {
            openWifiSettings()
        }
    }

    // modified the function to communicate with the esp32
    private fun showNextScreenButton(wifiname:String) {
        passed_text.setText("Connected to ${wifiname} ")
        button_connect.setText("Send and Connect WIFI credentials")
        button_connect.setOnClickListener {
            // Send GET request
            val getData = mapOf("key1" to "value1", "key2" to "value2")
            APiClientWifi.sendRequest("GET", "/get_data", getData) { responseBody, exception ->
                if (exception != null) {
                    // Handle the exception
                    return@sendRequest
                }

                // Process the response body
                if (responseBody != null) {
                    // Display the response or handle it as needed
                    println(responseBody)
                    Toast.makeText(this , responseBody , Toast.LENGTH_LONG).show()
                }
            }

        }
    }


    /**
     * This method will set the connected wifi name for all phones
     * @see connectedWifiName variable
     * @param mContext is the current fragment context ..
     * must be called when permission is granted ..
     * @see [https://developer.android.com/reference/kotlin/android/net/wifi/WifiManager#getConnectionInfo()]
     */
    private fun setConnectedWifiName(mContext : Context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val request = NetworkRequest.Builder()
//                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//                .build()
//            val connManager =
//                mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//            connManager.registerNetworkCallback(request, setCallBack())
//        } else {
            setWifiName(mContext)
//        }

    }

    /**
     * This method will only be called for new phones from s/31/And -12 -- Snow Cone
     * required ...  ACCESS_NETWORK_STATE and ACCESS_FINE_LOCATION permissions
     * need to pass FLAG_INCLUDE_LOCATION_INFO to NetworkCallback(), otherwise you will get "unknow ssid" only
     * @see [https://developer.android.com/reference/kotlin/android/net/wifi/WifiManager#getConnectionInfo()]
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun setCallBack(): ConnectivityManager.NetworkCallback {
        val networkCallback =
            object : ConnectivityManager.NetworkCallback(
                FLAG_INCLUDE_LOCATION_INFO
            ) {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    setUpWifiName(networkCapabilities.transportInfo as WifiInfo)
                }
            }

        return networkCallback

    }

    /**
     * This method will only be called --- below s/31/And -12 -- Snow Cone
     * @param mContext is current fragment context
     * ACCESS_WIFI_STATE  is required
     * ACCESS_COARSE_LOCATION with ACCESS_FINE_LOCATION for some specific version --->= 28/0/Oreo
     * @see [https://developer.android.com/develop/connectivity/wifi/wifi-scan#wifi-scan-permissions] for reference ..
     */
    @Suppress("DEPRECATION")
    private fun setWifiName(mContext : Context) {
        val wifiManager = mContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo =
            wifiManager.connectionInfo

        if (wifiInfo?.supplicantState == SupplicantState.COMPLETED) {
            setUpWifiName(wifiInfo)
        }
    }



    /**
     * Setting exact wifi name from here ..
     * @param wifiInfo is instance of WifiInfo ..
     */
    private fun setUpWifiName(wifiInfo: WifiInfo){
        connectedWifiName = wifiInfo.ssid.replace("\"", "")
    }
    private fun isConnectedToWifi(): Boolean {
        var result = false
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        else -> false
                    }

                }
            }
        }

        return result

    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 1001
    }


    private fun openWifiSettings() {
        val wifiSettingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
        startActivityForResult(wifiSettingsIntent, WIFI_SETTINGS_REQUEST_CODE)
    }

}