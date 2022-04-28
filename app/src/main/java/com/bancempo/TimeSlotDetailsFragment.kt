package com.bancempo

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController()) || super.onOptionsItemSelected(item)
    }



}