package com.jaribu.wekatuu

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var signUp: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var buttonLogin: Button
    private lateinit var userEmailEtv: TextView
    private lateinit var userPasswordLog: TextView
    private lateinit var showPasswordCheckbox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Check if user is already signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already signed in, start MainActivity and finish Login activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        signUp = findViewById(R.id.textViewSignup)
        userEmailEtv = findViewById(R.id.editTextLogin)
        userPasswordLog = findViewById(R.id.editTextPasswordLogin)
        buttonLogin = findViewById(R.id.buttonLogin)
        showPasswordCheckbox = findViewById(R.id.showPasswordCheckbox)

        signUp.setOnClickListener {
            val goToSignUpMenu = Intent(this, Signup::class.java)
            startActivity(goToSignUpMenu)
        }

        buttonLogin.setOnClickListener {
            val userEmail = userEmailEtv.text.toString().trim()
            val userPassword = userPasswordLog.text.toString().trim()

            if (userEmail.isEmpty()) {
                userEmailEtv.error = "Email is required"
                userEmailEtv.requestFocus()
                return@setOnClickListener
            }

            if (!isValidEmail(userEmail)) {
                userEmailEtv.error = "Invalid email format"
                userEmailEtv.requestFocus()
                return@setOnClickListener
            }

            if (userPassword.isEmpty()) {
                userPasswordLog.error = "Password is required"
                userPasswordLog.requestFocus()
                return@setOnClickListener
            }

            // Call login function to authenticate user
            login(userEmail, userPassword)
        }

        showPasswordCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                userPasswordLog.transformationMethod = null
            } else {
                userPasswordLog.transformationMethod = PasswordTransformationMethod()
            }
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login success, start MainActivity and finish Login activity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Login failed, show error message
                    Toast.makeText(
                        this, "Authentication failed. " + task.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Regex("[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+")
        return email.matches(emailPattern)
    }

}
