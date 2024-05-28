package com.deaenita.fiplaapp.database

import androidx.room.*

@Dao
interface IncomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addIncome(income: Income)

    @Query("SELECT * FROM Income")
    suspend fun getAllIncomes(): List<Income>
}

//@Dao
//interface ExpenseDao {
//    @Query("SELECT * FROM expense")
//    fun getAll(): List<Expense>
//
//    @Insert
//    fun insert(expense: Expense)
//
//    @Delete
//    fun delete(expense: Expense)
//}
