package com.budgeteasy.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.budgeteasy.data.local.database.dao.UserDao
import com.budgeteasy.data.local.database.dao.BudgetDao
import com.budgeteasy.data.local.database.dao.ExpenseDao
import com.budgeteasy.data.local.database.entity.UserEntity
import com.budgeteasy.data.local.database.entity.BudgetEntity
import com.budgeteasy.data.local.database.entity.ExpenseEntity

@Database(
    entities = [UserEntity::class, BudgetEntity::class, ExpenseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BudgetDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun budgetDao(): BudgetDao
    abstract fun expenseDao(): ExpenseDao
}