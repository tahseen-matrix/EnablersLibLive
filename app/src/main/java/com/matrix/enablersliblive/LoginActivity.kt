package com.matrix.enablersliblive

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.matrix.enablersliblive.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (supportActionBar?.isShowing == true) {
            supportActionBar?.hide()
        }

        binding.apply {
            loginButton.setOnClickListener {
                val username = usernameEditText.text.toString()
                val password = passwordEditText.text.toString()
                val allowedEmails =
                    listOf("user1@adopshun.com", "user2@adopshun.com", "default@adopshun.com")
                // Map usernames to user IDs
                val userIdMap = mapOf(
                    "user1@adopshun.com" to 401,
                    "user2@adopshun.com" to 402,
                    "default@adopshun.com" to 403
                )

                when {
                    username.isEmpty() && password.isNotEmpty() -> {
                        showAlertDialog(this@LoginActivity, "Please enter email") {

                        }
                    }
                    username.isNotEmpty() && password.isEmpty() -> {
                        showAlertDialog(this@LoginActivity, "Please enter password") {

                        }
                    }
                    username.isEmpty() && password.isEmpty() -> {
                        showAlertDialog(this@LoginActivity, "Please enter login credential.") {

                        }
                    }
                    username.isNotEmpty() && password.isNotEmpty() -> {
                        // Start new activity
                        if (allowedEmails.contains(username) && password == "123456") {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            MainActivity.selectedIdList = username
                            MainActivity.userId =
                                userIdMap[username] ?: -1 // Default to -1 if not found
                            startActivity(intent)
                            finish()
                        } else {
                            showAlertDialog(this@LoginActivity, "Invalid email or password") {

                            }
                        }
                    }
                }
            }
        }

    }


    private fun showAlertDialog(
        context: Context,
        message: String,
        onPositiveClick: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Alert")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Ok") { _, _ ->
                // Call the provided positive button response
                onPositiveClick()
            }
            .show()
    }

}