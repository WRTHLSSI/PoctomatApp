package com.example.poctomatapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val login: String,
    val password: String,
    val fullName: String
)