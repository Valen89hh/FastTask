package com.valendev.fasttask.data.repository

import android.util.Log
import com.valendev.fasttask.data.dao.UserDao
import com.valendev.fasttask.domain.model.UserModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class UserRepository @Inject constructor(private val userDao: UserDao) {

    suspend fun createUser(user: UserModel): Int{
        val id = userDao.insert(user)
        return id.toInt()
    }

    suspend fun updateUser(user: UserModel){
        userDao.update(user)
    }

    suspend fun deleteUser(user: UserModel){
        userDao.delete(user)
    }

    suspend fun getUser(id: Int): UserModel?{
        return userDao.getUserById(id)
    }

    suspend fun getAllUsers(): List<UserModel>{
        return userDao.getAllUsers()
    }
}