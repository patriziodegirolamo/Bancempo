package com.bancempo

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class TimeSlotListFragment : Fragment(R.layout.fragment_time_slot_list){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonProfile = view.findViewById<Button>(R.id.buttonProfile)
        val buttonList = view.findViewById<Button>(R.id.buttonList)

        buttonProfile.setOnClickListener{
            findNavController().navigate(R.id.action_timeSlotListFragment_to_showProfileFragment)
        }

        buttonList.setOnClickListener{
            findNavController().navigate(R.id.action_timeSlotListFragment_self)
        }
    }
}