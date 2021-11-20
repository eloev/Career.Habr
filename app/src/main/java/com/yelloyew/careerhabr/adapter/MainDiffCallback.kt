package com.yelloyew.careerhabr.adapter

import androidx.recyclerview.widget.DiffUtil
import com.yelloyew.careerhabr.model.Vacancy

class VacancyDiffCallback(
    private val oldList: MutableList<Vacancy>,
    private val newList: MutableList<Vacancy>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldVacancy = oldList[oldItemPosition]
        val newVacancy = newList[newItemPosition]
        return oldVacancy.company == newVacancy.company
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldVacancy = oldList[oldItemPosition]
        val newVacancy = newList[newItemPosition]
        return oldVacancy == newVacancy
    }
}