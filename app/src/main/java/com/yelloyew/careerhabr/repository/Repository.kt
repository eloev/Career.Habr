package com.yelloyew.careerhabr.repository

import android.content.Context
import android.util.Log
import com.yelloyew.careerhabr.model.Vacancy
import org.jsoup.Jsoup
import java.io.IOException

class Repository private constructor(context: Context) {

    private var listData = mutableListOf<Vacancy>()
    private var vacancyInfo = ""

    fun getVacanciesList(url: String, page: Int) : MutableList<Vacancy> {
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
                val itemPosition = i + 1 + 15*(page-1)
                listData.add(Vacancy(company, position, metaInfo, salary, skill, logo, date, vacancyUrl, itemPosition))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return listData
    }

    fun eraseList() {
        listData = mutableListOf<Vacancy>()
    }

    fun currentVacancyInfo(url: String) : String{
        try {
            val doc = Jsoup.connect(url).get()
            val string= doc.select("div.style-ugc").text()
            val strong = doc.select("div.style-ugc strong").text()
            val words = strong.split(":").toTypedArray()
            for (i in words.indices){
                words[i] = words[i].replace(" ", "")
            }
            val result = arrayOfNulls<String>(words.size+1)
            result[0] = string

            for (i in words.indices){
                result[i+1] = result[i]!!.replaceFirst(words[i], "\n" + words[i]).trim()
            }
            vacancyInfo = result.last().toString().prependIndent("     ")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return vacancyInfo
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