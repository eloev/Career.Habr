package com.yelloyew.careerhabr.model

data class Vacancy(
    var company: String = "",
    var position: String = "",
    var metaInfo: List<String> = emptyList(),
    var salary: String = "",
    var skill: String = "",
    var logo: String = "",
    var date: String = "",
    var url: String = ""
)