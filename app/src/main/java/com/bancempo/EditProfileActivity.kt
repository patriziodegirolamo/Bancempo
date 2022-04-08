package com.bancempo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream


class EditProfileActivity : AppCompatActivity() {

    val REQUEST_IMAGE_CAPTURE = 1
    val SELECT_PICTURE = 200
    var bitmap_photo :Bitmap? = null;
    var uri_photo :Uri? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val byte_array = intent.getByteArrayExtra("com.bancempo.PHOTO");

        if(byte_array != null){
            //val photo = findViewById<ImageView>(R.id.profile_pic).setImageBitmap(decodeBase64(intent.getStringExtra("com.bancempo.PHOTO")))
            val photo = findViewById<ImageView>(R.id.profile_pic).setImageBitmap(BitmapFactory.decodeByteArray(byte_array, 0, byte_array.size))

        }

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

    @SuppressLint("WrongThread")
    override fun onBackPressed() {
        val i = Intent(this, ShowProfileActivity::class.java)

        //ENCODE bitmap
        if (bitmap_photo != null){
            println(bitmap_photo);
            val stream = ByteArrayOutputStream()
            bitmap_photo?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray();
            i.putExtra("com.bancempo.PHOTO", byteArray);
            i.putExtra("com.bancempo.PHOTO_PROFILE", "bitmap");
        }
        else if (uri_photo != null){
            i.putExtra("com.bancempo.PHOTO", uri_photo.toString());
            i.putExtra("com.bancempo.PHOTO_PROFILE", "uri");
            println("URI");
        }
        else{
            i.putExtra("com.bancempo.PHOTO_PROFILE", "no");
            println("nothing");
        }

        i.putExtra("com.bancempo.FULL_NAME", findViewById<TextView>(R.id.editTextFullName).text.toString())
        i.putExtra("com.bancempo.NICKNAME", findViewById<TextView>(R.id.editTextNickname).text.toString())
        i.putExtra("com.bancempo.EMAIL", findViewById<TextView>(R.id.editTextEmail).text.toString())
        i.putExtra("com.bancempo.LOCATION", findViewById<TextView>(R.id.editTextLocation).text.toString())
        i.putExtra("com.bancempo.SKILLS", findViewById<TextView>(R.id.editTextSkills).text.toString())
        i.putExtra("com.bancempo.DESCRIPTION", findViewById<TextView>(R.id.editTextDescription).text.toString())


        println("pressing back button")
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
            bitmap_photo = data.extras?.get("data") as Bitmap;
            findViewById<ImageView>(R.id.profile_pic).setImageBitmap(bitmap_photo)

        } else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null){
            // Get the url of the image from data
            uri_photo = data.data
            // update the preview image in the layout
            findViewById<ImageView>(R.id.profile_pic).setImageURI(uri_photo)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /*------------------------------------  UTILITIES  -------------------------------------------*/
    fun encodeTobase64(image: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        val imageEncoded: String = Base64.encodeToString(b, Base64.DEFAULT)
        Log.d("Image Log:", imageEncoded)
        return imageEncoded
    }

    fun decodeBase64(input: String?): Bitmap? {
        val decodedByte: ByteArray = Base64.decode(input, 0)
        return BitmapFactory
            .decodeByteArray(decodedByte, 0, decodedByte.size)
    }


}

