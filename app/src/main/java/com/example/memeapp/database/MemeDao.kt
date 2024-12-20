package com.example.memeapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MemeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meme: Meme)

    @Query("SELECT * FROM memes")
    suspend fun getALlMemes(): List<Meme>
}