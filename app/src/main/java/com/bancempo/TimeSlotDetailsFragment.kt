package com.bancempo

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
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

    private lateinit var time: TextInputLayout
    private lateinit var time_ed: TextInputEditText

    private lateinit var note: TextInputLayout
    private lateinit var note_ed: TextInputEditText


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

        location = view.findViewById(R.id.location_adv)
        location_ed = view.findViewById(R.id.edit_location_text)

        note = view.findViewById(R.id.note_adv)
        note_ed = view.findViewById(R.id.edit_note_text)


        title_ed.setText(arguments?.getString("title"))
        description_ed.setText(arguments?.getString("description"))
        date_ed.setText(arguments?.getString("date"))
        time_ed.setText(arguments?.getString("time"))
        location_ed.setText(arguments?.getString("location"))
        note_ed.setText(arguments?.getString("note"))
        //arguments?.getString("duration")

        setFragmentResultListener("confirmationOkModifyToDetails1") { _, bundle ->
            title_ed.setText(bundle.getString("title"))
            description_ed.setText(bundle.getString("description"))
            date_ed.setText(bundle.getString("date"))

            //TODO: il time non lo prende
            time_ed.setText(bundle.getString("time"))
            location_ed.setText(bundle.getString("location"))
            note_ed.setText(bundle.getString("note"))
            setFragmentResult("confirmationOkModifyToDetails2", bundle)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val bundle = Bundle()
        when(item.itemId){
            //clicking on edit adv
            R.id.inDetailsEditAdv -> {
                bundle.putBoolean("modifyFromDetails", true)
                bundle.putString("title", arguments?.getString("title"))
                bundle.putInt("position", arguments?.getInt("position")!!)
                bundle.putString("description", arguments?.getString("description"))
                bundle.putString("date", arguments?.getString("date"))
                bundle.putString("time", arguments?.getString("time"))
                bundle.putString("location", arguments?.getString("location"))
                bundle.putString("note", arguments?.getString("note"))
                requireView().findNavController().navigate(R.id.action_timeSlotDetailsFragment_to_timeSlotEditFragment, bundle)
                return super.onOptionsItemSelected(item)
            }
            //clicking back button
            else -> {
                return NavigationUI.onNavDestinationSelected(item,
                    requireView().findNavController()) || super.onOptionsItemSelected(item)
            }
        }
    }

}