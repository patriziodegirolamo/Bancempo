package com.bancempo.fragments

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.bancempo.R
import com.bancempo.models.SharedViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.Exception
import java.util.*
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private val sharedVM: SharedViewModel by activityViewModels()
    private lateinit var photo: ImageView
    private lateinit var editPicture: ImageButton

    private lateinit var fullName: TextInputLayout
    private lateinit var fullName_ed: TextInputEditText
    private lateinit var description_ed: TextInputEditText
    private lateinit var nickname_ed: TextInputEditText
    private lateinit var email_ed: TextInputEditText
    private lateinit var location_ed: TextInputEditText
    private lateinit var skills_ed: TextInputEditText

    private lateinit var nickname: TextInputLayout
    private lateinit var email: TextInputLayout
    private lateinit var location: TextInputLayout
    private lateinit var skills: TextInputLayout
    private lateinit var description: TextInputLayout

    private lateinit var chipGroup: ChipGroup

    private val REQUEST_IMAGE_CAPTURE = 1
    private val SELECT_PICTURE = 200

    var btm: Bitmap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photo = view.findViewById(R.id.profile_pic)
        editPicture = view.findViewById(R.id.changeImageButton)
        fullName = view.findViewById(R.id.full_name)
        fullName_ed = view.findViewById(R.id.editTextFullName)
        nickname = view.findViewById(R.id.nick_name)
        nickname_ed = view.findViewById(R.id.editTextNickname)
        email = view.findViewById(R.id.email)
        email_ed = view.findViewById(R.id.editTextEmail)
        location = view.findViewById(R.id.location)
        location_ed = view.findViewById(R.id.editTextLocation)
        description = view.findViewById(R.id.description)
        description_ed = view.findViewById(R.id.editTextDescription)
        skills =view.findViewById(R.id.skills)
        skills_ed =view.findViewById(R.id.editTextSkills)
        chipGroup = view.findViewById(R.id.chipGroup)

        skills_ed.setOnClickListener{
            skills.error = null
        }

        skills.setEndIconOnClickListener {
            if (skills_ed.text.toString().isNotEmpty()) {
                skills_ed.setText(skills_ed.text.toString().trim())
                        //TODO SPAZI DENTRO STRINGA
                var valid = true
                for (i in 0 until chipGroup.childCount) {
                    val chip = chipGroup.getChildAt(i) as Chip
                    if(skills_ed.text.toString().toUpperCase() == chip.text.toString().toUpperCase()){
                        valid = false
                        break
                    }
                }
                if(valid) {
                    addChip(skills_ed.text.toString())
                    skills_ed.setText("")
                }
                else{
                    skills.setError("This skill has been already inserted!")
                }
            }
        }

        fullName_ed.setText(arguments?.getString("fullname"))
        nickname_ed.setText(arguments?.getString("nickname"))
        email_ed.setText(arguments?.getString("email"))
        location_ed.setText(arguments?.getString("location"))
        description_ed.setText(arguments?.getString("description"))
        var skillsString : String? = arguments?.getString("skill")

        println("SKILLSTRING $skillsString")


        if (skillsString != null) {
            chipGroup.removeAllViews()
            skillsString.split(",").forEach {
                val chip = Chip(activity)
                if (it.isNotEmpty()) {
                    chip.text = it
                    chip.isCloseIconVisible = true

                    chip.setOnCloseIconClickListener {
                        if( sharedVM.myAdvs.value!!.values.filter { x -> x.skill.split(",").contains(chip.text)}.isNotEmpty() ){
                            activity?.let {
                                val builder = AlertDialog.Builder(it)
                                builder.apply {
                                    setPositiveButton("Yes, I am sure", DialogInterface.OnClickListener{ _, _ ->
                                        println("chip: Yes, I'm sure!")
                                        chipGroup.removeView(chip)
                                    })
                                    setNegativeButton("No, turn back", DialogInterface.OnClickListener{ _, _ ->
                                        println("chip: Nope!")
                                    })
                                }
                                builder.setTitle("Removing Advertisments")
                                builder.setMessage(R.string.advs_delete_message)
                                builder.show()
                            }

                        }else{
                            chipGroup.removeView(chip)
                        }
                    }
                    chipGroup.addView(chip)
                }
            }
        }

        if (savedInstanceState != null) {
            skillsString = savedInstanceState.getString("skills")
            if (skillsString != null) {
                chipGroup.removeAllViews()
                skillsString.split(",").forEach {
                    val chip = Chip(activity)
                    if(it.isNotEmpty()) {
                        chip.text = it
                        chip.isCloseIconVisible = true

                        chip.setOnCloseIconClickListener {
                            chipGroup.removeView(chip)
                        }
                        chipGroup.addView(chip)
                    }
                }
            }
        }

        val btmString = savedInstanceState?.getString("btmString")
        if( btmString == null){
            sharedVM.loadImageUser(photo, view)
        }
        else{
            btm = stringToBitmap(btmString)
            if(btm != null)
                photo.setImageBitmap(btm)
        }

        //handling on-press small image button
        editPicture.setOnClickListener {
            showPopup(editPicture)
        }


        //handling on back pressed
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    println("-----------------${validation()}")
                    if(validation()) {
                        var chipText = ""
                        for (i in 0 until chipGroup.childCount) {
                            val chip = chipGroup.getChildAt(i) as Chip
                            if(i == chipGroup.childCount - 1){
                                chipText += "${chip.text}"

                            }else{
                                chipText += "${chip.text},"
                            }
                        }
                        if(btm != null){
                            println("bitmap: upload bitmap")
                            sharedVM.uploadBitmap(btm!!, view, chipText)
                        }
                        else{
                            println("bitmap: update user without new photo")
                            sharedVM.updateUser(view, chipText, updatingImg = false)
                        }



                        setFragmentResult("backFromEdit", bundleOf(Pair("chipText", chipText)))
                        findNavController().popBackStack()
                    }
                }
            })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var chipText = ""
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            chipText += "${chip.text},"
        }
        outState.putString("skills", chipText)

        if(btm != null){
            val btmString = bitmapToString(btm!!)
            outState.putString("btmString", btmString)
        }

        else{
        }
    }


    private fun validateTextInput(text: TextInputLayout, textEdit: TextInputEditText): Boolean {
        if (textEdit.text.isNullOrEmpty()) {
            if(text.hint != "Nickname") {
                text.error = "Please, fill in this field!"
                return false
            }
            else{
                textEdit.setError("Please, fill in this field!")
                return false
            }
        } else {
            if (text.hint == "Description") {
                return if (textEdit.text?.length!! > 200) {
                    text.error = "Your ${text.hint} is too long."
                    false
                } else {
                    text.error = null
                    true
                }
            } else if (text.hint == "Full Name" || text.hint == "Location") {
                return if (textEdit.text?.length!! > 40) {
                    text.error = "Your ${text.hint} is too long."
                    false
                } else {
                    text.error = null
                    return true
                }
            } else if (text.hint == "Nickname") {
                return if (textEdit.text?.length!! > 25) {
                    textEdit.setError("Your ${text.hint} is too long.")
                    false
                } else {
                    text.error = null
                    true
                }
            }
            else if(text.hint == "Email" ){
                return if (textEdit.text?.length!! > 40) {
                    text.error = "Your ${text.hint} is too long."
                    false
                } else {
                    text.error = null
                    return true
                }
            }
            else return false

        }

    }

    private fun validation(): Boolean {
        var valid = true

        if (!validateTextInput(fullName, fullName_ed)) {
            valid = false
            println("FULLNAME $valid")
        }
        if (!validateTextInput(description, description_ed)) {
            valid = false
            println("DESCRIPTION $valid")
        }

        if (!validateTextInput(nickname, nickname_ed)) {
            valid = false
            println("NICKNAME $valid")
        }

        if (!validateTextInput(email, email_ed)) {
            valid = false
            println("EMAIL $valid")
        }
        if (!validateTextInput(location, location_ed)) {
            valid = false
            println("LOCATION $valid")
        }
        return valid
    }


    private fun showPopup(v: View) {

        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(R.menu.menu_profile_picture, popup.menu)

        popup.setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.select_image_gallery -> {
                    val i = Intent(Intent.ACTION_GET_CONTENT)
                    i.type = "image/*"
                    // pass the constant to compare it with the returned requestCode
                    startActivityForResult(
                        Intent.createChooser(i, "Select Picture"),
                        SELECT_PICTURE
                    )

                }
                R.id.use_camera -> {
                    dispatchTakePictureIntent()

                }
            }
            false
        }

        popup.show()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            Toast.makeText(context, R.string.camera_not_available, Toast.LENGTH_SHORT).show()
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val bitmapPhoto = data.extras?.get("data") as Bitmap
            //sharedVM.uploadBitmap(bitmapPhoto)
            btm = bitmapPhoto
            photo.setImageBitmap(bitmapPhoto)
        }

        else if (requestCode == SELECT_PICTURE && resultCode == AppCompatActivity.RESULT_OK && data != null){
            val uriPhoto = data.data
            val bitmapPhoto = updateProfilePictureFromURI(uriPhoto!!)
            btm = bitmapPhoto
            photo.setImageBitmap(bitmapPhoto)
        }
    }

    fun updateProfilePictureFromURI(uri : Uri) :Bitmap{
        val bmp:Bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
        val ins: InputStream? = context?.contentResolver?.openInputStream(uri)

        val ei = ExifInterface(ins!!)
        val rotatedBitmap: Bitmap = when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bmp, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bmp, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bmp, 270f)
            ExifInterface.ORIENTATION_NORMAL -> bmp
            else -> bmp
        }

        ins.close()
        return rotatedBitmap
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)

        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    private fun addChip(text: String) {
        val chip = Chip(context)
        val lower = text.substring(1, text.length).toLowerCase()
        val upper = text.capitalize().substring(0,1)
        chip.text = upper + lower
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(chip)
        }
        chipGroup.addView(chip)
    }

    private fun bitmapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.getEncoder().encodeToString(b)!!
    }

    private fun stringToBitmap(encodedString: String): Bitmap? {
        return try {
            val encodedByte = Base64.getDecoder().decode(encodedString)
            val bitmap = BitmapFactory.decodeByteArray(encodedByte, 0,encodedByte.size)
            bitmap
        } catch(e:Exception){
            e.message
            null
        }
    }

}