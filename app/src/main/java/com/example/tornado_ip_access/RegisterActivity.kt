package com.example.tornado_ip_access

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        auth = FirebaseAuth.getInstance()
        // define the buttons
        var register_button  = findViewById<Button>(R.id.btnReg)
        var email_address = findViewById<EditText>(R.id.email_address_register)
        var password_edit = findViewById<EditText>(R.id.password_reg)

        register_button.setOnClickListener {
            val email = email_address.text.toString() // Enter the user's email here
            val password = password_edit.text.toString() // Enter the user's password here

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registration successful
                        currentUser = auth.currentUser!!

                        showToast("Registration successful")

                    } else {
                        // Registration failed
                        showToast("Registration Failed")
                        // You can display an error message or handle the failure in another way
                    }
                }
        }

        var sendOtpButton = findViewById<TextView>(R.id.verify)
        var emailEditText = findViewById<EditText>(R.id.email_address_register)
        var verifyOtpButton = findViewById<Button>(R.id.btn_otp)
        var otpEditText = findViewById<EditText>(R.id.otp_edit_text)
        // Send OTP button click listener
        sendOtpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            currentUser = auth.currentUser!!
            // Send OTP via email
            sendEmailWithOTP(currentUser)

            // set visibility to seen
            verifyOtpButton.visibility = View.VISIBLE
            otpEditText.visibility = View.VISIBLE
        }



        // Verify OTP button click listener
        verifyOtpButton.setOnClickListener {
            val otp = otpEditText.text.toString()
            val email = emailEditText.text.toString()
            // Verify OTP
           verifyOTP(currentUser , otp){ isCorrect ->
               if (isCorrect) {
                   showToast("User is verified Sucessfully!")
                   startActivity(Intent(this , LoginActivity::class.java))
               } else {
                   // OTP is incorrect
                  showToast("User is not verified")
               }
           }
        }
    }

    fun generateOTP(): String {
        // Implement your OTP generation logic
        return (100000..999999).random().toString() // Generate a 6-digit OTP
    }


    fun sendEmailWithOTP(user: FirebaseUser) {
        val otp = generateOTP()

        // Save the OTP to the Realtime Database
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("otp/${user.uid.toString()}")
        ref.setValue(otp)

        // Send the email
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val properties = Properties()
                properties["mail.smtp.auth"] = "true"
                properties["mail.smtp.starttls.enable"] = "true"
                properties["mail.smtp.host"] = "smtp.gmail.com" // Use your SMTP server here
                properties["mail.smtp.port"] = "587" // Use the appropriate port for your SMTP server

                val username = "amra51548@gmail.com" // Your email address
                val password = "sdgf quhh iiyi utkg" // Your email password

                val session = Session.getInstance(properties, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(username, password)
                    }
                })

                val message = MimeMessage(session)
                message.setFrom(InternetAddress(username))
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.email))
                message.subject = "Your OTP"
                message.setText("Your OTP is: $otp")

                Transport.send(message)
            }catch (e: Exception) {
                Log.e("CoroutineError", "Exception in coroutine: ${e.message}")
            }
        }
    }

    fun verifyOTP(user: FirebaseUser, enteredOTP: String, callback: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("otp/${user.uid}")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val storedOTP = dataSnapshot.getValue(String::class.java)

                if (storedOTP == enteredOTP) {
                    // OTP is correct
                    val userRef = database.getReference("users/${user.uid}")
                    userRef.child("emailVerified").setValue(true)
                    callback(true)
                } else {
                    // OTP is incorrect
                    callback(false)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                callback(false)
            }
        })
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}

