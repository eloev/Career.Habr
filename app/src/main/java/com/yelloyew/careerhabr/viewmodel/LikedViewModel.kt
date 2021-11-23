package com.yelloyew.careerhabr.viewmodel

import androidx.lifecycle.ViewModel
import com.yelloyew.careerhabr.model.Vacancy
import com.yelloyew.careerhabr.repository.LikedRepository

class LikedViewModel: ViewModel() {

    private val likedRepository = LikedRepository.get()
    val likedListLiveData = likedRepository.getLiked()

    fun addLike(vacancy: Vacancy) {
        likedRepository.addLike(vacancy)
    }

    fun deleteLike(vacancy: Vacancy) {
        likedRepository.deleteLike(vacancy)
    }
}