package com.yelloyew.careerhabr.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.yelloyew.careerhabr.MainActivity
import com.yelloyew.careerhabr.MainViewModel
import com.yelloyew.careerhabr.R
import com.yelloyew.careerhabr.adapter.LikedRecyclerAdapter
import com.yelloyew.careerhabr.adapter.LikedSwipeAdapter
import com.yelloyew.careerhabr.databinding.FragmentLikedBinding

class LikedFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()

    private var adapter = LikedRecyclerAdapter()

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
            viewLifecycleOwner, {
                if (it.size == 0) {
                    binding.tvIfNull.isVisible = true
                } else {
                    binding.tvIfNull.isVisible = false
                    adapter.setData(it)
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}