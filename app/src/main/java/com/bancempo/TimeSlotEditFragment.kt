package com.bancempo

import android.text.format.DateFormat
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
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

    private lateinit var date: TextInputLayout
    private lateinit var dateEdit: TextInputEditText

    private lateinit var timeslot: TextInputLayout
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
        dateEdit = view.findViewById(R.id.tvDate_text)
        timeslot = view.findViewById(R.id.tvTime)
        timeslotEdit = view.findViewById(R.id.tvTime_text)


        titleEdit.setText(arguments?.getString("title"))
        descriptionEdit.setText(arguments?.getString("description"))
        locationEdit.setText(arguments?.getString("location"))
        noteEdit.setText(arguments?.getString("note"))
        dateEdit.setText( arguments?.getString("date"))
        timeslotEdit.setText( arguments?.getString("time"))

        //TODO: da aggiungere date and time

        val createNewAdv = arguments?.getBoolean("createNewAdv")
        val modify = createNewAdv == null || createNewAdv == false

        val buttonDate = view.findViewById<Button>(R.id.buttonDate)
        buttonDate.setOnClickListener { showDialogOfDatePicker(modify) }

        val buttonTime = view.findViewById<Button>(R.id.buttonTime)
        buttonTime.setOnClickListener { showDialogOfTimeSlotPicker(modify) }

        val confirmButton = view.findViewById<Button>(R.id.confirmationButton)
        confirmButton.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("title", titleEdit.text.toString())
            bundle.putString("date", dateEdit.text.toString())
            bundle.putString("description", descriptionEdit.text.toString())
            bundle.putString("timeslot", timeslotEdit.text.toString())
            bundle.putString("duration", "TODO")
            bundle.putString("location", locationEdit.text.toString())
            bundle.putString("note", noteEdit.text.toString())

            //MODIFY ADV
            if(modify){
                val modifyFromList = arguments?.getBoolean("modifyFromList")
                val pos = arguments?.getInt("position")
                bundle.putInt("position", pos!!)

                if(modifyFromList == true){
                    setFragmentResult("confirmationOkModifyToList", bundle)
                }
                else{
                    setFragmentResult("confirmationOkModifyToDetails1", bundle)
                }
            }
            //CREATE A NEW ADV
            else{
                setFragmentResult("confirmationOkCreate", bundle)
            }
            findNavController().popBackStack()
        }

    }

    //Date
    private fun showDialogOfDatePicker(modifyFromList: Boolean) {
        val datePickerFragment = DatePickerFragment(dateEdit, modifyFromList)
        datePickerFragment.show(requireActivity().supportFragmentManager, "datePicker")
    }


    class DatePickerFragment(private val date: TextInputEditText, private val modify: Boolean) : DialogFragment(),DatePickerDialog.OnDateSetListener {

        private var c = Calendar.getInstance()
        private var year = c.get(Calendar.YEAR)
        private var month = c.get(Calendar.MONTH)
        private var day = c.get(Calendar.DAY_OF_MONTH)

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            if(modify){
                val arr = date.text.toString().split("/")
                val dd = arr[0].toInt()
                val mm = arr[1].toInt()
                val yyyy = arr[2].toInt()
                c.set(yyyy, mm, dd)
            }
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
                if (date.text != null) {
                    println("-------------- + ${date.text}")
                    year = date.text!!.split("/").elementAt(2).toInt()
                    month = date.text!!.split("/").elementAt(1).toInt() - 1
                    day = date.text!!.split("/").elementAt(0).toInt()
                } else {
                    year = c.get(Calendar.YEAR)
                    month = c.get(Calendar.MONTH)
                    day = c.get(Calendar.DAY_OF_MONTH)
                }
            }
            return DatePickerDialog(requireContext(), this, year, month, day)
        }

        @SuppressLint("SetTextI18n")
        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            this.year = year
            this.month = month
            this.day = day
            date.setText("${day}/${(month + 1)}/${year}")
        }

    }

    //Time
    private fun showDialogOfTimeSlotPicker(modify: Boolean) {
        val timeslotPickerFragment = TimePickerFragment(timeslotEdit, modify)
        timeslotPickerFragment.show(requireActivity().supportFragmentManager, "timePicker")
    }

    class TimePickerFragment(private val timeslot: TextInputEditText, private val modify: Boolean) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

        private var c = Calendar.getInstance()
        private var hour = c.get(Calendar.HOUR_OF_DAY)
        private var minute = c.get(Calendar.MINUTE)

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            if(modify){
                val arrTime = timeslot.text.toString().split(":")
                println("------------$arrTime")
                val hh = arrTime[0].toInt()
                val mm = arrTime[1].toInt()
                c.set(Calendar.HOUR_OF_DAY, hh)
                c.set(Calendar.MINUTE, mm)
            }
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
                if (timeslot != null) {
                    hour = timeslot.text!!.split(":").elementAt(0).toInt()
                    minute = timeslot.text!!.split(":").elementAt(1).toInt()
                } else {
                    hour = c.get(Calendar.HOUR)
                    minute = c.get(Calendar.MINUTE)
                }
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
                timeslot.setText("$hourOfDay:0$minute")
            } else {
                timeslot.setText("$hourOfDay:$minute")
            }
        }
    }
}