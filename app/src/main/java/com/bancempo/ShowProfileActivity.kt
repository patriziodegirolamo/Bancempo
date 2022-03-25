package com.bancempo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.Text

class ShowProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.activity_show_profile)
        val photo = R.string.app_name
        val fullName = R.string.full_name
        val nickname = R.string.nickname
        val email = R.string.email
        val location = R.string.location
        val skills = R.string.skills
        val description = R.string.description
    }
}