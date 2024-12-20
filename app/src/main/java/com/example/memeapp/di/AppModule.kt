package com.example.memeapp.di

import android.app.Application
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
    fun provideMemeDatabase(@ApplicationContext app:Application): MemeDatabase{
        return MemeDatabase.getDatabase(app)
    }

    @Provides
    @Singleton
    fun provideMemeViewModel(@ApplicationContext app:Application,db: MemeDatabase): MemeViewModel{
        return MemeViewModel(app,db.memeDao())
    }
}