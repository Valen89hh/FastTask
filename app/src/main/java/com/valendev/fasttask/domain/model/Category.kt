package com.valendev.fasttask.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nameCategory: String,
    val imageCategory: Int,
    val color: Int,
    val colorSelected: Int
)