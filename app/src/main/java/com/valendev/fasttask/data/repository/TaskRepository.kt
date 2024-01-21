package com.valendev.fasttask.data.repository

import com.valendev.fasttask.data.dao.TaskDao
import com.valendev.fasttask.domain.model.Category
import com.valendev.fasttask.domain.model.Task
import com.valendev.fasttask.domain.usecase.date.AppDate
import java.util.Date
import javax.inject.Inject

class TaskRepository @Inject constructor(private val taskDao: TaskDao) {

    suspend fun getAllTasks(): List<Task>{
        return taskDao.getAllTasks()
    }

    suspend fun getTask(id: Long): Task{
        return taskDao.getTaskById(id)
    }

    suspend fun getTasksByCategoryId(categoryId: Long): List<Task>{
        return taskDao.getTasksByCategoryId(categoryId)
    }

    suspend fun deleteTask(task: Task){
        taskDao.delete(task)
    }

    suspend fun updateTask(task: Task){
        taskDao.update(task)
    }

    suspend fun geMountTaskByCategory(categoryId: Long): Int{
        return getTasksByCategoryId(categoryId).size
    }

    suspend fun createTask(task: Task){
        taskDao.insert(task)
    }

    suspend fun searchTasks(query: String): List<Task>{
        return taskDao.getTasksByName(query)
    }

    suspend fun getMountTasks() = taskDao.getMountTasks()

    suspend fun getTaskByIsTerminate(terminate: Boolean): List<Task> = if (terminate) taskDao.getTasksCompleted() else taskDao.getPendingTasks()

    suspend fun getTaskByDate(date: String) = taskDao.getTaskByDate(date)

    suspend fun getTaskByCurrentDay(date: String) = taskDao.getTaskByCurrentDate(date)
}