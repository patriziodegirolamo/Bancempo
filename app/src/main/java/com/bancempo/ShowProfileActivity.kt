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

    var image:String = "";
    var uri:Uri? = null;

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

        if (savedInstanceState != null) {
            image = savedInstanceState.getString("image").toString();
            val photodaripristinare = savedInstanceState.getString("photo");
            println("___>$image");

            if(image == "bitmap"){
                println("saving bitmap")
                if (photodaripristinare != null) {
                    loadImageFromStorage(photodaripristinare)
                }
            }
            else if (image =="uri"){
                //println("saving uri ${Uri.parse(photodaripristinare)}")
                if (photodaripristinare != null) {
                    println("NUOVA FOTO DA RIPR $photodaripristinare")
                    loadImageFromStorage(photodaripristinare)
                }
                //photo.setImageURI(Uri.parse(photodaripristinare));
                //photo.setImageURI(Uri.parse(photodaripristinare));
                //photo.setImageURI(uri);
            }
            else{

            }
            fullName.text = savedInstanceState.getString("full_name");
            nickname.text = savedInstanceState.getString("nickname");
            email.text = savedInstanceState.getString("email");
            location.text = savedInstanceState.getString("location");
            skills.text = savedInstanceState.getString("skills")
            description.text = savedInstanceState.getString("description")
            //println("restoring from instance state")
        }

        else{
            loadImageFromStorage("/data/user/0/com.bancempo/app_imageDir")
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

            println("loading from sharedPrefs")
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
        outState.putString("image", image);

        //outState.putString("photo", )
        //val photo_profile = intent.getStringExtra("com.bancempo.PHOTO_PROFILE")
        if(image == "bitmap"){
            outState.putString("photo", "/data/user/0/com.bancempo/app_imageDir")
        }
        else if(image == "uri"){
            outState.putString("photo", uri.toString());
        }
        else {

        }
        println("PHOTO PROFILE: $image");
        //outState.putString("photo", encodeTobase64(photo.drawToBitmap()))
        //println("outstate" + outState.toString())
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

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            val photo_profile = data.getStringExtra("com.bancempo.PHOTO_PROFILE")

            if( photo_profile == "bitmap"){
                image = "bitmap";
                val photo = findViewById<ImageView>(R.id.profile_pic).setImageBitmap(EditProfileActivity().decodeBase64(data.getStringExtra("com.bancempo.PHOTO")))
            }
            else if (photo_profile == "uri"){
                image = "uri"
                //println("EXTRACTING URI");
                uri = Uri.parse(data.getStringExtra("com.bancempo.PHOTO"));
                val photo = findViewById<ImageView>(R.id.profile_pic).setImageURI(uri);
            }
            else{
                //println("EXTRACTING none");
            }


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

            val jObject = JSONObject()
            jObject.put(getString(R.string.full_name), findViewById<TextView>(R.id.textViewFullName).text.toString())
            jObject.put(getString(R.string.nickname), findViewById<TextView>(R.id.textViewNickname).text.toString());
            jObject.put(getString(R.string.email), findViewById<TextView>(R.id.textViewEmail).text.toString());
            jObject.put(getString(R.string.location), findViewById<TextView>(R.id.textViewLocation).text.toString());
            jObject.put(getString(R.string.skills), findViewById<TextView>(R.id.textViewSkills).text.toString());
            jObject.put(getString(R.string.description), findViewById<TextView>(R.id.textViewDescription).text.toString());


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