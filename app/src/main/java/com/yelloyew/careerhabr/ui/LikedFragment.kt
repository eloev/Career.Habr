package com.yelloyew.careerhabr.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.yelloyew.careerhabr.MainActivity
import com.yelloyew.careerhabr.MainViewModel
import com.yelloyew.careerhabr.R
import com.yelloyew.careerhabr.adapter.LikedSwipeAdapter
import com.yelloyew.careerhabr.adapter.MainSwipeAdapter
import com.yelloyew.careerhabr.databinding.FragmentLikedBinding
import com.yelloyew.careerhabr.databinding.ItemVacancyBinding
import com.yelloyew.careerhabr.model.Vacancy

class LikedFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()

    private var adapter: RecyclerAdapter = RecyclerAdapter()

    private var _binding: FragmentLikedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLikedBinding.inflate(inflater, container, false)

        (requireActivity() as MainActivity).title = getString(R.string.liked_vacancy_title)

        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
        }

        val itemTouchHelper = ItemTouchHelper(LikedSwipeAdapter(adapter))
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.likedListLiveData.observe(
            viewLifecycleOwner,
            Observer { adapter.setData(it)
            Log.d("tag8", "$it")}
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    inner class ViewHolder(private val bindingItem: ItemVacancyBinding) :
        RecyclerView.ViewHolder(bindingItem.root), View.OnClickListener {

        private lateinit var vacancy: Vacancy

        init {
            itemView.setOnClickListener(this)
        }

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
            Glide.with(context!!)
                .load(vacancy.logo)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(bindingItem.logo)
        }

        override fun onClick(v: View?) {
            val action = LikedFragmentDirections.actionLikedFragmentToVacancyFragment(vacancy)
            findNavController().navigate(action)
        }
    }

    inner class RecyclerAdapter : RecyclerView.Adapter<ViewHolder>() {
        private var vacancies: List<Vacancy> = listOf()

        fun setData(newVacancyList: List<Vacancy>) {
            vacancies = newVacancyList
            notifyItemRangeInserted(0, vacancies.size)
        }

        fun deleteVacancy(position: Int) {
            mainViewModel.deleteLike(vacancies[position])
            notifyItemRemoved(position)
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

        override fun getItemCount(): Int {
            return if (vacancies.isNotEmpty()) {
                binding.tvIfNull.isVisible = false
                vacancies.size
            } else {
                binding.tvIfNull.isVisible = true
                0
            }
        }
    }
}