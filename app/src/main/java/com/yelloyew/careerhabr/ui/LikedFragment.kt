package com.yelloyew.careerhabr.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.yelloyew.careerhabr.MainActivity
import com.yelloyew.careerhabr.R
import com.yelloyew.careerhabr.adapter.LikedRecyclerAdapter
import com.yelloyew.careerhabr.adapter.LikedSwipeAdapter
import com.yelloyew.careerhabr.databinding.FragmentLikedBinding
import com.yelloyew.careerhabr.viewmodel.LikedViewModel

class LikedFragment : Fragment() {

    private val likedViewModel: LikedViewModel by viewModels()

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
        likedViewModel.likedListLiveData.observe(
            viewLifecycleOwner, {
                if (it.size == 0) {
                    binding.tvIfNull.isVisible = true
                } else {
                    binding.tvIfNull.isVisible = false
                    adapter.setData(it)
                    context?.vibrate()
                }
            }
        )
    }

    private fun Context.vibrate(){
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                //deprecated in API 26
                vibrator.vibrate(100)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}