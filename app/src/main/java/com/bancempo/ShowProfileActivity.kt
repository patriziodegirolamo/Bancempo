package com.bancempo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.view.menu.MenuBuilder
import androidx.compose.material.Text

class ShowProfileActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Inflate the menu. this adds items to the action bar if it is present
        menuInflater.inflate(R.menu.menu_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_profile -> {
                editProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun editProfile() {
        val photo = findViewById<ImageView>(R.id.profile_pic)
        val fullName = findViewById<TextView>(R.id.full_name)
        val nickname = findViewById<TextView>(R.id.textViewNickname)
        val email = findViewById<TextView>(R.id.textViewEmail)
        val location = findViewById<TextView>(R.id.textViewLocation)
        val skills = findViewById<TextView>(R.id.textViewSkills)
        val description = findViewById<TextView>(R.id.textViewDescription)

        val i = Intent(this, EditProfileActivity::class.java).apply {
            putExtra("com.bancempo.FULLNAME", findViewById<TextView>(R.id.full_name).text.toString())
        }
        //startActivityForResult(i, 0)
        //startActivity(i)
    }
}