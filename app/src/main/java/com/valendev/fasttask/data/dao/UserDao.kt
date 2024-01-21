package com.valendev.fasttask.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.valendev.fasttask.domain.model.UserModel

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserModel): Long

    @Update
    suspend fun update(user: UserModel)

    @Delete
    suspend fun delete(user: UserModel)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): UserModel?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserModel>
}