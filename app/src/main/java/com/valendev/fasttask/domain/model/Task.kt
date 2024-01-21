package com.valendev.fasttask.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.OffsetDateTime
import java.util.Date

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var nameTask: String,
    var description: String,
    var isTerminate: Boolean = false,
    var dateCreate: String,
    var dateUpdate: String,
    var startTime: String,
    var endTime: String,
    var categoryId: Long
)
