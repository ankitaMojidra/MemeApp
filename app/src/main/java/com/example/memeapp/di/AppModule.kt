package com.example.memeapp.di

import android.app.Application
import android.content.Context
import com.example.memeapp.database.MemeDao
import com.example.memeapp.database.MemeDatabase
import com.example.memeapp.viewmodel.MemeViewModel
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
    fun provideMemeDatabase(@ApplicationContext context: Context): MemeDatabase {
        return MemeDatabase.getDatabase(context)
    }

    @Provides
    fun provideMemeDao(database: MemeDatabase): MemeDao {
        return database.memeDao()
    }
}