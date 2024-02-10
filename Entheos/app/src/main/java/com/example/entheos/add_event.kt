package com.example.entheos

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TimePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.SimpleTimeZone

class add_event : AppCompatActivity(),DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {

    var title:EditText? = null
    var desc:EditText? = null
    var date:EditText? = null
    var time:EditText? = null
    var btn:Button? = null
    var back:LinearLayout? = null

    private var calender:Calendar = Calendar.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        title = findViewById(R.id.add_title)
        desc = findViewById(R.id.add_desc)
        date= findViewById(R.id.add_date)
        time = findViewById(R.id.add_time)
        btn = findViewById(R.id.add_btn)
        back = findViewById(R.id.btn_back_add)

        date!!.isFocusable = false
        date!!.isClickable = true
        date!!.isLongClickable = false
        date!!.keyListener = null

        time!!.isFocusable = false
        time!!.isClickable = true
        time!!.isLongClickable = false
        time!!.keyListener = null

        date!!.setOnClickListener {
            showDate()
        }
        date!!.inputType = View.LAYER_TYPE_NONE

        time!!.setOnClickListener {
            showTime()
        }



        btn!!.setOnClickListener {
            val result = Bundle().apply {
                putString("ip1",title!!.text.toString())
            }
//            supportFragmentManager.setFragmentResult("request",result)
//            finish()

            val intent = Intent()
            intent.putExtra("frag","add_frag")
            intent.putExtra("title",title!!.text.toString())
            intent.putExtra("desc",desc!!.text.toString())
            intent.putExtra("date",date!!.text.toString())
            intent.putExtra("time",time!!.text.toString())
            setResult(Activity.RESULT_OK,intent)
            finish()
        }

        back!!.setOnClickListener {
            val i = Intent(this@add_event,home::class.java)
            i.putExtra("frag","add_frag")
            startActivity(i)
        }

    }

    private fun showDate(){
        val datePicker = DatePickerDialog(
            this,
            this,
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }
    private fun showTime(){
        val timePicker= TimePickerDialog(
            this,
            this,
            calender.get(Calendar.HOUR_OF_DAY),
            calender.get(Calendar.MINUTE),
            false
        )
        timePicker.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calender.set(Calendar.YEAR,year)
        calender.set(Calendar.MONTH,month)
        calender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
        updateDateEditText()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        calender.set(Calendar.HOUR_OF_DAY,hourOfDay)
        calender.set(Calendar.MINUTE,minute)
        updateTimeEditText()
    }

    private fun updateDateEditText(){
        val dateformate = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        date!!.setText(dateformate.format(calender.time))
    }

    private fun updateTimeEditText(){
        val timeFormate = SimpleDateFormat("hh:mm a", Locale.US)
        time!!.setText(timeFormate.format(calender.time))
    }

}