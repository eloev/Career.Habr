package com.yelloyew.careerhabr.utils

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

private const val PREF_SEARCH_REQUEST = "searchRequest"
private const val PREF_NOTIFY_REQUEST = "notifyRequest"
private const val PREF_NOTIFY_LAST_URLS = "lastUrls"

object RequestPreferences {

    fun getStoredRequest(context: Context):String{
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(PREF_SEARCH_REQUEST, "")!!
    }

    fun setStoredRequest(context: Context, response: String){
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit { putString(PREF_SEARCH_REQUEST, response) }
    }

    fun getNotifyRequest(context: Context):String{
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(PREF_NOTIFY_REQUEST, "")!!
    }

    fun setNotifyRequest(context: Context, response: String){
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit { putString(PREF_NOTIFY_REQUEST, response) }
    }

    fun getLastUrls(context: Context):String{
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(PREF_NOTIFY_LAST_URLS, "")!!
    }

    fun setLastUrls(context: Context, response: String){
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit { putString(PREF_NOTIFY_LAST_URLS, response) }
    }
}