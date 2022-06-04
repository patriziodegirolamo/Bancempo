package com.bancempo.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bancempo.R
import com.bancempo.models.SharedViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText

class OtherProfileFragment : Fragment(R.layout.fragment_show_profile) {

    private val sharedVM: SharedViewModel by activityViewModels()
    private lateinit var fullNameEd: TextInputEditText
    private lateinit var descriptionEd: TextInputEditText
    private lateinit var nicknameEd: TextInputEditText
    private lateinit var emailEd: TextInputEditText
    private lateinit var locationEd: TextInputEditText
    private lateinit var skillsEd: TextInputEditText
    private lateinit var creditEd: TextInputEditText
    private lateinit var ratingBar: RatingBar
    private lateinit var ratingNum: TextView



    private lateinit var photo: ImageView
    private lateinit var chipGroup: ChipGroup
    private var loadImg = true


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        ratingBar = view.findViewById(R.id.ratingBar)
        ratingNum = view.findViewById(R.id.rating_num)
        fullNameEd = view.findViewById(R.id.textViewFullName_ed)
        photo = view.findViewById(R.id.profile_pic)
        nicknameEd = view.findViewById(R.id.textViewNickname_ed)
        emailEd = view.findViewById(R.id.textViewEmail_ed)
        locationEd = view.findViewById(R.id.textViewLocation_ed)
        descriptionEd = view.findViewById(R.id.textViewDescription_ed)
        creditEd = view.findViewById(R.id.tvCredit_ed)

        skillsEd = view.findViewById(R.id.textViewSkills_ed)
        chipGroup = view.findViewById(R.id.chipGroup)

        val userId = arguments?.getString("userId")
        val user = sharedVM.users.value!![userId]!!

        val numRatings = sharedVM.ratings.value!!.values
            .filter { x -> x.idReceiver == user.email }.size

        ratingBar.rating = user.rating.toFloat()
        ratingNum.text = " ( ".plus(numRatings.toString()).plus(" ) ")
        fullNameEd.setText(user.fullname)
        nicknameEd.setText(user.nickname)
        emailEd.setText(user.email)
        locationEd.setText(user.location)
        descriptionEd.setText(user.description)
        creditEd.setText(user.credit.toString())

        skillsEd.setText("")
        chipGroup.removeAllViews()
        user.skills.forEach {
            val chip = Chip(activity)
            chip.text = it
            chip.setChipBackgroundColorResource(R.color.divider_color)
            chip.isCheckable = false
            chipGroup.addView(chip)
        }


        if (loadImg) {
            sharedVM.loadImageUser(photo, view, user)
        } else {
            val pb = view.findViewById<ProgressBar>(R.id.progressBar)
            pb.visibility = View.VISIBLE
            photo.visibility = View.INVISIBLE
            loadImg = true
        }

        ratingBar.setOnTouchListener( View.OnTouchListener{ _, event ->
            if(event.action == MotionEvent.ACTION_UP){
                println("rating: Show rating of this user")
                val bundle = Bundle()
                bundle.putString("userId", user.email)
                findNavController().navigate(R.id.action_otherProfileFragment_to_ratingsFragment, bundle)
            }
            return@OnTouchListener true
        })
    }

}