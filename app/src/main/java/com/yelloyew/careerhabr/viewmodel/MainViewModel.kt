package com.yelloyew.careerhabr.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yelloyew.careerhabr.model.Vacancy
import com.yelloyew.careerhabr.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val url = "https://career.habr.com/vacancies?type=all&sort=date&per_page=15"
    private val repository = Repository.get()
    private var vacancies: MutableLiveData<MutableList<Vacancy>> = MutableLiveData()
    private var vacancyInfo: MutableLiveData<String> = MutableLiveData()

    private var newResponse = ""

    var page: Int = 1

    fun getData(response: String): MutableLiveData<MutableList<Vacancy>> {
        viewModelScope.launch(Dispatchers.IO) {
            if (response != newResponse) {
                eraseList()
                page = 1
                newResponse = response
            }
            vacancies.postValue(repository.getVacanciesList(("$url$response&page=$page"), page))
            Log.d("tag3", "$url$response&page=$page")
        }
        return vacancies
    }

    fun eraseList(){
        repository.eraseList()
    }

    fun getCurrentVacancyInfo(currentVacancyUrl: String): MutableLiveData<String> {
        viewModelScope.launch(Dispatchers.IO) {
            vacancyInfo.postValue(repository.currentVacancyInfo(currentVacancyUrl))
        }
        return vacancyInfo
    }
}