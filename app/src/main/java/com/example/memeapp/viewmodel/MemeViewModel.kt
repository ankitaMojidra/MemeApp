package com.example.memeapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.memeapp.database.MemeDao
import com.example.memeapp.database.MemeDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class MemeViewModel @Inject constructor(application: Application, private val memeDao: MemeDao) :
    ViewModel() {
    private val database = MemeDatabase.getDatabase(application)
    val memes = flow {
        emit(database.memeDao().getALlMemes())
    }.flowOn(Dispatchers.IO).stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}