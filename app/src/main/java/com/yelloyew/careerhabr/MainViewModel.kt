package com.yelloyew.careerhabr

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.yelloyew.careerhabr.model.Vacancy
import com.yelloyew.careerhabr.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {

    private val url = "https://career.habr.com/vacancies?q=kotlin&type=all&per_page=10&page="
    private val repository = Repository.get()
    var vacancies: MutableLiveData<MutableList<Vacancy>> = MutableLiveData()
    var page: Int = 1

    fun getData(): MutableLiveData<MutableList<Vacancy>> {
        viewModelScope.launch(Dispatchers.IO) {
            vacancies.postValue(repository.getVacanciesList("$url+${page.toString()}"))
        }
        return vacancies
    }
}