package com.bancempo

import android.os.Bundle
import android.sax.TextElementListener
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class TimeSlotEditFragment : Fragment(R.layout.fragment_time_slot_edit){
    private lateinit var title: TextInputLayout
    private lateinit var description: TextInputLayout
    private lateinit var selectedDates: TextInputLayout
    private lateinit var date: DatePicker
    private lateinit var location: TextInputLayout
    private lateinit var time: TimePicker
    private lateinit var note: TextInputLayout


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val year: Int
        val month: Int
        val day: Int
        val string_date: String?

        title = view.findViewById(R.id.edit_title)
        description = view.findViewById(R.id.edit_description)
        location = view.findViewById(R.id.edit_location)
        note = view.findViewById(R.id.edit_note)

        title.placeholderText = arguments?.getString("title")
        description.placeholderText = arguments?.getString("description")
        location.placeholderText = arguments?.getString("location")
        note.placeholderText = arguments?.getString("note")

        /*
        string_date = arguments?.getString("date");

        if(string_date != null) {
            year = string_date.split("/").elementAt(0).toInt()
            month = string_date.split("/").elementAt(1).toInt()
            day = string_date.split("/").elementAt(2).toInt()

            date.init(year,month,day, DatePicker.OnDateChangedListener { date, year, month, day->
                selectedDates.placeholderText = "Year: "+ year + " Month: "+ (month+1) + " Day: "+day
            })
        }

         */

    }
}