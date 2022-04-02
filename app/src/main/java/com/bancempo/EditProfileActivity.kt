package com.bancempo

import android.R.attr.button
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.ContextMenu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult


class EditProfileActivity : AppCompatActivity() {

    val REQUEST_IMAGE_CAPTURE = 1
    val SELECT_PICTURE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        //val photo = findViewById<ImageView>(R.id.profile_pic).setImageURI(intent.getStringExtra("com.bancempo.PHOTO"))
        val fullName =
            findViewById<TextView>(R.id.editTextFullName).setText(intent.getStringExtra("com.bancempo.FULL_NAME"))
        val nickname =
            findViewById<TextView>(R.id.editTextNickname).setText(intent.getStringExtra("com.bancempo.NICKNAME"))
        val email =
            findViewById<TextView>(R.id.editTextEmail).setText(intent.getStringExtra("com.bancempo.EMAIL"))
        val location =
            findViewById<TextView>(R.id.editTextLocation).setText(intent.getStringExtra("com.bancempo.LOCATION"))
        val skills =
            findViewById<TextView>(R.id.editTextSkills).setText(intent.getStringExtra("com.bancempo.SKILLS"))
        val description =
            findViewById<TextView>(R.id.editTextDescription).setText(intent.getStringExtra("com.bancempo.DESCRIPTION"))

        val editPicture = findViewById<ImageButton>(R.id.changeImageButton)
        editPicture.setOnClickListener {
            showPopup(editPicture)
        }
    }

    private fun showPopup(v: View) {
        val popup = PopupMenu(this, v)
        popup.menuInflater.inflate(R.menu.menu_profile_picture, popup.menu)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.select_image_gallery -> {
                    val i = Intent(Intent.ACTION_GET_CONTENT)
                    i.type = "image/*"
                    // pass the constant to compare it with the returned requestCode
                    startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE)
                    true
                }
                R.id.use_camera -> {
                    dispatchTakePictureIntent()
                    true
                }
            }
            false
        })

        popup.show()
    }

//    override fun onCreateContextMenu(menu: ContextMenu, v: View,
//                                     menuInfo: ContextMenu.ContextMenuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo)
//        val inflater: MenuInflater = menuInflater
//        inflater.inflate(R.menu.menu_profile_picture, menu)
//    }



//    override fun onContextItemSelected(item: MenuItem): Boolean {
//        //val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
//        return when (item.itemId) {
//            R.id.select_image_gallery -> {
//                val i = Intent(Intent.ACTION_GET_CONTENT)
//                i.type = "image/*"
//                // pass the constant to compare it with the returned requestCode
//                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE)
//                true
//            }
//            R.id.use_camera -> {
//                dispatchTakePictureIntent()
//                true
//            }
//            else -> super.onContextItemSelected(item)
//        }
//    }

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

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            Toast.makeText(applicationContext, R.string.camera_not_available, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            val imageBitmap = data.extras?.get("data") as Bitmap
            findViewById<ImageView>(R.id.profile_pic).setImageBitmap(imageBitmap)
        } else if (requestCode == SELECT_PICTURE){
            //val imageBitmap = data.extras?.get("data") as Bitmap
            //findViewById<ImageView>(R.id.profile_pic).setImageBitmap(imageBitmap)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}