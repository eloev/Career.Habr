package com.yelloyew.careerhabr.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yelloyew.careerhabr.model.Vacancy

@Database(entities = [ Vacancy::class], version=1)
@TypeConverters(LikedTypeConverters::class)
abstract class LikedDatabase : RoomDatabase() {

    abstract fun likedDao() : LikedDao
}