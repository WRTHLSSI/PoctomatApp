package com.example.poctomatapp

data class Cell(
    val id: Int,
    var isOccupied: Boolean = false,
    var trackNumber: String? = null,
    var expiryDate: String? = null
)