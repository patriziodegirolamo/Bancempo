package com.bancempo

import android.app.Activity
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
        val fullName = findViewById<TextView>(R.id.textViewFullName)
        val nickname = findViewById<TextView>(R.id.textViewNickname)
        val email = findViewById<TextView>(R.id.textViewEmail)
        val location = findViewById<TextView>(R.id.textViewLocation)
        val skills = findViewById<TextView>(R.id.textViewSkills)
        val description = findViewById<TextView>(R.id.textViewDescription)

        val i = Intent(this, EditProfileActivity::class.java)
        //i.putExtra("com.bancempo.PHOTO", photo.tag.toString())
        i.putExtra("com.bancempo.FULL_NAME", fullName.text.toString())
        i.putExtra("com.bancempo.NICKNAME", nickname.text.toString())
        i.putExtra("com.bancempo.EMAIL", email.text.toString())
        i.putExtra("com.bancempo.LOCATION", location.text.toString())
        i.putExtra("com.bancempo.SKILLS", skills.text.toString())
        i.putExtra("com.bancempo.DESCRIPTION", description.text.toString())

        startActivityForResult(i, 0)
        //startActivity(i)

        //newString= extras.getString(“STRING_I_NEED”);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //val photo = findViewById<ImageView>(R.id.profile_pic)
            //val fullName =
            findViewById<TextView>(R.id.textViewFullName).setText(data.getStringExtra("com.bancempo.FULL_NAME"))
            //val nickname =
            findViewById<TextView>(R.id.textViewNickname).setText(data.getStringExtra("com.bancempo.NICKNAME"))
            //val email =
            findViewById<TextView>(R.id.textViewEmail).setText(data.getStringExtra("com.bancempo.EMAIL"))
            //val location =
            findViewById<TextView>(R.id.textViewLocation).setText(data.getStringExtra("com.bancempo.LOCATION"))
            //val skills =
            findViewById<TextView>(R.id.textViewSkills).setText(data.getStringExtra("com.bancempo.SKILLS"))
            //val description =
            findViewById<TextView>(R.id.textViewDescription).setText(data.getStringExtra("com.bancempo.DESCRIPTION"))
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}