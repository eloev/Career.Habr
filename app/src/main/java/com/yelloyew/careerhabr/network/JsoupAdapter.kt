package com.yelloyew.careerhabr.network

import android.content.Context
import com.yelloyew.careerhabr.model.Vacancy
import org.jsoup.Jsoup
import java.io.IOException

private const val URL = "https://career.habr.com/vacancies?type=all&sort=date&per_page=15"

class JsoupAdapter private constructor(context: Context) {

    private var listData = mutableListOf<Vacancy>()
    private var vacancyInfo = ""

    fun getVacancies(response: String) : MutableList<Vacancy> {
        try {
            val doc = Jsoup.connect(URL + response).get()
            val vacancies = doc.select("div.vacancy-card")

            for ((i, vacancy) in vacancies.withIndex()) {

                val vacancyUrl = "https://career.habr.com" + vacancy.select("div.vacancy-card__title")
                    .select("a")
                    .attr("href")

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

                val date = vacancy.select("div.vacancy-card__date")
                    .text()
                listData.add(Vacancy(vacancyUrl, company, position, metaInfo, salary, skill, logo, date))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return listData
    }

    fun eraseList() {
        listData = mutableListOf()
    }

    fun getCurrentVacancyInfo(url: String) : String{
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

    fun getUrlVacancy(response: String) : MutableList<String> {
        val data = mutableListOf<String>()
        try {
            val doc = Jsoup.connect(URL + response).get()
            val vacancies = doc.select("div.vacancy-card")

            for ((i, vacancy) in vacancies.withIndex()) {

                val vacancyUrl = "https://career.habr.com" + vacancy.select("div.vacancy-card__title")
                    .select("a")
                    .attr("href")
                data.add(vacancyUrl)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return data
    }

    companion object{
        private var INSTANCE: JsoupAdapter? = null

        fun initialize(context: Context){
            if (INSTANCE == null){
                INSTANCE = JsoupAdapter(context)
            }
        }

        fun get(): JsoupAdapter{
            return INSTANCE ?:
            throw IllegalStateException("JsoupAdapter must be initialized")
        }
    }
}