package com.valendev.fasttask.extension

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun String.toTime(): Date{
    val spf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return spf.parse(this) ?: Date()
}

fun String.toDate(): Date{
    val spf = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
    return spf.parse(this) ?: Date()
}

fun Date.toStringDate():String{
    val spf = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
    return spf.format(this)
}

fun Date.toStringTime(): String{
    val spf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return spf.format(this)
}

fun Date.addHour(hours: Int): Date{
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.HOUR_OF_DAY, hours)
    return calendar.time
}