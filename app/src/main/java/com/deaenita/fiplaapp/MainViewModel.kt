package com.deaenita.fiplaapp

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.deaenita.fiplaapp.database.AppDatabase
import com.deaenita.fiplaapp.database.Income
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    lateinit var db: AppDatabase

    fun initializeDatabase(applicationContext: Context) {
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    fun addIncome(id:Int, title: String, amount: String) {
        viewModelScope.launch {

            db.incomeDao().addIncome(Income(id,title, amount))
        }
    }
}