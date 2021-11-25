package com.yelloyew.careerhabr.utils

import android.app.Application
import com.yelloyew.careerhabr.database.LikedRepository
import com.yelloyew.careerhabr.network.JsoupAdapter

class VacancyIntent : Application() {
    override fun onCreate() {
        super.onCreate()
        JsoupAdapter.initialize(this)
        LikedRepository.initialize(this)
    }
}