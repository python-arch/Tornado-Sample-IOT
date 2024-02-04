package com.example.tornado_ip_access
import LocationUtils
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.io.IOException
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.math.log

class SignUpActivity : AppCompatActivity() {
    private lateinit var btnSignUp: Button
    private lateinit var btnLogin: Button
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private  var location_granted = false
    private lateinit var locationManager: LocationManager
    private lateinit var locationUtils: LocationUtils

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationUtils = LocationUtils(this)

        btnSignUp = findViewById(R.id.register)
        btnLogin = findViewById(R.id.Sign_in)

        btnSignUp.setOnClickListener{
            startActivity(Intent(this , Transition_page_select::class.java))
        }

        btnLogin.setOnClickListener {
            startActivity(Intent(this , LoginActivity::class.java))
        }
        checkLocationPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Location permission granted
                // Call a function to get the user's location here
                // For simplicity, let's call a sample function
                getUserLocation()
            } else {
                // Location permission denied
                Toast.makeText(
                    this,
                    "Location permission denied. Some features may not work.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            getUserLocation()
        } else {
            // Permission is already granted
            // Call a function to get the user's location here
            // For simplicity, let's call a sample function
            getUserLocation()
            location_granted = true
            Toast.makeText(this, "User location obtained" , Toast.LENGTH_LONG).show()
        }
    }

    private fun getUserLocation() {
        // Check if the location permission is granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Get the user's location
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                5f,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        // Now, you can send the location via email
                        sendLocationViaEmail(latitude, longitude)
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    }

                    override fun onProviderEnabled(provider: String) {
                    }

                    override fun onProviderDisabled(provider: String) {
                    }
                }
            )
        } else {
            Toast.makeText(
                this,
                "Location permission denied. Unable to get the user's location.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun sendLocationViaEmail(latitude: Double, longitude: Double) {
        var mail ="abdelrahman.elsayed@ejust.edu.eg"
        var name = "Eng.Abdelrahman"

        GlobalScope.launch(Dispatchers.IO) {
            val subject = "User Location"
            val city = locationUtils.getCityFromLocation(latitude, longitude)
            val message = "Dear $name\n The user's location is '\n Latitude: $latitude\nLongitude: $longitude\n city:$city\n"

            val properties = System.getProperties()
            properties["mail.smtp.host"] = "smtp.gmail.com"
            properties["mail.smtp.port"] = "587"
            properties["mail.smtp.auth"] = "true"
            properties["mail.smtp.starttls.enable"] = "true"

            val session = Session.getInstance(properties, object : javax.mail.Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication("amra51548@gmail.com", "egyt ktfg qrmd gsdd")
                }
            })

            try {
                val mimeMessage = MimeMessage(session)
                mimeMessage.setFrom(InternetAddress("amra51548@gmail.com"))
                mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress(mail))
                mimeMessage.subject = subject
                mimeMessage.setText(message)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignUpActivity, "Sending email...", Toast.LENGTH_SHORT).show()
                }

                Transport.send(mimeMessage)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignUpActivity, "Location sent via email", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Email", "Error sending email", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignUpActivity, "Error sending location via email", Toast.LENGTH_SHORT).show()
                }
            }catch (e: IOException) {
                // Handle network issues
                Log.e("Email", "Network error", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignUpActivity, "Network error. Retrying...", Toast.LENGTH_SHORT).show()
                }

                // Retry with backoff delay
                delay(5000) // Delay for 5 seconds
                sendLocationViaEmail(latitude, longitude) // Recursive call to retry
            }
        }
    }
}
