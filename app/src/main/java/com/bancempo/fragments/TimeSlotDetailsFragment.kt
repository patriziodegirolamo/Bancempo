package com.bancempo.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.bancempo.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class TimeSlotDetailsFragment : Fragment(R.layout.fragment_time_slot_details) {
    private lateinit var title: TextInputLayout
    private lateinit var title_ed: TextInputEditText

    private lateinit var description: TextInputLayout
    private lateinit var description_ed: TextInputEditText


    private lateinit var date: TextInputLayout
    private lateinit var date_ed: TextInputEditText


    private lateinit var location: TextInputLayout
    private lateinit var location_ed: TextInputEditText

    private lateinit var duration: TextInputLayout
    private lateinit var duration_ed: TextInputEditText

    private lateinit var time: TextInputLayout
    private lateinit var time_ed: TextInputEditText

    private lateinit var note: TextInputLayout
    private lateinit var note_ed: TextInputEditText

    private lateinit var chipGroup: ChipGroup

    private var skills : String? = ""

    private var isMyAdv = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        title = view.findViewById(R.id.title_adv)
        title_ed = view.findViewById(R.id.edit_title_text)

        description = view.findViewById(R.id.description_adv)
        description_ed = view.findViewById(R.id.edit_description_text)

        date = view.findViewById(R.id.date_adv)
        date_ed = view.findViewById(R.id.edit_date_text)

        time = view.findViewById(R.id.time_adv)
        time_ed = view.findViewById(R.id.edit_time_text)

        duration = view.findViewById(R.id.duration_adv)
        duration_ed = view.findViewById(R.id.edit_duration_text)

        location = view.findViewById(R.id.edit_duration)
        location_ed = view.findViewById(R.id.edit_location_text)

        note = view.findViewById(R.id.note_adv)
        note_ed = view.findViewById(R.id.edit_note_text)

        chipGroup = view.findViewById(R.id.chipGroup)

        title_ed.setText(arguments?.getString("title"))
        description_ed.setText(arguments?.getString("description"))
        date_ed.setText(arguments?.getString("date"))
        time_ed.setText(arguments?.getString("time"))
        duration_ed.setText(arguments?.getString("duration"))
        location_ed.setText(arguments?.getString("location"))
        note_ed.setText(arguments?.getString("note"))

        skills = arguments?.getString("skill")
        println("----ARG $arguments")
        println("----SKILLS $skills")

        skills?.split(",")?.forEach{
            if(it != "") {
                val chip = Chip(activity)
                chip.text = it
                chip.setChipBackgroundColorResource(R.color.divider_color)
                chip.isCheckable = false
                chipGroup.addView(chip)
            }
        }

        isMyAdv = arguments?.getBoolean("isMyAdv")!!


        setFragmentResultListener("confirmationOkModifyToDetails") { _, bundle ->
            title_ed.setText(bundle.getString("title"))
            description_ed.setText(bundle.getString("description"))
            date_ed.setText(bundle.getString("date"))
            time_ed.setText(bundle.getString("time"))
            location_ed.setText(bundle.getString("location"))
            note_ed.setText(bundle.getString("note"))
            duration_ed.setText(bundle.getString("duration"))
            skills = bundle.getString("skill")
            println("----BUNDLE $bundle")
            println("----SKILLS $skills")

            chipGroup.removeAllViews()
            skills!!.split(",").forEach{
                if(it != "") {
                    val chip = Chip(activity)
                    chip.text = it
                    chip.setChipBackgroundColorResource(R.color.divider_color)
                    chip.isCheckable = false
                    chipGroup.addView(chip)
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (isMyAdv) {
            inflater.inflate(R.menu.options_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val bundle = Bundle()
        when (item.itemId) {
            //clicking on edit adv
            R.id.inDetailsEditAdv -> {
                bundle.putBoolean("modifyFromDetails", true)
                bundle.putString("id", arguments?.getString("id"))
                bundle.putString("title", title_ed.text.toString())
                bundle.putInt("position", arguments?.getInt("position")!!)
                bundle.putString("description", description_ed.text.toString())
                bundle.putString("duration", duration_ed.text.toString())
                bundle.putString("date", date_ed.text.toString())
                bundle.putString("time", time_ed.text.toString())
                bundle.putString("location", location_ed.text.toString())
                bundle.putString("note", note_ed.text.toString())

                var chipText = ""
                for (i in 0 until chipGroup.childCount) {
                    val chip = chipGroup.getChildAt(i) as Chip
                        println("--------CHIP ${chipGroup.childCount}")
                        if (i == chipGroup.childCount - 1) {
                            chipText += "${chip.text}"

                        } else {
                            chipText += "${chip.text},"
                        }
                }

                bundle.putString("skill",  chipText)

                println("----ARG222 $chipText")

                requireView().findNavController()
                    .navigate(R.id.action_timeSlotDetailsFragment_to_timeSlotEditFragment, bundle)
                return super.onOptionsItemSelected(item)
            }
            //clicking back button
            else -> {
                return NavigationUI.onNavDestinationSelected(
                    item,
                    requireView().findNavController()
                ) || super.onOptionsItemSelected(item)
            }
        }
    }

}