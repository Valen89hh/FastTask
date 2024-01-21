package com.valendev.fasttask.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.valendev.fasttask.domain.model.Task

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): Task

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId")
    suspend fun getTasksByCategoryId(categoryId: Long): List<Task>

    @Query("SELECT * FROM tasks WHERE LOWER(nameTask) LIKE '%' || LOWER(:query) || '%'")
    suspend fun getTasksByName(query: String): List<Task>

    @Query("SELECT * FROM tasks WHERE isTerminate = 1")
    suspend fun getTasksCompleted(): List<Task>

    @Query("SELECT * FROM tasks WHERE isTerminate = 0")
    suspend fun getPendingTasks(): List<Task>

    @Query("SELECT * FROM tasks WHERE dateCreate >= :date")
    suspend fun getTaskByDate(date: String): List<Task>

    @Query("SELECT * FROM tasks WHERE dateCreate = :date")
    suspend fun getTaskByCurrentDate(date: String): List<Task>

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getMountTasks(): Int

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}