package com.example.myactivitytracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.myactivitytracker.databinding.ActivityStepsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class StepsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStepsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStepsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logoutButton.setOnClickListener(this::onLogoutButton)
    }

    private fun onLogoutButton(view: View) {
        Firebase.auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}