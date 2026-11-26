package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "formulas")
data class Formula(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topic: String,
    val title: String,
    val equation: String,
    val isFavorite: Boolean = false,
    val legend: String = "" // JSON or newline separated string of variables
)
