package com.bancempo



import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.json.JSONObject
import java.io.*

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    val userVM: UserVM by activityViewModels()


    lateinit var fullName: TextView
    lateinit var photo: ImageView
    lateinit var nickname: TextView
    lateinit var email: TextView
    lateinit var location: TextView
    lateinit var skills: TextView
    lateinit var description: TextView
    lateinit var editPicture: ImageButton

    lateinit var editText: EditText
    lateinit var addchipbutton: ImageButton
    lateinit var chipGroup: ChipGroup

    val REQUEST_IMAGE_CAPTURE = 1
    val SELECT_PICTURE = 200


    //var uri_or_bitmap:String = ""
    var bitmap_photo: Bitmap? = null
    var uri_photo: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photo = view.findViewById<ImageView>(R.id.profile_pic)
        editPicture = view.findViewById<ImageButton>(R.id.changeImageButton)
        fullName = view.findViewById<TextView>(R.id.editTextFullName)
        nickname = view.findViewById<TextView>(R.id.editTextNickname)
        email = view.findViewById<TextView>(R.id.editTextEmail)
        location = view.findViewById<TextView>(R.id.editTextLocation)
        description = view.findViewById<TextView>(R.id.editTextDescription)
        editText = view.findViewById<EditText>(R.id.editTextSkills)
        addchipbutton = view.findViewById<ImageButton>(R.id.addChipButton)
        chipGroup = view.findViewById<ChipGroup>(R.id.chipGroup)

        addchipbutton.setOnClickListener {
            if (editText.text.toString().isNotEmpty()) {
                addChip(editText.text.toString())
                editText.setText("")
            }
        }

        //get value from showprofile
        photo.setImageBitmap(userVM.profilePictureBitmap.value)
        fullName.text = arguments?.getString("fullname")
        nickname.text = arguments?.getString("nickname")
        email.text = arguments?.getString("email")
        location.text = arguments?.getString("location")
        description.text = arguments?.getString("description")
        val skillsString = arguments?.getString("skills")
        if (skillsString != null) {
            chipGroup.removeAllViews()
            skillsString.split(",").forEach {
                val chip = Chip(activity)
                if (it.isNotEmpty()) {
                    chip.text = it
                    chip.isCloseIconVisible = true

                    chip.setOnCloseIconClickListener {
                        chipGroup.removeView(chip)
                    }
                    chipGroup.addView(chip)
                }
            }
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
                    userVM.updateFromEditProfile(view)

                    setFragmentResult("backPressed", bundleOf())
                    findNavController().popBackStack()
                }
            })

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
                    true
                }
                R.id.use_camera -> {
                    dispatchTakePictureIntent()
                    true
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
            bitmap_photo = data.extras?.get("data") as Bitmap
            userVM.storeProfilePicture(bitmap_photo!!)
            photo.setImageBitmap(userVM.profilePictureBitmap.value)
        }

        else if (requestCode == SELECT_PICTURE && resultCode == AppCompatActivity.RESULT_OK && data != null){
            uri_photo = data.data
            userVM.updateProfilePictureFromURI(uri_photo!!)
            photo.setImageBitmap(userVM.profilePictureBitmap.value)
        }
    }


    private fun addChip(text: String) {
        val chip = Chip(context)
        chip.text = text
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(chip)
        }
        chipGroup.addView(chip)
    }

}