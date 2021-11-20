package com.yelloyew.careerhabr.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yelloyew.careerhabr.model.Vacancy
import java.util.*

@Dao
interface LikedDao {
    @Query("SELECT * FROM vacancy")
    fun getLiked(): LiveData<List<Vacancy>>
    @Query("SELECT * FROM vacancy WHERE url=(:url)")
    fun getLike(url: String) : LiveData<Vacancy?>
    @Insert
    fun addLike(vacancy: Vacancy)
    @Query("DELETE FROM vacancy WHERE url=(:url)")
    fun deleteLike(url: String)
    @Update
    fun updateLike(vacancy: Vacancy)
}