package com.yelloyew.careerhabr.utils

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

private const val PREF_SEARCH_REQUEST = "searchRequest"

object RequestPreferences {

    fun getStoredRequest(context: Context):String{
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(PREF_SEARCH_REQUEST, "")!!
    }

    fun setStoredRequest(context: Context, response: String){
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit { putString(PREF_SEARCH_REQUEST, response) }
    }
}