package com.yelloyew.careerhabr.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.yelloyew.careerhabr.MainViewModel
import com.yelloyew.careerhabr.databinding.FragmentMainBinding
import com.yelloyew.careerhabr.databinding.ItemVacancyBinding
import com.yelloyew.careerhabr.model.Vacancy
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private lateinit var _binding: FragmentMainBinding
    private val binding get() = _binding

    private lateinit var recyclerView: RecyclerView
    private var adapter: RecyclerAdapter = RecyclerAdapter()

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter


        binding.refresh.isRefreshing = true
        mainViewModel.getData().observe(
            viewLifecycleOwner,
            Observer {
                adapter.setData(it)
                adapter.notifyItemRangeInserted(10*(mainViewModel.page-1), 10*(mainViewModel.page))
                binding.refresh.isRefreshing = false
                Log.d("tag", "$it")
            })

        binding.refresh.setOnRefreshListener {
            binding.refresh.isRefreshing = false
            mainViewModel.page = mainViewModel.page + 1
            mainViewModel.getData()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private inner class ViewHolder(val bindingItem: ItemVacancyBinding) :
        RecyclerView.ViewHolder(bindingItem.root) {

        private lateinit var vacancy: Vacancy

        fun bind(vacancy: Vacancy) {
            this.vacancy = vacancy
            bindingItem.companyName.text = vacancy.company
            bindingItem.vacancyDate.text = vacancy.date
            bindingItem.position.text = vacancy.position

            if (vacancy.salary.isNotBlank()) {
                bindingItem.salary.text = vacancy.salary
                bindingItem.salary.isVisible = true
            }
            bindingItem.skill.text = vacancy.skill
            bindingItem.metaInfo1.text = vacancy.metaInfo[0]
            if (vacancy.metaInfo.size >= 2) {
                bindingItem.metaInfo2.text = vacancy.metaInfo[1]
                bindingItem.metaInfo2.isVisible = true
            }
            if (vacancy.metaInfo.size == 3) {
                bindingItem.metaInfo3.text = vacancy.metaInfo[2]
                bindingItem.metaInfo3.isVisible = true
            }
            Glide.with(context!!)
                .load(vacancy.logo)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(bindingItem.logo)
        }
    }

    private inner class RecyclerAdapter : RecyclerView.Adapter<ViewHolder>() {
        private var vacancies: MutableList<Vacancy> = mutableListOf()

        fun setData(newVacancyList: MutableList<Vacancy>) {
            val diffUtil = VacancyDiffCallback(vacancies, newVacancyList)
            val diffResult = DiffUtil.calculateDiff(diffUtil)
            vacancies = newVacancyList
            diffResult.dispatchUpdatesTo(this)
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
            if (position % 10 == 8){
                mainViewModel.page = mainViewModel.page + 1
                mainViewModel.getData()
                binding.refresh.isRefreshing = true
            }
        }

        override fun getItemCount() = vacancies.size
    }

    private inner class VacancyDiffCallback(
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

}