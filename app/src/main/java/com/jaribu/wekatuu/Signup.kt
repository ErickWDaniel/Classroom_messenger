package com.jaribu.wekatuu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class Signup : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var buttonSignUp: Button
    private lateinit var userEmailEtv: TextView
    private lateinit var userPasswordLog: TextView
    private lateinit var admissionNumber: TextView
    private lateinit var nameSignup: TextView
    private lateinit var phoneNumberSignup: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mAuth = FirebaseAuth.getInstance()

        nameSignup = findViewById(R.id.editTextNameSignup)
        admissionNumber = findViewById(R.id.editTextAdmissionNumberSignUp)
        userEmailEtv = findViewById(R.id.editTextEmailSignup)
        phoneNumberSignup = findViewById(R.id.editTextPhoneNumberSignup)
        userPasswordLog = findViewById(R.id.editTextPasswordSignup)
        buttonSignUp = findViewById(R.id.buttonSingUp)

        buttonSignUp.setOnClickListener {
            val userName = nameSignup.text.toString().trim()
            val userAdminStr = admissionNumber.text.toString().trim()
            val userEmail = userEmailEtv.text.toString().trim()
            val userPhoneStr = phoneNumberSignup.text.toString().trim()
            val userPassword = userPasswordLog.text.toString().trim()

            if (userName.isEmpty()) {
                nameSignup.error = "Name is required."
                nameSignup.requestFocus()
            } else if (userAdminStr.isEmpty()) {
                admissionNumber.error = "Admission number is required."
                admissionNumber.requestFocus()
            } else if (!userEmail.matches(Regex("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b"))) {
                userEmailEtv.error = "Invalid email format."
                userEmailEtv.requestFocus()
            } else if (!userPhoneStr.matches(Regex("^\\d{10}$"))) {
                phoneNumberSignup.error = "Phone number must be 10 digits start with 0."
                phoneNumberSignup.requestFocus()
            } else if (userPassword.isEmpty()) {
                userPasswordLog.error = "Password is required."
                userPasswordLog.requestFocus()
            } else {
                val userAdmin = userAdminStr.toInt()
                val userPhone = userPhoneStr.toInt()
                signup(userName, userAdmin, userEmail, userPhone, userPassword)
            }
        }
    }
    private fun signup(
        username: String,
        userAdmin: Int,
        userEmail: String,
        userPhone: Int,
        userPass: String
    ) {
        mAuth.createUserWithEmailAndPassword(userEmail, userPass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                val userData = hashMapOf(
                                    "name" to username,
                                    "admission_number" to userAdmin,
                                    "phone_number" to userPhone,
                                    "email" to userEmail
                                )
                                val db = FirebaseFirestore.getInstance()
                                db.collection("users").document(user.uid)
                                    .set(userData)
                                    .addOnSuccessListener {

                                        // Clear all fields
                                        nameSignup.text=""
                                        admissionNumber.text=""
                                        userEmailEtv.text=""
                                        phoneNumberSignup.text=""
                                        userPasswordLog.text=""
                                        Toast.makeText(this,"ACCOUNT CREATED",Toast.LENGTH_SHORT).show()
                                        // Registration successful, navigate to the main activity
                                        val intent = Intent(this, Login::class.java)
                                        startActivity(intent)
                                        finish()


                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this,
                                            "Registration failed:\n Error: ${e.message}".uppercase(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        nameSignup.text=""
                                        admissionNumber.text=""
                                        userEmailEtv.text=""
                                        phoneNumberSignup.text=""
                                        userPasswordLog.text=""
                                        val intent = Intent(this, Login::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Profile update failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                                nameSignup.text=""
                                admissionNumber.text=""
                                userEmailEtv.text=""
                                phoneNumberSignup.text=""
                                userPasswordLog.text=""
                                val intent = Intent(this, Login::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                } else {
                    Toast.makeText(
                        this,
                        "Registration failed:\nError:${task.exception?.message}".uppercase(),
                        Toast.LENGTH_SHORT
                    ).show()
                    nameSignup.text=""
                    admissionNumber.text=""
                    userEmailEtv.text=""
                    phoneNumberSignup.text=""
                    userPasswordLog.text=""
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    finish()
                }
            }
    }

}
