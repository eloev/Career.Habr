package com.yelloyew.careerhabr.utils

import android.app.PendingIntent
import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yelloyew.careerhabr.MainActivity
import com.yelloyew.careerhabr.R
import com.yelloyew.careerhabr.network.JsoupAdapter

private const val TAG = "worker"

class PollWorker(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val jsoupAdapter = JsoupAdapter.get()
    private var notifyText = ""

    override fun doWork(): Result {
        var query = ""
        var salary = ""
        var remote = ""
        var skill = ""

        val resources = context.resources

        val notifyRequest =
            RequestPreferences.getNotifyRequest(context).split(",").toTypedArray()
        if (notifyRequest.size == 4) {
            Log.d(TAG, notifyRequest.contentToString())
            query = notifyRequest[0]
            remote = notifyRequest[1]
            salary = notifyRequest[2]
            skill = notifyRequest[3]
        }
        val vacancies =
            jsoupAdapter.getUrlVacancy("&q=$query&remote=$remote&salary=$salary&qid=$skill")
        val lastUrls = RequestPreferences.getLastUrls(context).split(",")

        //счётчик количества уведомлений
        var notificationCounter = vacancies.size - 1
        for (i in lastUrls.indices - 1) {
            for (y in vacancies.indices - 1) {
                if (vacancies[y] == lastUrls[i]) {
                    notificationCounter -= 1
                }
            }
        }

        val intent = MainActivity.newIntent(context)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        when {
            notificationCounter <= 0 -> {
                notificationCounter = 0
            }
            notificationCounter == 1 -> {
                notifyText = resources.getString(R.string.notify_vacancy)
                createNotify(resources, pendingIntent)
            }
            notificationCounter in 2..4 -> {
                notifyText =
                    resources.getString(R.string.notify_vacancies_start) + " " + notificationCounter.toString() +
                            " " + resources.getString(R.string.notify_vacancies_2_4)
                createNotify(resources, pendingIntent)
            }
            notificationCounter in 5..10 -> {
                notifyText =
                    resources.getString(R.string.notify_vacancies_start) + " " + notificationCounter.toString() +
                            " " + resources.getString(R.string.notify_vacancies_5_10)
                createNotify(resources, pendingIntent)
            }
            else -> {
                notifyText = resources.getString(R.string.notify_vacancies_more)
                createNotify(resources, pendingIntent)
            }
        }

        Log.d(TAG, notifyText)

        var urlsToSave = ""
        vacancies.map { urlsToSave += "${it}," }
        RequestPreferences.setLastUrls(context, urlsToSave)

        return Result.success()
    }

    private fun createNotify(resources: Resources, pendingIntent: PendingIntent) {
        val notification = NotificationCompat
            .Builder(context, CHANNEL_ID)
            .setTicker(resources.getString(R.string.notify_title))
            .setSmallIcon(R.drawable.ic_for_notify)
            .setContentText(notifyText)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(0, notification)
    }
}