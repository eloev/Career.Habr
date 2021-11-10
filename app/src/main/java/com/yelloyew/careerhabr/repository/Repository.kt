package com.yelloyew.careerhabr.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yelloyew.careerhabr.model.Vacancy
import org.jsoup.Jsoup
import java.io.IOException

class Repository private constructor(context: Context) {

    private val listData = mutableListOf<Vacancy>()

    fun getVacanciesList(url: String) : MutableList<Vacancy> {
        try {
            val doc = Jsoup.connect(url).get()
            val vacancies = doc.select("div.vacancy-card")

            for ((i, vacancy) in vacancies.withIndex()) {

                val logo = vacancy.select("img.vacancy-card__icon")
                    .attr("src")

                val company = vacancy.select("div.vacancy-card__company-title")
                    .text()

                val position = vacancy.select("a.vacancy-card__title-link")
                    .text()

                val metaInfo = vacancy.select("div.vacancy-card__meta")
                    .text().split(" · ")

                val salary = vacancy.select("div.basic-salary")
                    .text()

                val skill = vacancy.select("div.vacancy-card__skills")
                    .text()

                val vacancyUrl = "https://career.habr.com" + vacancy.select("div.vacancy-card__title")
                    .select("a")
                    .attr("href")

                val date = vacancy.select("div.vacancy-card__date")
                    .text()

                listData.add(Vacancy(company, position, metaInfo, salary, skill, logo, date, vacancyUrl))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return listData
    }

    companion object{
        private var INSTANCE: Repository? = null

        fun initialize(context: Context){
            if (INSTANCE == null){
                INSTANCE = Repository(context)
            }
        }

        fun get(): Repository{
            return INSTANCE ?:
            throw IllegalStateException("Repository must be initialized")
        }
    }
}