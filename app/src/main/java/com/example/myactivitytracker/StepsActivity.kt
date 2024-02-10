package com.example.myactivitytracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

        binding.logoutButton.setOnClickListener(this::onLogoutButton)
        binding.switchButton.setOnClickListener (this::onSwitchButton)
        binding.profileButton.setOnClickListener(this::onProfileButton)

        setFragment(stepsFragment)
    }

    private fun onLogoutButton(view: View) {
        Firebase.auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onSwitchButton(view: View) {
        setFragment(if (showingMap) stepsFragment else mapFragment)
        showingMap = !showingMap
    }

    private fun onProfileButton(view: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom,
            androidx.appcompat.R.anim.abc_shrink_fade_out_from_bottom,
        )
        transaction.replace(R.id.mainFrameLayout, fragment)
        transaction.commit()
    }
}