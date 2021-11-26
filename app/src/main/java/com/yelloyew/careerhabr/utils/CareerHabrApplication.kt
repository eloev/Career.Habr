package com.yelloyew.careerhabr.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.yelloyew.careerhabr.R
import com.yelloyew.careerhabr.database.LikedRepository
import com.yelloyew.careerhabr.network.JsoupAdapter

const val CHANNEL_ID = "career_pool"

class CareerHabrApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        JsoupAdapter.initialize(this)
        LikedRepository.initialize(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notify_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}