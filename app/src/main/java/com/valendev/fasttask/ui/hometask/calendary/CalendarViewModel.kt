package com.valendev.fasttask.ui.hometask.calendary

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.android.material.datepicker.DateSelector
import com.valendev.fasttask.data.repository.CategoryRepository
import com.valendev.fasttask.data.repository.TaskRepository
import com.valendev.fasttask.domain.model.DateModel
import com.valendev.fasttask.domain.model.DayOffMonth
import com.valendev.fasttask.domain.viewmodel.TaskViewModel
import com.valendev.fasttask.resource.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.sql.DatabaseMetaData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : TaskViewModel(taskRepository, categoryRepository){

    private val _daysOffMonth = MutableStateFlow<List<DayOffMonth>>(emptyList())
    val daysOffMonth = _daysOffMonth

    private val _statusDataCalendar = MutableSharedFlow<Status<Int>>()
    val statusDataCalendar = _statusDataCalendar


    private val _daySelected = MutableStateFlow<Int?>(null)
    val daySelected = _daySelected

    private val _dateSelected = MutableStateFlow<DateModel?>(null)
    val dateSelected = _dateSelected

    fun updateDaysOffMonth(year: Int, month: Int, day: Int){
        viewModelScope.launch {
            try {
                _daysOffMonth.value = getDaysOffMonth(year, month)
                _statusDataCalendar.emit(Status.Success(day))
            }catch (e: Exception){
                _statusDataCalendar.emit(Status.Error(e.message.toString()))
            }

        }
    }

    fun updateDaysOffMonth(){
        viewModelScope.launch {
            val instanceCalendar = Calendar.getInstance()
            val year = instanceCalendar.get(Calendar.YEAR)
            val month = instanceCalendar.get(Calendar.MONTH)


            _daysOffMonth.value = getDaysOffMonth(year, month)
        }
    }

    fun updateTaskByDate(day: Int){
        if (_dateSelected.value != null){
            val year = _dateSelected.value!!.year
            val month = _dateSelected.value!!.month
            updateListDataTask(year, month, day)
        }
    }

    private fun getDaysOffMonth(year: Int, month: Int): List<DayOffMonth>{
        val calendar = GregorianCalendar(Locale.getDefault())
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val diasDelMes = mutableListOf<DayOffMonth>()

        val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())

        val instanceCalendar = Calendar.getInstance()

        while (calendar.get(Calendar.MONTH) == month) {
            val numeroDelDia = calendar.get(Calendar.DAY_OF_MONTH)
            val nombreDelDia = dateFormat.format(calendar.time)
            val currentDay = instanceCalendar.get(Calendar.MONTH) == month && instanceCalendar.get(Calendar.DAY_OF_MONTH) == numeroDelDia
            diasDelMes.add(DayOffMonth(numeroDelDia, nombreDelDia, currentDay))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        _dateSelected.value = DateModel(year=year, month = month)

        return diasDelMes
    }

    fun saveDayOffMonth(day: Int) = viewModelScope.launch {
        _daySelected.value = day
    }

}