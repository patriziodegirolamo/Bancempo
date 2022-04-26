package com.bancempo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.core.view.drawToBitmap
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class ShowProfileFragment : Fragment(R.layout.fragment_show_profile) {

    lateinit var fullName : TextView
    lateinit var photo : ImageView
    lateinit var nickname : TextView
    lateinit var email : TextView
    lateinit var location : TextView
    lateinit var skills : TextView
    lateinit var description : TextView

    lateinit var chipGroup : ChipGroup

    var w: Int = 0
    var h: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        fullName = view.findViewById<TextView>(R.id.textViewFullName)
        photo = view.findViewById<ImageView>(R.id.profile_pic)
        nickname = view.findViewById<TextView>(R.id.textViewNickname)
        email = view.findViewById<TextView>(R.id.textViewEmail)
        location = view.findViewById<TextView>(R.id.textViewLocation)
        skills = view.findViewById<TextView>(R.id.textViewSkills)
        description = view.findViewById<TextView>(R.id.textViewDescription)
        chipGroup = view.findViewById<ChipGroup>(R.id.chipGroup)


        val orientation: Int = this.resources.configuration.orientation



        loadImageFromStorage("/data/user/0/com.bancempo/app_imageDir")

        if (savedInstanceState != null) {


            fullName.text = savedInstanceState.getString("full_name")
            nickname.text = savedInstanceState.getString("nickname")
            email.text = savedInstanceState.getString("email")
            location.text = savedInstanceState.getString("location")
            description.text = savedInstanceState.getString("description")

            val skillsString = savedInstanceState.getString("skills")
            chipGroup.removeAllViews()
            if (skillsString != null && skillsString != "") {
                skillsString.split(",").forEach {
                    var chip = Chip(activity)
                    if(!it.isEmpty()) {
                        chip.setText(it)
                        chipGroup.addView(chip)
                    }
                }
            }
        }

        else{
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
            val stringJSON:String? = sharedPref.getString("bancempoJSON", "")
            var jObject: JSONObject? = null
            if(stringJSON != null && stringJSON != "") {
                jObject = JSONObject(stringJSON)
                fullName.text = jObject.getString(getString(R.string.full_name))
                nickname.text = jObject.getString(getString(R.string.nickname))
                email.text = jObject.getString(getString(R.string.email))
                location.text = jObject.getString(getString(R.string.location))
                description.text = jObject.getString(getString(R.string.description))
                val skillsString = jObject.getString(getString(R.string.skills))
                chipGroup.removeAllViews()
                if (skillsString != "") {
                    skillsString.split(",").forEach {
                        var chip = Chip(activity)
                        if(!it.isEmpty()) {
                            chip.setText(it)
                            chipGroup.addView(chip)
                        }
                    }
                }
            }
        }


    }

    @SuppressLint("SdCardPath")
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("full_name", fullName.text.toString())
        outState.putString("nickname", nickname.text.toString())
        outState.putString("email", email.text.toString())
        outState.putString("location", location.text.toString())
        outState.putString("description", description.text.toString())

        var chipText = ""
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            chipText += "${chip.text},"
        }
        outState.putString("skills", chipText)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_profile, menu)
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
        val i = Intent(activity, EditProfileActivity::class.java)
        i.putExtra("com.bancempo.PHOTO", encodeToBase64(photo.drawToBitmap()))
        i.putExtra("com.bancempo.FULL_NAME", fullName.text.toString())
        i.putExtra("com.bancempo.NICKNAME", nickname.text.toString())
        i.putExtra("com.bancempo.EMAIL", email.text.toString())
        i.putExtra("com.bancempo.LOCATION", location.text.toString())
        i.putExtra("com.bancempo.DESCRIPTION", description.text.toString())

        var chipText = ""
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            chipText += "${chip.text},"
        }
        i.putExtra("com.bancempo.SKILLS", chipText)

        startActivityForResult(i, 0)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            loadImageFromStorage("/data/user/0/com.bancempo/app_imageDir")

            fullName.text = data.getStringExtra("com.bancempo.FULL_NAME")
            nickname.text = data.getStringExtra("com.bancempo.NICKNAME")
            email.text = data.getStringExtra("com.bancempo.EMAIL")
            location.text = data.getStringExtra("com.bancempo.LOCATION")
            description.text = data.getStringExtra("com.bancempo.DESCRIPTION")

            val skillsString = data.getStringExtra("com.bancempo.SKILLS")
            chipGroup.removeAllViews()
            if (skillsString != null && skillsString != "") {
                skillsString.split(",").forEach {
                    var chip = Chip(activity)
                    if(!it.isEmpty()) {
                        chip.setText(it)
                        chipGroup.addView(chip)
                    }
                }
            }

            val jObject = JSONObject()
            jObject.put(getString(R.string.full_name), fullName.text.toString())
            jObject.put(getString(R.string.nickname), nickname.text.toString())
            jObject.put(getString(R.string.email), email.text.toString())
            jObject.put(getString(R.string.location), location.text.toString())
            jObject.put(getString(R.string.description), description.text.toString())

            var chipText = ""
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                chipText += "${chip.text},"
            }
            jObject.put(getString(R.string.skills), chipText)

            //save all the textviews in the shared_preferences file
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
            with (sharedPref.edit()) {
                putString("bancempoJSON", jObject.toString())
                apply()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }



    private fun loadImageFromStorage(path: String) {
        try {
            val f = File(path, "profile.jpeg")
            val fis = FileInputStream(f)
            val b = BitmapFactory.decodeStream(fis)
            val img = requireView().findViewById<ImageView>(R.id.profile_pic)
            fis.close()
            img.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    fun encodeToBase64(image: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        val imageEncoded: String = Base64.encodeToString(b, Base64.DEFAULT)
        //Log.d("Image Log:", imageEncoded)
        baos.close()
        return imageEncoded
    }


}