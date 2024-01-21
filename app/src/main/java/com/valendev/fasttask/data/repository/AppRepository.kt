package com.valendev.fasttask.data.repository

import javax.inject.Inject

class AppRepository @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) {

    fun getCategoryRepo() = categoryRepository
    fun getTaskRepo() = taskRepository
    fun getUserRepo() = userRepository


}