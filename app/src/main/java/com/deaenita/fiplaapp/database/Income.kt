package com.deaenita.fiplaapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Income(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: String
)

//@Entity(tableName = "expense")
//data class Expense(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    val title: String,
//    val amount: Double
//)