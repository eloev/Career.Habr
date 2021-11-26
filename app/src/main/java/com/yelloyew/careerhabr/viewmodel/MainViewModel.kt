package com.yelloyew.careerhabr.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yelloyew.careerhabr.model.Vacancy
import com.yelloyew.careerhabr.network.JsoupAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val jsoupAdapter = JsoupAdapter.get()
    private var vacancies: MutableLiveData<MutableList<Vacancy>> = MutableLiveData()
    private var vacancyInfo: MutableLiveData<String> = MutableLiveData()

    private var newRequest = ""

    var page: Int = 1

    fun getData(request: String): MutableLiveData<MutableList<Vacancy>> {
        viewModelScope.launch(Dispatchers.IO) {
            if (request != newRequest) {
                jsoupAdapter.eraseList()
                page = 1
                newRequest = request
            }
            vacancies.postValue(jsoupAdapter.getVacancies("$request&page=$page"))
            Log.d("tag3", "$request&page=$page")
        }
        return vacancies
    }

    fun eraseList(){
        jsoupAdapter.eraseList()
    }

    fun getCurrentVacancyInfo(currentVacancyUrl: String): MutableLiveData<String> {
        viewModelScope.launch(Dispatchers.IO) {
            vacancyInfo.postValue(jsoupAdapter.getCurrentVacancyInfo(currentVacancyUrl))
        }
        return vacancyInfo
    }
}