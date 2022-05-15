package com.bancempo.fragments


import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bancempo.R
import com.bancempo.models.SharedViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class ShowProfileFragment : Fragment(R.layout.fragment_show_profile) {

    private val sharedVM: SharedViewModel by activityViewModels()
    private lateinit var fullName: TextInputLayout
    private lateinit var fullName_ed: TextInputEditText
    private lateinit var description_ed: TextInputEditText
    private lateinit var nickname_ed: TextInputEditText
    private lateinit var email_ed: TextInputEditText
    private lateinit var location_ed: TextInputEditText
    private lateinit var skills_ed: TextInputEditText

    private lateinit var photo: ImageView
    private lateinit var nickname: TextInputLayout
    private lateinit var email: TextInputLayout
    private lateinit var location: TextInputLayout
    private lateinit var skills: TextInputLayout
    private lateinit var description: TextInputLayout
    private lateinit var chipGroup: ChipGroup

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        fullName_ed = view.findViewById(R.id.textViewFullName_ed)
        photo = view.findViewById(R.id.profile_pic)
        nickname_ed = view.findViewById(R.id.textViewNickname_ed)
        email_ed = view.findViewById(R.id.textViewEmail_ed)
        location_ed = view.findViewById(R.id.textViewLocation_ed)
        description_ed = view.findViewById(R.id.textViewDescription_ed)

        skills_ed =view.findViewById(R.id.textViewSkills_ed)
        chipGroup = view.findViewById(R.id.chipGroup)


        sharedVM.currentUser.observe(viewLifecycleOwner){ user ->
            fullName_ed.setText(user.fullname)
            nickname_ed.setText(user.nickname)
            email_ed.setText(user.email)
            location_ed.setText(user.location)
            description_ed.setText(user.description)
            skills_ed.setText("")
            sharedVM.loadImageUser(photo)

            for(s in user.skills){
                s.get().addOnSuccessListener {
                    val chip = Chip(activity)
                    if (it.id.isNotEmpty()) {
                        chip.text = it.id
                        chipGroup.addView(chip)
                    }
                }
            }
            /*
            chipGroup.removeAllViews()
            for(s in user.skills){
                s.get().addOnSuccessListener {
                    val chip = Chip(activity)
                    if (it.id.isNotEmpty()) {
                        chip.text = it.id
                        chipGroup.addView(chip)
                    }
                }
            }

             */
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
        bundle.putString("fullname", fullName_ed.text.toString())
        bundle.putString("nickname", nickname_ed.text.toString())
        bundle.putString("description", description_ed.text.toString())
        bundle.putString("location", location_ed.text.toString())
        bundle.putString("email", email_ed.text.toString())

        var chipText = ""
        for (j in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(j) as Chip
            chipText += "${chip.text},"
        }
        bundle.putString("skills", chipText)

        findNavController().navigate(R.id.action_showProfileFragment_to_editProfileFragment, bundle)
    }




}