package com.valendev.fasttask.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.valendev.fasttask.domain.model.Category

@Dao
interface CategoryDao {

    @Insert
    suspend fun insert(category: Category)

    @Insert
    suspend fun insertAll(categories: List<Category>)

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Long): Category
}