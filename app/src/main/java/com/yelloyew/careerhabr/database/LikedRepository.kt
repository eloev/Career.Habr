package com.yelloyew.careerhabr.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.yelloyew.careerhabr.database.LikedDatabase
import com.yelloyew.careerhabr.model.Vacancy
import java.lang.Exception
import java.util.concurrent.Executors

private const val DATABASE_NAME = "liked_vacancy"

class LikedRepository private constructor(context: Context){

    private val database : LikedDatabase = Room.databaseBuilder(
        context.applicationContext,
        LikedDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val likedDao = database.likedDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getLiked() : LiveData<MutableList<Vacancy>> = likedDao.getLiked()

    //fun getLike(url: String) : LiveData<Vacancy?> = likedDao.getLike(url)

    fun addLike(vacancy: Vacancy) {
        executor.execute{
            try {
                likedDao.addLike(vacancy)
            }
            catch (e: Exception){
                Log.d("tagR", e.toString())
            }
        }
    }

    fun deleteLike(vacancy: Vacancy){
        executor.execute{
            likedDao.deleteLike(vacancy.url)
        }
    }

//    fun updateLike(vacancy: Vacancy){
//        executor.execute{
//            likedDao.updateLike(vacancy)
//        }
//    }

    companion object{
        private var INSTANCE: LikedRepository? = null

        fun initialize(context: Context){
            if (INSTANCE == null){
                INSTANCE = LikedRepository(context)
            }
        }

        fun get(): LikedRepository{
            return INSTANCE ?:
            throw IllegalStateException("LikedRepository must be initialized")
        }
    }
}