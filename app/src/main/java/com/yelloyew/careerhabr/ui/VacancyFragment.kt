package com.yelloyew.careerhabr.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.yelloyew.careerhabr.MainViewModel
import com.yelloyew.careerhabr.databinding.FragmentVacancyBinding
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.yelloyew.careerhabr.R

class VacancyFragment : Fragment() {

    private var _binding: FragmentVacancyBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVacancyBinding.inflate(inflater, container, false)

        val args = VacancyFragmentArgs.fromBundle(requireArguments())
        val vacancy = args.vacancy

        mainViewModel.getCurrentVacancyInfo(vacancy.url).observe(
            viewLifecycleOwner,
            Observer {
                binding.apply {
                    progressBar.isVisible = false
                    shareButton.isVisible = true
                    vacancyInfo.text = it
                }
            })

        binding.apply {
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
            shareButton.setOnClickListener {
                Intent(Intent.ACTION_SEND).apply{
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, vacancy.url)
                }.also { intent -> startActivity(intent) }
            }
        }
        Glide.with(requireContext())
            .load(vacancy.logo)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.logo)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}