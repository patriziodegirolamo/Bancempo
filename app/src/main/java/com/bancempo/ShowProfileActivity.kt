package com.bancempo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.view.menu.MenuBuilder
import androidx.compose.material.Text

class ShowProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)
        val photo = R.string.app_name
        val fullName = R.string.full_name
        val nickname = R.string.nickname
        val email = R.string.email
        val location = R.string.location
        val skills = R.string.skills
        val description = R.string.description
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Inflate the menu. this adds items to the action bar if it is present
        menuInflater.inflate(R.menu.menu_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_profile -> {
                val text = "Hello toast!"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}