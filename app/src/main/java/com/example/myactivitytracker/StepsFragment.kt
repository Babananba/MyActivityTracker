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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.max

class StepsFragment : Fragment(), SensorEventListener {
    private lateinit var binding: FragmentStepsBinding

    private var sensorManager: SensorManager? = null

    private var running = false
    private var steps = 0
    private var lastUpdatedSteps: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStepsBinding.inflate(inflater, container, false)
        binding.stepCountTextView.setOnClickListener {
            Toast.makeText(context, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }
        binding.stepCountTextView.setOnLongClickListener{
            steps = 0
            binding.stepCountTextView.text = "0"
            Firebase.auth.uid?.let { userID ->
                Database.base.getReference("$userID/steps/${getCurrentDate()}").setValue(0)
                lastUpdatedSteps = 0
            }
            true
        }
        sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        Firebase.auth.uid?.let { userID ->
            Database.base.getReference("$userID/steps").get().addOnSuccessListener {
                it.getValue<HashMap<String, Int>>()?.let {
                    var containsToday = false
                    var dates = it.toList().sortedByDescending { it.first }
                    dates.find { it.first == getCurrentDate() }?.let {
                        steps = it.second
                        binding.stepCountTextView.text = "$steps"
                        containsToday = true
                    }
                    if (containsToday){
                        dates = dates.subList(1,dates.size)
                    }
                    if (dates.size >= 2){
                        binding.yesterdayTitleTextView.text = "Steps ${dates[0].first}"
                        binding.yesterdayStepsTextView.text = "${dates[0].second}"
                        binding.dayBeforeYesterdayTitleTextView.text = "Steps ${dates[1].first}"
                        binding.dayBeforeYesterdayStepsTextView.text = "${dates[1].second}"
                    }
                    else if(dates.size == 1){
                        binding.yesterdayTitleTextView.text = "Steps ${dates[0].first}"
                        binding.yesterdayStepsTextView.text = "${dates[0].second}"
                    }
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

    private fun savePreviousSteps(count: Int){
        val sharedPreferences = context?.getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putInt(APP_PREVIOUS_STEPS_KEY, count)
        editor?.apply()
    }

    private fun loadPreviousSteps(): Int? {
        val sharedPreferences = context?.getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return sharedPreferences?.getInt(APP_PREVIOUS_STEPS_KEY, 0)
    }

    private fun getCurrentDate(): String{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDateTime.now().format(formatter)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(running){
            val previousSteps = loadPreviousSteps() ?: return
            val currentSteps = event!!.values[0].toInt()
            savePreviousSteps(currentSteps)

            steps += max(currentSteps - previousSteps, 0)
            binding.stepCountTextView.text = "$steps"

            if(steps - lastUpdatedSteps >= 10){
                lastUpdatedSteps = steps
                Firebase.auth.uid?.let { userID ->
                    Database.base.getReference("$userID/steps/${getCurrentDate()}").setValue(steps)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    companion object {
        private const val APP_PREFERENCES_NAME = "MyActivityTrackerPreferences"
        private const val APP_PREVIOUS_STEPS_KEY = "PreviousSteps"
    }
}