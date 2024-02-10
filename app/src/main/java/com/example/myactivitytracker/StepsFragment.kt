package com.example.myactivitytracker

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myactivitytracker.databinding.FragmentStepsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlin.math.max

class StepsFragment : Fragment(), SensorEventListener {
    private lateinit var binding: FragmentStepsBinding

    private var sensorManager: SensorManager? = null

    private var running = false
    private var currentStepCount = 0
    private var lastUpdatedSteps: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStepsBinding.inflate(inflater, container, false)
        binding.stepCountTextView.setOnClickListener {
            Toast.makeText(context, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }
        binding.stepCountTextView.setOnLongClickListener{
            currentStepCount = 0
            binding.stepCountTextView.text = "0"
            Firebase.auth.uid?.let { userID ->
                Database.base.getReference("$userID/steps").setValue(0)
            }
            true
        }
        sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        Firebase.auth.uid?.let { userID ->
            Database.base.getReference("$userID/steps").get().addOnSuccessListener {
                it.getValue<Int>()?.let {
                    currentStepCount = it
                    binding.stepCountTextView.text = "$it"

                }
            }
        }
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        running = true
        val stepSensor : Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if(stepSensor == null){
            Toast.makeText(context, "No sensor detected", Toast.LENGTH_SHORT).show()
        }
        else{
            sensorManager?.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun saveSteps(count: Int){
        val sharedPreferences = context?.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putInt("key1", count)
        editor?.apply()
    }

    private fun loadSteps(): Int{
        val sharedPreferences = context?.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getInt("key1", 0) ?: 0
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