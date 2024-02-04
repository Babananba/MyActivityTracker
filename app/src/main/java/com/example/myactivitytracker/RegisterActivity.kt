package com.example.myactivitytracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myactivitytracker.databinding.ActivityRegisterBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener(this::onRegisterButton)
        binding.switchToLoginButton.setOnClickListener(this::onSwitchToLoginButton)
    }

    private fun onRegisterButton(view: View){
        val username: String = binding.usernameEditText.text.toString()
        val email: String = binding.emailEditText.text.toString()
        val password: String = binding.passwordEditText.text.toString()
        val confirmPassword: String = binding.confirmPasswordEditText.text.toString()
        
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            return 
        }
        
        if (password != confirmPassword){
            Toast.makeText(this, "Passwords need to match!", Toast.LENGTH_LONG).show()
            return 
        }
        
        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this, "Authentication failed. ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun onSwitchToLoginButton(view: View){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()

    }
}