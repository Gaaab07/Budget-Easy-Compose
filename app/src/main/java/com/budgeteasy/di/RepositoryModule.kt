package com.budgeteasy.di



import com.budgeteasy.data.repository.BudgetRepository
import com.budgeteasy.data.repository.ExpenseRepository
import com.budgeteasy.data.repository.UserRepository
import com.budgeteasy.domain.repository.IBudgetRepository
import com.budgeteasy.domain.repository.IExpenseRepository
import com.budgeteasy.domain.repository.IUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindUserRepository(
        userRepository: UserRepository
    ): IUserRepository

    @Singleton
    @Binds
    abstract fun bindBudgetRepository(
        budgetRepository: BudgetRepository
    ): IBudgetRepository

    @Singleton
    @Binds
    abstract fun bindExpenseRepository(
        expenseRepository: ExpenseRepository
    ): IExpenseRepository
}