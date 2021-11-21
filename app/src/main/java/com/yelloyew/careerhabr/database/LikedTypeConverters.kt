package com.yelloyew.careerhabr.database

import androidx.room.TypeConverter


class LikedTypeConverters {

    @TypeConverter
    fun fromMetaInfo(metaInfo: List<String>): String {
        return metaInfo.joinToString(",")
    }

    @TypeConverter
    fun toMetaInfo(data: String): List<String> {
        return data.split(",")
    }

}