package com.yelloyew.careerhabr.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.yelloyew.careerhabr.MainViewModel
import com.yelloyew.careerhabr.databinding.ItemVacancyBinding
import com.yelloyew.careerhabr.model.Vacancy
import com.yelloyew.careerhabr.ui.LikedFragmentDirections

class LikedRecyclerAdapter : RecyclerView.Adapter<LikedRecyclerAdapter.ViewHolder>() {
    private var vacancies: MutableList<Vacancy> = mutableListOf()

    fun setData(newVacancyList: MutableList<Vacancy>) {
        vacancies = newVacancyList
        notifyItemRangeInserted(0, vacancies.size)
    }

    fun deleteVacancy(position: Int) {
        MainViewModel().deleteLike(vacancies[position])
        notifyItemRangeRemoved(0, vacancies.size)
        vacancies.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemVacancyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vacancy = vacancies[position]
        holder.bind(vacancy)
    }

    override fun getItemCount() = vacancies.size

    inner class ViewHolder(private val bindingItem: ItemVacancyBinding) : RecyclerView.ViewHolder(bindingItem.root) {

        private lateinit var vacancy: Vacancy

        fun bind(vacancy: Vacancy) {
            this.vacancy = vacancy
            bindingItem.apply {
                companyName.text = vacancy.company
                vacancyDate.text = vacancy.date
                position.text = vacancy.position
                if (vacancy.salary.isNotBlank()) {
                    salary.text = vacancy.salary
                    salary.isVisible = true
                }
                skill.text = vacancy.skill
                metaInfo1.text = vacancy.metaInfo[0]
                if (vacancy.metaInfo.size >= 2) {
                    metaInfo2.text = vacancy.metaInfo[1]
                    metaInfo2.isVisible = true
                }
                if (vacancy.metaInfo.size == 3) {
                    metaInfo3.text = vacancy.metaInfo[2]
                    metaInfo3.isVisible = true
                }
            }
            Glide.with(itemView.context)
                .load(vacancy.logo)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(bindingItem.logo)

            itemView.setOnClickListener{
                val action = LikedFragmentDirections.actionLikedFragmentToVacancyFragment(vacancy)
                findNavController(it).navigate(action)
            }
        }
    }
}