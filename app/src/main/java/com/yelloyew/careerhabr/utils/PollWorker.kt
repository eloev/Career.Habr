package com.yelloyew.careerhabr.utils

import android.content.Context
import android.text.Editable
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yelloyew.careerhabr.model.Vacancy
import com.yelloyew.careerhabr.network.JsoupAdapter
import kotlin.math.absoluteValue

private const val TAG = "worker"

class PollWorker(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val jsoupAdapter = JsoupAdapter.get()
    private var vacancies = mutableListOf<String>()

    private var query = ""
    private var salary = ""
    private var remote = ""
    private var skill = ""

    override fun doWork(): Result {

        val notifyRequest =
            RequestPreferences.getNotifyRequest(context).split(",").toTypedArray()
        if (notifyRequest.size == 4) {
            Log.d(TAG, notifyRequest.contentToString())
            query = notifyRequest[0]
            remote = notifyRequest[1]
            salary = notifyRequest[2]
            skill = notifyRequest[3]
        }
        vacancies = jsoupAdapter.getUrlVacancy("&q=$query&remote=$remote&salary=$salary&qid=$skill")

        val lastUrls = RequestPreferences.getLastUrls(context).split(",")

        //счётчик количества уведомлений
        var notificationCounter = vacancies.size-1
        for (i in lastUrls.indices-1) {
            for (y in vacancies.indices-1) {
                if (vacancies[y] == lastUrls[i]) {
                    notificationCounter -= 1
                }
            }
        }
        if (notificationCounter <= 0){
            notificationCounter = 0
            Log.d(TAG, "op kak horosho poluchilos'")
        }
        Log.d(TAG, notificationCounter.toString())
        Log.d(TAG, lastUrls.size.toString())
        Log.d(TAG, vacancies.size.toString())

        var urlsToSave = ""
        vacancies.map { urlsToSave += "${it}," }
        RequestPreferences.setLastUrls(context, urlsToSave)

        return Result.success()
    }
}