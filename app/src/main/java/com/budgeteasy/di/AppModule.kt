// Si ya tienes un AppModule.kt, AGREGA estas líneas:
// Si NO tienes, CREA este archivo en: di/AppModule.kt

package com.budgeteasy.di

import android.content.Context
import com.budgeteasy.data.preferences.LanguageManager
import com.budgeteasy.data.preferences.ThemeManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideThemeManager(
        @ApplicationContext context: Context
    ): ThemeManager {
        return ThemeManager(context)
    }

    @Provides
    @Singleton
    fun provideLanguageManager(
        @ApplicationContext context: Context
    ): LanguageManager {
        return LanguageManager(context)
    }
}