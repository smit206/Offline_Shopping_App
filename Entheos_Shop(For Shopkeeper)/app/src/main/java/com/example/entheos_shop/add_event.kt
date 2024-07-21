package com.example.entheos_shop

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TimePicker
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    private lateinit var firabaseAuth:FirebaseAuth
    private lateinit var firebaseDatabase:FirebaseDatabase
    private lateinit var databaseReference:DatabaseReference

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

        firabaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("Events")

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
            val currentUserId = firabaseAuth.currentUser?.uid

            val reference = firebaseDatabase.reference.child("shops").child(currentUserId!!)

            reference.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){

                        val newEvent = Eventdata(
                            title!!.text.toString(),
                            desc!!.text.toString(),
                            date!!.text.toString(),
                            time!!.text.toString(),
                            currentUserId
                        )
                        addEventToFirebase(newEvent)
                        Toast.makeText(this@add_event, "Event added successfully", Toast.LENGTH_SHORT).show()
                        title!!.setText("")
                        desc!!.setText("")
                        date!!.setText("")
                        time!!.setText("")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@add_event, "Failed to send Event!!", Toast.LENGTH_SHORT).show()
                }

            })
            val i = Intent(this@add_event,home::class.java)
            i.putExtra("frag","add_frag")
            startActivity(i)
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

    private fun addEventToFirebase(eventdata: Eventdata){
        val databaseReference = firebaseDatabase.reference.child("Events").push()
        databaseReference.setValue(eventdata)
    }

}

data class Eventdata(
    val name:String = "",
    val discn:String = "",
    val date:String = "",
    val time:String = "",
    val senderId:String = ""
)