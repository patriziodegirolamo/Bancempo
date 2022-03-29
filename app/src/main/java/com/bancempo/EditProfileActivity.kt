package com.bancempo

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        //val photo = findViewById<ImageView>(R.id.profile_pic).setImageURI(intent.getStringExtra("com.bancempo.PHOTO"))
        val fullName = findViewById<TextView>(R.id.editTextFullName).setText(intent.getStringExtra("com.bancempo.FULL_NAME"))
        val nickname = findViewById<TextView>(R.id.editTextNickname).setText(intent.getStringExtra("com.bancempo.NICKNAME"))
        val email = findViewById<TextView>(R.id.editTextEmail).setText(intent.getStringExtra("com.bancempo.EMAIL"))
        val location = findViewById<TextView>(R.id.editTextLocation).setText(intent.getStringExtra("com.bancempo.LOCATION"))
        val skills = findViewById<TextView>(R.id.editTextSkills).setText(intent.getStringExtra("com.bancempo.SKILLS"))
        val description = findViewById<TextView>(R.id.editTextDescription).setText(intent.getStringExtra("com.bancempo.DESCRIPTION"))

        val editPicture = findViewById<ImageButton>(R.id.changeImageButton)
        registerForContextMenu(editPicture)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_profile_picture, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        //val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        return when (item.itemId) {
            R.id.select_image_gallery -> {
                //editNote(info.id)
                true
            }
            R.id.use_camera -> {
                //deleteNote(info.id)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val i = Intent(this, ShowProfileActivity::class.java)
        //i.putExtra("com.bancempo.PHOTO", findViewById<ImageView>(R.id.profile_pic).tag.toString())
        i.putExtra("com.bancempo.FULL_NAME", findViewById<TextView>(R.id.editTextFullName).text.toString())
        i.putExtra("com.bancempo.NICKNAME", findViewById<TextView>(R.id.editTextNickname).text.toString())
        i.putExtra("com.bancempo.EMAIL", findViewById<TextView>(R.id.editTextEmail).text.toString())
        i.putExtra("com.bancempo.LOCATION", findViewById<TextView>(R.id.editTextLocation).text.toString())
        i.putExtra("com.bancempo.SKILLS", findViewById<TextView>(R.id.editTextSkills).text.toString())
        i.putExtra("com.bancempo.DESCRIPTION", findViewById<TextView>(R.id.editTextDescription).text.toString())
        setResult(Activity.RESULT_OK, i)

        super.onBackPressed()
    }
}