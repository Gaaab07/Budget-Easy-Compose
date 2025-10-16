package com.budgeteasy.di

import android.content.Context
import androidx.room.Room
import com.budgeteasy.data.local.database.BudgetDatabase
import com.budgeteasy.data.local.database.dao.UserDao
import com.budgeteasy.data.local.database.dao.BudgetDao
import com.budgeteasy.data.local.database.dao.ExpenseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideBudgetDatabase(
        @ApplicationContext context: Context
    ): BudgetDatabase {
        return Room.databaseBuilder(
            context,
            BudgetDatabase::class.java,
            "budget_easy_db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideUserDao(database: BudgetDatabase): UserDao {
        return database.userDao()
    }

    @Singleton
    @Provides
    fun provideBudgetDao(database: BudgetDatabase): BudgetDao {
        return database.budgetDao()
    }

    @Singleton
    @Provides
    fun provideExpenseDao(database: BudgetDatabase): ExpenseDao {
        return database.expenseDao()
    }
}