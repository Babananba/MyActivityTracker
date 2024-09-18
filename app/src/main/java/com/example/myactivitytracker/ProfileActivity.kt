package com.example.myactivitytracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myactivitytracker.databinding.ActivityProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveButton.isEnabled = false
        binding.saveButton.setOnClickListener {
            saveHeightAndWeight()
        }

        Firebase.auth.uid?.let { userID ->
            Database.base.getReference("$userID/profile").get().addOnCompleteListener { task ->
                if (task.isSuccessful){
                    task.result.getValue<HashMap<String, String>>()?.let {
                        binding.heightEditText.setText(it["height"])
                        binding.weightEditText.setText(it["weight"])
                    }
                }
                else{
                    Toast.makeText(this, "Failed to get information", Toast.LENGTH_LONG).show()
                }
                binding.saveButton.isEnabled = true
            }
        }
    }

    private fun saveHeightAndWeight() {
        val height = binding.heightEditText.text.toString()
        val weight = binding.weightEditText.text.toString()

        if (height.isEmpty() || weight.isEmpty()) {
            Toast.makeText(this, "Please enter both height and weight", Toast.LENGTH_SHORT).show()
            return
        }

        Firebase.auth.uid?.let { userID ->
            val map = HashMap<String, String>()
            map["height"] = height
            map["weight"] = weight
            Database.base.getReference("$userID/profile").setValue(map).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val intent = Intent(this, StepsActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    Toast.makeText(this, "Failed to save information", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}