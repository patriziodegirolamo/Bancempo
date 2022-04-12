package com.bancempo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class ShowProfileActivity : AppCompatActivity() {
    lateinit var fullName : TextView;
    lateinit var photo : ImageView
    lateinit var nickname : TextView
    lateinit var email : TextView
    lateinit var location : TextView
    lateinit var skills : TextView
    lateinit var description : TextView

    lateinit var orientation : String
    var w: Int = 0
    var h: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)


        fullName = findViewById<TextView>(R.id.textViewFullName)
        photo = findViewById<ImageView>(R.id.profile_pic)
        nickname = findViewById<TextView>(R.id.textViewNickname)
        email = findViewById<TextView>(R.id.textViewEmail)
        location = findViewById<TextView>(R.id.textViewLocation)
        skills = findViewById<TextView>(R.id.textViewSkills)
        description = findViewById<TextView>(R.id.textViewDescription)

        val orientation: Int = this.resources.configuration.orientation


        loadImageFromStorage("/data/user/0/com.bancempo/app_imageDir")

        if (savedInstanceState != null) {


            fullName.text = savedInstanceState.getString("full_name");
            nickname.text = savedInstanceState.getString("nickname");
            email.text = savedInstanceState.getString("email");
            location.text = savedInstanceState.getString("location");
            skills.text = savedInstanceState.getString("skills")
            description.text = savedInstanceState.getString("description")
            println("restoring from instance state")
        }

        else{
            val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
            val stringJSON:String? = sharedPref.getString("bancempoJSON", "")
            var jObject: JSONObject? = null;
            if(stringJSON != null && stringJSON != "") {
                jObject = JSONObject(stringJSON);
                fullName.text = jObject.getString(getString(R.string.full_name));
                nickname.text = jObject.getString(getString(R.string.nickname));
                email.text = jObject.getString(getString(R.string.email));
                location.text = jObject.getString(getString(R.string.location));
                skills.text = jObject.getString(getString(R.string.skills));
                description.text = jObject.getString(getString(R.string.description));
            }
            println("loading from sharedPrefs");
        }



    }

    @SuppressLint("SdCardPath")
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("full_name", fullName.text.toString())
        outState.putString("nickname", nickname.text.toString())
        outState.putString("email", email.text.toString())
        outState.putString("location", location.text.toString())
        outState.putString("skills", skills.text.toString())
        outState.putString("description", description.text.toString())

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
        val i = Intent(this, EditProfileActivity::class.java)
        i.putExtra("com.bancempo.PHOTO", EditProfileActivity().encodeTobase64(photo.drawToBitmap()))
        i.putExtra("com.bancempo.FULL_NAME", fullName.text.toString())
        i.putExtra("com.bancempo.NICKNAME", nickname.text.toString())
        i.putExtra("com.bancempo.EMAIL", email.text.toString())
        i.putExtra("com.bancempo.LOCATION", location.text.toString())
        i.putExtra("com.bancempo.SKILLS", skills.text.toString())
        i.putExtra("com.bancempo.DESCRIPTION", description.text.toString())

        startActivityForResult(i, 0)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        println("RETURN TO SHOW");
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            loadImageFromStorage("/data/user/0/com.bancempo/app_imageDir")

            fullName.text = data.getStringExtra("com.bancempo.FULL_NAME")
            nickname.text = data.getStringExtra("com.bancempo.NICKNAME")
            email.text = data.getStringExtra("com.bancempo.EMAIL")
            location.text = data.getStringExtra("com.bancempo.LOCATION")
            skills.text = data.getStringExtra("com.bancempo.SKILLS")
            description.text = data.getStringExtra("com.bancempo.DESCRIPTION")

            val jObject = JSONObject()
            jObject.put(getString(R.string.full_name), fullName.text.toString())
            jObject.put(getString(R.string.nickname), nickname.text.toString());
            jObject.put(getString(R.string.email), email.text.toString());
            jObject.put(getString(R.string.location), location.text.toString());
            jObject.put(getString(R.string.skills), skills.text.toString());
            jObject.put(getString(R.string.description), description.text.toString());

            //save all the textviews in the shared_preferences file
            val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
            with (sharedPref.edit()) {
                putString("bancempoJSON", jObject.toString());
                apply()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /*------------------------------------  UTILITIES  -------------------------------------------*/


    private fun loadImageFromStorage(path: String) {
        try {
            val f = File(path, "profile.jpeg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            val img = findViewById<ImageView>(R.id.profile_pic)
            img.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }
}