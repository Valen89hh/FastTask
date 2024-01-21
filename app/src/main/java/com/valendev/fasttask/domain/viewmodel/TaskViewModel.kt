package com.valendev.fasttask.domain.viewmodel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valendev.fasttask.data.repository.CategoryRepository
import com.valendev.fasttask.data.repository.TaskRepository
import com.valendev.fasttask.domain.model.DayOffMonth
import com.valendev.fasttask.domain.model.Task
import com.valendev.fasttask.domain.model.TaskData
import com.valendev.fasttask.domain.usecase.date.AppDate
import com.valendev.fasttask.extension.toStringDate
import com.valendev.fasttask.resource.Status
import com.valendev.fasttask.resource.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.Year
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
open class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
): ViewModel() {

    // Stat flow
    private val _dataTasks = MutableStateFlow<List<TaskData>>(emptyList())
    val dataTasks = _dataTasks

    private val _statusUpdateItemTask = MutableSharedFlow<Status<TaskData>>()
    val statusUpdateItemTask = _statusUpdateItemTask

    private val _statusDeleteTask = MutableSharedFlow<Status<String>>()
    val statusDeleteTask = _statusDeleteTask

    fun updateListDataTask(){
        viewModelScope.launch(Dispatchers.IO) {
            val tasksData = mutableListOf<TaskData>()
            val tasks = taskRepository.getAllTasks()

            tasks.forEach {task ->
                val category = categoryRepository.getCategory(task.categoryId)
                val dataTask = TaskData(
                    task = task,
                    image = category.imageCategory
                )
                tasksData.add(dataTask)
            }
            withContext(Dispatchers.Main){
                _dataTasks.value = tasksData
            }
        }
    }

    fun updateListDataTask(categoryId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val tasks = taskRepository.getTasksByCategoryId(categoryId)
            val tasksData = convertTaskToDataTask(tasks)

            withContext(Dispatchers.Main){
                _dataTasks.value = tasksData
            }
        }
    }

    fun updateListDataTask(status: TaskStatus){
        viewModelScope.launch(Dispatchers.IO) {
            val tasks: List<TaskData> = when(status){
                TaskStatus.NEW -> {
                    val currentDate = Calendar.getInstance().time.toStringDate()
                    val newTasks = taskRepository.getTaskByDate(currentDate)
                    Log.d("NEWASKF", currentDate)
                    Log.d("NEWASKF", newTasks.toString())
                    convertTaskToDataTask(newTasks)
                }
                TaskStatus.PENDING -> {
                    val pendingTasks = taskRepository.getTaskByIsTerminate(false)
                    convertTaskToDataTask(pendingTasks)
                }
                TaskStatus.COMPLETED -> {
                    val completedTasks = taskRepository.getTaskByIsTerminate(true)
                    convertTaskToDataTask(completedTasks)
                }
            }
            withContext(Dispatchers.Main){
                _dataTasks.value = tasks
            }
        }
    }

    fun updateListDataTask(search: String){
        viewModelScope.launch(Dispatchers.IO) {
            val tasks = taskRepository.searchTasks(search)
            val taskSearch = convertTaskToDataTask(tasks)
            withContext(Dispatchers.Main){
                _dataTasks.value = taskSearch
            }
        }
    }

    fun updateListDataTask(year: Int, month: Int, day: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            val tasks = taskRepository.getTaskByCurrentDay(calendar.time.toStringDate())
            Log.d("LJDLJSD", calendar.time.toStringDate())

            val taskSearch = convertTaskToDataTask(tasks)
            withContext(Dispatchers.Main){
                _dataTasks.value = taskSearch
            }
        }
    }

    fun updateTerminateTask(isTerminate: Boolean, taskId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val task = taskRepository.getTask(taskId)
                task.isTerminate = isTerminate
                taskRepository.updateTask(task)
                Log.d("TAflajdfldf", task.toString())
                val category = categoryRepository.getCategory(task.categoryId)
                val updateTaskData = TaskData(
                    task = task,
                    image = category.imageCategory
                )
                withContext(Dispatchers.Main){
                    _statusUpdateItemTask.emit(Status.Success(updateTaskData))
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    _statusUpdateItemTask.emit(Status.Error(e.message.toString()))
                }
            }

        }
    }

    fun deleteTask(taskId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val taskDelete = taskRepository.getTask(taskId)
                taskRepository.deleteTask(taskDelete)
                withContext(Dispatchers.Main){
                    _statusDeleteTask.emit(Status.Success("Task deleted successfully"))
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    _statusDeleteTask.emit(Status.Error(e.message.toString()))
                }
            }

        }
    }


    private suspend fun convertTaskToDataTask(taskList: List<Task>): List<TaskData>  = taskList.map { task ->
        val category = categoryRepository.getCategory(task.categoryId)
        TaskData(
            task = task,
            image = category.imageCategory
        )
    }
}