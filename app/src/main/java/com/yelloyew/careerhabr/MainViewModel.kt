package com.yelloyew.careerhabr

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.yelloyew.careerhabr.model.Vacancy
import com.yelloyew.careerhabr.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {

    private val url = "https://career.habr.com/vacancies?type=all&per_page=15"
    private val repository = Repository.get()
    private var vacancies: MutableLiveData<MutableList<Vacancy>> = MutableLiveData()

    private var response = ""
    private var newResponse = ""

    var page: Int = 1
    var query = "kotlin"
    var remote = ""
    var salary = ""
    var qid = ""

    fun getData(): MutableLiveData<MutableList<Vacancy>> {
        viewModelScope.launch(Dispatchers.IO) {
            response = "&q=$query&remote=$remote&salary=$salary&qid=$qid"
            if ( response != newResponse){
                repository.eraseList()
                page = 1
                newResponse = response
            }
            vacancies.postValue(repository.getVacanciesList(("$url$response&page=$page"), page))
            Log.d("tag3", "$url$response&page=$page")
        }
        return vacancies
    }
}