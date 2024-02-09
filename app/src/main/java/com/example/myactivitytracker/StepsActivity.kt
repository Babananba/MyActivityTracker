package com.example.myactivitytracker

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myactivitytracker.databinding.ActivityStepsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlin.math.max

class StepsActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityStepsBinding

    private var sensorManager: SensorManager? = null

    private var running = false
    private var currentStepCount = 0
    private var lastUpdatedSteps: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStepsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logoutButton.setOnClickListener(this::onLogoutButton)
        binding.profileButton.setOnClickListener(this::onProfileButton)

        binding.stepCountTextView.setOnClickListener {
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }
        binding.stepCountTextView.setOnLongClickListener{
            currentStepCount = 0
            binding.stepCountTextView.text = "0"
            Firebase.auth.uid?.let { userID ->
                Database.base.getReference("$userID/steps").setValue(0)
            }
            true
        }
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        Firebase.auth.uid?.let { userID ->
            Database.base.getReference("$userID/steps").get().addOnSuccessListener {
                it.getValue<Int>()?.let {
                    currentStepCount = it
                    binding.stepCountTextView.text = "$it"

                }
            }
        }

    }

    private fun onLogoutButton(view: View) {
        Firebase.auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onProfileButton(view: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        running = true
        val stepSensor : Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if(stepSensor == null){
            Toast.makeText(this, "No sensor detected", Toast.LENGTH_SHORT).show()
        }
        else{
            sensorManager?.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun saveSteps(count: Int){
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("key1", count)
        editor.apply()
    }

    private fun loadSteps(): Int{
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("key1", 0)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(running){
            val totalStepCount = event!!.values[0].toInt()
            currentStepCount += max(totalStepCount - loadSteps(), 0)
            saveSteps(totalStepCount)
            binding.stepCountTextView.text = "$currentStepCount"

            if(currentStepCount - lastUpdatedSteps >= 10){
                lastUpdatedSteps = currentStepCount
                Firebase.auth.uid?.let { userID ->
                    Database.base.getReference("$userID/steps").setValue(currentStepCount)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}