package com.yelloyew.careerhabr.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Vacancy(
    @PrimaryKey var url: String = "",
    var company: String = "",
    var position: String = "",
    var metaInfo: List<String> = emptyList(),
    var salary: String = "",
    var skill: String = "",
    var logo: String = "",
    var date: String = "",
    var itemPosition: Int = 0
) : Parcelable