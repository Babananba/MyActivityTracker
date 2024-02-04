package com.example.myactivitytracker

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object Database {
    val base = Firebase.database("https://my-activity-tracker-a87d3-default-rtdb.europe-west1.firebasedatabase.app/")
}