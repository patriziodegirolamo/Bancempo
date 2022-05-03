package com.bancempo


import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ShowProfileFragment : Fragment(R.layout.fragment_show_profile) {

    private val userVM: UserVM by activityViewModels()
    private lateinit var fullName: TextView
    private lateinit var photo: ImageView
    private lateinit var nickname: TextView
    private lateinit var email: TextView
    private lateinit var location: TextView
    private lateinit var skills: TextView
    private lateinit var description: TextView
    private lateinit var chipGroup: ChipGroup

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        fullName = view.findViewById(R.id.textViewFullName)
        photo = view.findViewById(R.id.profile_pic)
        nickname = view.findViewById(R.id.textViewNickname)
        email = view.findViewById(R.id.textViewEmail)
        location = view.findViewById(R.id.textViewLocation)
        description = view.findViewById(R.id.textViewDescription)

        skills =view.findViewById(R.id.textViewSkills)
        chipGroup = view.findViewById(R.id.chipGroup)


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
        fragmentManager?.setFragmentResultListener("backPressed", viewLifecycleOwner){ _, _ ->

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
        println("--------------------EDIT PROFILE")
        findNavController().navigate(R.id.action_showProfileFragment_to_editProfileFragment, bundle)
    }




}