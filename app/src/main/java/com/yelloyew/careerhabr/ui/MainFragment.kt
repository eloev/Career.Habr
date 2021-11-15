package com.yelloyew.careerhabr.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.yelloyew.careerhabr.MainViewModel
import com.yelloyew.careerhabr.R
import com.yelloyew.careerhabr.databinding.FragmentMainBinding
import com.yelloyew.careerhabr.databinding.ItemVacancyBinding
import com.yelloyew.careerhabr.model.Vacancy

class MainFragment : Fragment() {

    private lateinit var _binding: FragmentMainBinding
    private val binding get() = _binding

    private var adapter: RecyclerAdapter = RecyclerAdapter()

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var searchItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
                //adapter.notifyItemRangeInserted(15 * (mainViewModel.page - 1) + 1, 15 * (mainViewModel.page))
                adapter.notifyDataSetChanged()
                binding.refresh.isRefreshing = false
            })

        binding.refresh.setOnRefreshListener {
            binding.refresh.isRefreshing = false
        }

        //remote button
        binding.remoteTextview.setOnClickListener {
            if (mainViewModel.remote == "") {
                binding.remoteTextview.setCompoundDrawablesWithIntrinsicBounds(
                    getDrawable(
                        requireContext(),
                        R.drawable.ic_done
                    ), null, null, null
                )
                mainViewModel.remote = "true"
            } else if (mainViewModel.remote == "true") {
                binding.remoteTextview.setCompoundDrawablesWithIntrinsicBounds(
                    getDrawable(
                        requireContext(),
                        R.drawable.ic_close
                    ), null, null, null
                )
                mainViewModel.remote = ""
            }
            mainViewModel.getData()
            binding.refresh.isRefreshing = true
        }

        //spinner
        val items = resources.getStringArray(R.array.skills)
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.menuSkillTextview as? AutoCompleteTextView)?.setAdapter(adapter)
        binding.menuSkillTextview.setOnItemClickListener { _, _, i, _ ->
            when (i) {
                0 -> {
                    mainViewModel.qid = ""
                }
                1 -> {
                    mainViewModel.qid = "1"
                }
                2 -> {
                    mainViewModel.qid = "3"
                }
                3 -> {
                    mainViewModel.qid = "4"
                }
                4 -> {
                    mainViewModel.qid = "5"
                }
                5 -> {
                    mainViewModel.qid = "6"
                }
            }
            mainViewModel.getData()
        }
        //spinner end

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main_fragment, menu)
        searchItem = menu.findItem(R.id.menu_item_search)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_search -> {
                if (binding.searchLayout.isVisible) {
                    closeSearch()
                } else {
                    binding.searchLayout.isVisible = true
                    binding.searchBackground.isVisible = true
                    searchItem.setIcon(R.drawable.ic_close)

                    binding.searchBackgroundClick.setOnClickListener {
                        closeSearch()
                        mainViewModel.query = binding.searchText.text.toString()
                    }

                    binding.searchText.setOnKeyListener(object : View.OnKeyListener {
                        override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                            // if the event is a key down event on the enter button
                            if (event.action == KeyEvent.ACTION_DOWN &&
                                keyCode == KeyEvent.KEYCODE_ENTER
                            ) {
                                closeSearch()
                                binding.searchText.isCursorVisible = false
                                mainViewModel.query = binding.searchText.text.toString()
                                mainViewModel.getData()
                                binding.refresh.isRefreshing = true
                                return true
                            }
                            return false
                        }
                    })

                    binding.salaryText.setOnKeyListener(object : View.OnKeyListener {
                        override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                            // if the event is a key down event on the enter button
                            if (event.action == KeyEvent.ACTION_DOWN &&
                                keyCode == KeyEvent.KEYCODE_ENTER
                            ) {
                                closeSearch()
                                binding.salaryText.isCursorVisible = false
                                mainViewModel.salary = binding.salaryText.text.toString()
                                mainViewModel.getData()
                                binding.refresh.isRefreshing = true
                                return true
                            }
                            return false
                        }
                    })
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun closeSearch() {
        binding.searchLayout.isVisible = false
        binding.searchBackground.isVisible = false
        searchItem.setIcon(R.drawable.ic_search)
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
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
        private var initUpdate = true

        fun setData(newVacancyList: MutableList<Vacancy>) {
            val diffUtil = VacancyDiffCallback(vacancies, newVacancyList)
            val diffResult = DiffUtil.calculateDiff(diffUtil)
            vacancies = newVacancyList
            diffResult.dispatchUpdatesTo(this)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            initUpdate = true
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
            if (vacancies.size - position == 4 && initUpdate) {
                mainViewModel.page = mainViewModel.page + 1
                mainViewModel.getData()
                binding.refresh.isRefreshing = true
                initUpdate = false
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