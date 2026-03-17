package com.example.poctomatapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cells")
data class CellEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val postamatId: Int,
    val number: Int,
    val size: String, // ← добавили
    val isOccupied: Boolean = false,
    val trackNumber: String? = null,
    val expiryDate: String? = null
)