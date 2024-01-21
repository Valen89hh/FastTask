package com.valendev.fasttask.data.repository

import com.valendev.fasttask.data.dao.CategoryDao
import com.valendev.fasttask.domain.model.Category
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val categoryDao: CategoryDao) {

    suspend fun createCategory(category: Category){
        categoryDao.insert(category)
    }

    suspend fun getAllCategories(): List<Category>{
        return categoryDao.getAllCategories()
    }

    suspend fun getCategory(id: Long): Category{
        return categoryDao.getCategoryById(id)
    }

}