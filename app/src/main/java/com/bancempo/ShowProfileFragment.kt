package com.bancempo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class ShowProfileFragment : Fragment(R.layout.fragment_show_profile) {

    val userVM: UserVM by activityViewModels()
    lateinit var fullName: TextView
    lateinit var photo: ImageView
    lateinit var nickname: TextView
    lateinit var email: TextView
    lateinit var location: TextView
    lateinit var skills: TextView
    lateinit var description: TextView
    lateinit var chipGroup: ChipGroup

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        fullName = view.findViewById<TextView>(R.id.textViewFullName)
        photo = view.findViewById<ImageView>(R.id.profile_pic)
        nickname = view.findViewById<TextView>(R.id.textViewNickname)
        email = view.findViewById<TextView>(R.id.textViewEmail)
        location = view.findViewById<TextView>(R.id.textViewLocation)
        description = view.findViewById<TextView>(R.id.textViewDescription)

        skills =view.findViewById<TextView>(R.id.textViewSkills)
        chipGroup = view.findViewById<ChipGroup>(R.id.chipGroup)


        photo.setImageBitmap(userVM.profilePictureBitmap.value)
        fullName.text = userVM.fullname.value.toString()
        nickname.text = userVM.nickname.value.toString()
        email.text = userVM.email.value.toString()
        location.text = userVM.location.value.toString()
        description.text = userVM.description.value.toString()
        skills.text = getString(R.string.skills)

        var skillsString = userVM.skills.value.toString()
        chipGroup.removeAllViews()
        if (skillsString != "") {
            skillsString.split(",").forEach {
                val chip = Chip(activity)
                if (it.isNotEmpty()) {
                    chip.text = it
                    chipGroup.addView(chip)
                }
            }
        }



        //after backpressed from editprofile
        fragmentManager?.setFragmentResultListener("backPressed", viewLifecycleOwner){ _, bundle ->

            photo.setImageBitmap(userVM.profilePictureBitmap.value)
            fullName.text = userVM.fullname.value.toString()
            nickname.text = userVM.nickname.value.toString()
            email.text = userVM.email.value.toString()
            location.text = userVM.location.value.toString()
            description.text = userVM.description.value.toString()

            skills.text = getString(R.string.skills)
            skillsString = userVM.skills.value.toString()


            chipGroup.removeAllViews()
            if (skillsString != "") {
                skillsString.split(",").forEach {
                    val chip = Chip(activity)
                    if (it.isNotEmpty()) {
                        chip.text = it
                        chipGroup.addView(chip)
                    }
                }
            }
        }

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
        val bundle = Bundle()

        bundle.putString("fullname", fullName.text.toString())
        bundle.putString("nickname", nickname.text.toString())
        bundle.putString("description", description.text.toString())
        bundle.putString("location", location.text.toString())
        bundle.putString("email", email.text.toString())

        //bundle.putString("photo", encodeToBase64(photo.drawToBitmap()))
        //bundle.putParcelable("bitmapImg", photo.drawToBitmap())
        var chipText = ""
        for (j in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(j) as Chip
            chipText += "${chip.text},"
        }
        bundle.putString("skills", chipText)
        findNavController().navigate(R.id.action_showProfileFragment_to_editProfileFragment, bundle)
    }




}