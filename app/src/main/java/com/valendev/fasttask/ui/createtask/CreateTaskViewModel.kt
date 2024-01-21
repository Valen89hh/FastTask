package com.valendev.fasttask.ui.createtask

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valendev.fasttask.data.repository.CategoryRepository
import com.valendev.fasttask.data.repository.TaskRepository
import com.valendev.fasttask.domain.model.Category
import com.valendev.fasttask.domain.model.Task
import com.valendev.fasttask.domain.model.TaskData
import com.valendev.fasttask.resource.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val taskRepository: TaskRepository
): ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories

    private val _statusDb = MutableSharedFlow<Status<String>>()
    val statusDb = _statusDb

    private val _taskUpdate = MutableSharedFlow<Status<Task>>()
    val taskUpdate = _taskUpdate

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _categories.value = categoryRepository.getAllCategories()
        }
    }

    fun createTask(nameTask: String, description: String, dateCreate: String, dateUpdate: String, startTime: String, endTime: String, categoryId: Long?){

        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskRepository.createTask(Task(
                    nameTask = nameTask,
                    description = description,
                    dateUpdate = dateUpdate,
                    dateCreate = dateCreate,
                    startTime = startTime,
                    endTime = endTime,
                    categoryId = categoryId ?: _categories.value[0].id
                ))
                _statusDb.emit(Status.Success("The task: $nameTask has been created successfully"))
            }catch (e: Exception){
                _statusDb.emit(Status.Error(e.message.toString()))
            }

        }
    }

    fun getTaskForUpdate(taskUpdateId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val task = taskRepository.getTask(taskUpdateId)
                Log.d("IDLKFKLLKJFAS", task.toString())
                withContext(Dispatchers.Main){
                    _taskUpdate.emit(Status.Success(task))

                }
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    _taskUpdate.emit(Status.Error(e.message.toString()))

                }
            }

        }
    }

    fun saveTask(task: Task){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskRepository.updateTask(task)
                _statusDb.emit(Status.Success("Task updated successfully"))
            }catch (e: Exception){
                _statusDb.emit(Status.Error(e.message.toString()))
            }
        }
    }


}