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

class TimeSlotDetailsFragment : Fragment(R.layout.fragment_time_slot_details) {
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var date: TextView
    private lateinit var location: TextView
    private lateinit var time: TextView
    private lateinit var note: TextView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        title = view.findViewById(R.id.title_adv)
        description = view.findViewById(R.id.description_adv)
        date = view.findViewById(R.id.date_adv)
        time = view.findViewById(R.id.time_adv)
        location = view.findViewById(R.id.location_adv)
        note = view.findViewById(R.id.note_adv)

        title.text = arguments?.getString("title")
        description.text = arguments?.getString("description")
        date.text = arguments?.getString("date")
        time.text = arguments?.getString("time")
        location.text = arguments?.getString("location")
        note.text = arguments?.getString("note")
        //arguments?.getString("duration")

        setFragmentResultListener("confirmationOkModifyToDetails1") { _, bundle ->
            title.text = bundle.getString("title")
            description.text = bundle.getString("description")
            date.text = bundle.getString("date")

            //TODO: il time non lo prende
            time.text = bundle.getString("time")
            location.text = bundle.getString("location")
            note.text = bundle.getString("note")
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