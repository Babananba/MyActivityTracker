package com.example.myactivitytracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myactivitytracker.databinding.ActivityLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onStart() {
        super.onStart()

        val user = Firebase.auth.currentUser
        if (user != null){
            val intent = Intent(this, StepsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener(this::onLoginButton)
        binding.switchToRegisterButton.setOnClickListener(this::onSwitchToRegisterButton)
    }

    private fun onLoginButton(view: View){
        val email: String = binding.usernameEditText.text.toString()
        val password: String = binding.passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            return
        }

        Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val intent = Intent(this, StepsActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this, "Authentication failed. ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun onSwitchToRegisterButton(view: View){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}