package com.yelloyew.careerhabr.repository

import android.app.Application

class VacancyIntent : Application() {
    override fun onCreate() {
        super.onCreate()
        Repository.initialize(this)
        LikedRepository.initialize(this)
    }
}