package com.example.tornado_ip_access


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var tvRedirectSignUp: TextView
    lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    lateinit var btnLogin: Button

    // Creating firebaseAuth object
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // View Binding
        tvRedirectSignUp = findViewById(R.id.tvRedirectSignUp)
        btnLogin = findViewById(R.id.btnLogin)
        etEmail = findViewById(R.id.etEmailAddress)
        etPass = findViewById(R.id.etPassword)

        // initialising Firebase auth object
        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
           login()
        }


        tvRedirectSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            // using finish() to end the activity
            finish()
            startActivity(intent)
        }
    }

    private fun login() {
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()
        // calling signInWithEmailAndPassword(email, pass)
        // function using Firebase auth object
        // On successful response Display a Toast
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    val userRef = FirebaseDatabase.getInstance().getReference("users/${user.uid}")
                    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val emailVerified = dataSnapshot.child("emailVerified").getValue(Boolean::class.java)
                            if (emailVerified == true) {
                                startActivity(Intent(this@LoginActivity , After_Login::class.java))
                            } else {
                                startActivity(Intent(this@LoginActivity , Verify_Email::class.java))
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                           Toast.makeText(this@LoginActivity , databaseError.toString(), Toast.LENGTH_LONG).show()
                        }
                    })
                } else {
                    // User is not signed in
                    // Handle this case as needed
                }
                finish()
                startActivity(Intent(this , SignUpActivity::class.java))
            } else
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
        }
    }

}
