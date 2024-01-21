package com.valendev.fasttask.domain.usecase.date

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AppDate(private val activity: Activity) {

    val currentDate: Date = Calendar.getInstance().time

    fun showDatePickerDialog(handleAccept: (view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int)-> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            activity,
            DatePickerDialog.OnDateSetListener(handleAccept),
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
    
     fun showTimePickerDialog(handleAccept: (view: TimePicker, hourOfDay: Int, minute: Int)->Unit){
         val calendar = Calendar.getInstance()

         val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
         val currentMinute = calendar.get(Calendar.MINUTE)
         
         val timePickerDialog = TimePickerDialog(
             activity,
             TimePickerDialog.OnTimeSetListener(handleAccept),
             currentHour,
             currentMinute,
             false
         )
         
         timePickerDialog.show()
     }

    fun formatTimeToString(time: Date): String{
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(time)
    }

    fun formatTimeToString(hour: Int, minute: Int): String{
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(calendar.time)
    }

    fun formatDateToString(date: Date): String{
        val format = SimpleDateFormat("d/MM/yyyy", Locale.getDefault())
        return format.format(date)
    }

    fun convertStringToDateTime(dateString: String): Date{
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.parse(dateString) ?: Date()
    }

    fun convertStringToDate(date: String): Date{
        val format = SimpleDateFormat("d/MM/yyyy", Locale.getDefault())
        return format.parse(date) ?: Date()
    }

    fun sumDate(date1: Date, date2: Date): Date{
        return Date(date1.time + date2.time)
    }

    fun addHourToDate(date: Date, hours: Int): Date{
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.HOUR_OF_DAY, hours)
        return calendar.time
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isBefore(date1: Date, date2: Date): Boolean{
        val dateCompare = date1.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        val dateComparation = date2.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()

        return dateCompare.isBefore(dateComparation)
    }
}