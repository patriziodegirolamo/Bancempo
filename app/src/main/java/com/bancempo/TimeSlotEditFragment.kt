package com.bancempo

import android.text.format.DateFormat
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class TimeSlotEditFragment : Fragment(R.layout.fragment_time_slot_edit){
    private lateinit var title: TextInputLayout
    private lateinit var titleEdit: TextInputEditText

    private lateinit var description: TextInputLayout
    private lateinit var descriptionEdit: TextInputEditText

    private lateinit var location: TextInputLayout
    private lateinit var locationEdit: TextInputEditText

    private lateinit var note: TextInputLayout
    private lateinit var noteEdit: TextInputEditText

    private lateinit var date: TextView
    private lateinit var dateEdit: TextInputEditText

    private lateinit var timeslot: TextView
    private lateinit var timeslotEdit: TextInputEditText


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title = view.findViewById(R.id.edit_title)
        titleEdit = view.findViewById(R.id.edit_title_text)

        description = view.findViewById(R.id.edit_description)
        descriptionEdit = view.findViewById(R.id.edit_description_text)

        location = view.findViewById(R.id.edit_location)
        locationEdit = view.findViewById(R.id.edit_location_text)

        note = view.findViewById(R.id.edit_note)
        noteEdit = view.findViewById(R.id.edit_note_text)

        date = view.findViewById(R.id.tvDate)
        timeslot = view.findViewById(R.id.tvTime)

        if(arguments != null){
            //TODO: editText without rewrite everything
            title.placeholderText = arguments?.getString("title")
            description.placeholderText = arguments?.getString("description")
            location.placeholderText = arguments?.getString("location")
            note.placeholderText = arguments?.getString("note")
        }


        val buttonDate = view.findViewById<Button>(R.id.buttonDate)
        buttonDate.setOnClickListener { showDialogOfDatePicker() }

        val buttonTime = view.findViewById<Button>(R.id.buttonTime)
        buttonTime.setOnClickListener { showDialogOfTimeSlotPicker() }

        val confirmButton = view.findViewById<Button>(R.id.confirmationButton)
        confirmButton.setOnClickListener{
            val fromDetails = arguments?.getBoolean("fromDetails")
            println("---------------$fromDetails")
            val bundle = Bundle()
            bundle.putString("title", titleEdit.text.toString())
            bundle.putString("date", date.text.toString())
            bundle.putString("description", descriptionEdit.text.toString())
            bundle.putString("timeslot", timeslot.text.toString())
            bundle.putString("duration", "TODO")
            bundle.putString("location", locationEdit.text.toString())
            bundle.putString("note", noteEdit.text.toString())
            //CREATE A NEW ADV
            if(arguments == null){
                setFragmentResult("confirmationOkCreate", bundle)
            }
            //MODIFY ADV
            else{
                val pos = arguments?.getInt("position")
                bundle.putInt("position", pos!!)

                setFragmentResult("confirmationOkModify", bundle)
            }
            findNavController().popBackStack()
        }
    }

    //Date
    private fun showDialogOfDatePicker() {
        val datePickerFragment = DatePickerFragment(date)
        datePickerFragment.show(requireActivity().supportFragmentManager, "datePicker")
    }


    class DatePickerFragment(private val date: TextView) : DialogFragment(),DatePickerDialog.OnDateSetListener {

        private var c = Calendar.getInstance()
        private var year = c.get(Calendar.YEAR)
        private var month = c.get(Calendar.MONTH)
        private var day = c.get(Calendar.DAY_OF_MONTH)

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            outState.putInt("year", year)
            outState.putInt("month", month)
            outState.putInt("day", day)
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            if(savedInstanceState!= null){
                year = savedInstanceState.getInt("year")
                month = savedInstanceState.getInt("month")
                day = savedInstanceState.getInt("day")
            }
            else{
                year = c.get(Calendar.YEAR)
                month = c.get(Calendar.MONTH)
                day = c.get(Calendar.DAY_OF_MONTH)
            }
            return DatePickerDialog(requireContext(), this, year, month, day)
        }

        @SuppressLint("SetTextI18n")
        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            this.year = year
            this.month = month
            this.day = day
            date.text = "${day}/${(month + 1)}/${year}"
        }

    }

    //Time
    private fun showDialogOfTimeSlotPicker() {
        val timeslotPickerFragment = TimePickerFragment(timeslot)
        timeslotPickerFragment.show(requireActivity().supportFragmentManager, "timePicker")
    }

    class TimePickerFragment(private val timeslot: TextView) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

        private var c = Calendar.getInstance()
        private var hour = c.get(Calendar.HOUR_OF_DAY)
        private var minute = c.get(Calendar.MINUTE)

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }

        override fun onSaveInstanceState(outState: Bundle) {
            outState.putInt("hour", hour)
            outState.putInt("minute", minute)
            super.onSaveInstanceState(outState)
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            if(savedInstanceState!= null){
                hour = savedInstanceState.getInt("hour")
                minute = savedInstanceState.getInt("minute")
            }
            else{
                hour = c.get(Calendar.HOUR)
                minute = c.get(Calendar.MINUTE)
            }

            return TimePickerDialog(
                activity,
                this,
                hour,
                minute,
                DateFormat.is24HourFormat(activity)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
            if (minute in 0..9) {
                timeslot.text = "$hourOfDay:0$minute"
            } else {
                timeslot.text = "$hourOfDay:$minute"
            }
        }
    }
}