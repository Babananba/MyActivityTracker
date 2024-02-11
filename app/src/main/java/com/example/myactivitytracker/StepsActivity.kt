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

    private lateinit var stepsFragment: StepsFragment
    private lateinit var mapFragment: MapFragment
    private var showingMap = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStepsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stepsFragment = StepsFragment()
        mapFragment = MapFragment()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom,
            androidx.appcompat.R.anim.abc_shrink_fade_out_from_bottom,
        )
        transaction.add(R.id.mainFrameLayout, mapFragment)
        transaction.add(R.id.mainFrameLayout, stepsFragment)
        transaction.commit()

        binding.logoutButton.setOnClickListener(this::onLogoutButton)
        binding.switchButton.setOnClickListener (this::onSwitchButton)
        binding.profileButton.setOnClickListener(this::onProfileButton)
    }

    private fun onLogoutButton(view: View) {
        Firebase.auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onSwitchButton(view: View) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom,
            androidx.appcompat.R.anim.abc_shrink_fade_out_from_bottom,
        )
        transaction.hide(if (!showingMap) stepsFragment else mapFragment)
        transaction.show(if (showingMap) stepsFragment else mapFragment)
        transaction.commit()
        showingMap = !showingMap
    }

    private fun onProfileButton(view: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}