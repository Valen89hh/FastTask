package com.valendev.fasttask.domain.model

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.valendev.fasttask.domain.converters.Converters

@Entity(tableName = "users")
data class UserModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var lastName: String,
    var description: String,
    var image: String? = null
)
