package com.yelloyew.careerhabr.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Vacancy(
    var company: String = "",
    var position: String = "",
    var metaInfo: List<String> = emptyList(),
    var salary: String = "",
    var skill: String = "",
    var logo: String = "",
    var date: String = "",
    var url: String = "",
    var itemPosition: Int = 0
) : Parcelable