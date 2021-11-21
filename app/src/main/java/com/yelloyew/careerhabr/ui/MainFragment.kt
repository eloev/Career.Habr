package com.yelloyew.careerhabr.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.yelloyew.careerhabr.MainActivity
import com.yelloyew.careerhabr.MainViewModel
import com.yelloyew.careerhabr.R
import com.yelloyew.careerhabr.databinding.FragmentMainBinding
import com.yelloyew.careerhabr.databinding.ItemVacancyBinding
import com.yelloyew.careerhabr.model.Vacancy
import androidx.navigation.fragment.findNavController
import com.yelloyew.careerhabr.adapter.MainSwipeAdapter
import com.yelloyew.careerhabr.adapter.VacancyDiffCallback


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var adapter = MainRecyclerAdapter()

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

        (requireActivity() as MainActivity).title = getString(R.string.app_name)

        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter

            refresh.isRefreshing = true

            refresh.setOnRefreshListener {
                refresh.isRefreshing = false
            }
        }

        val itemTouchHelper = ItemTouchHelper(MainSwipeAdapter(adapter))
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        //search view remote button
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

        //search view spinner
        val items = resources.getStringArray(R.array.skills)
        val adapter = ArrayAdapter(requireContext(), R.layout.textview_item, items)
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainViewModel.getData().observe(
            viewLifecycleOwner, {
                adapter.apply {
                    setData(it)
                }
                binding.refresh.isRefreshing = false
            })
        super.onViewCreated(view, savedInstanceState)
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
                    // поиск открыт
                    searchItem.setIcon(R.drawable.ic_close)
                    binding.apply {
                        searchLayout.isVisible = true
                        searchBackground.isVisible = true
                        hideBottomNav(true)

                        // фон поиска
                        searchBackgroundClick.setOnClickListener {
                            mainViewModel.query = searchText.text.toString()
                            closeSearch()
                        }

                        // нажатие enter в поиске
                        binding.searchText.setOnKeyListener(object : View.OnKeyListener {
                            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                                if (event.action == KeyEvent.ACTION_DOWN &&
                                    keyCode == KeyEvent.KEYCODE_ENTER
                                ) {
                                    closeSearch()
                                    binding.apply {
                                        searchText.isCursorVisible = false
                                        refresh.isRefreshing = true
                                    }
                                    mainViewModel.apply {
                                        query = binding.searchText.text.toString()
                                        getData()
                                    }
                                    return true
                                }
                                return false
                            }
                        })

                        // нажатие enter в цене
                        binding.salaryText.setOnKeyListener(object : View.OnKeyListener {
                            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                                // if the event is a key down event on the enter button
                                if (event.action == KeyEvent.ACTION_DOWN &&
                                    keyCode == KeyEvent.KEYCODE_ENTER
                                ) {
                                    closeSearch()
                                    binding.apply {
                                        salaryText.isCursorVisible = false
                                        refresh.isRefreshing = true
                                    }

                                    mainViewModel.apply {
                                        salary = binding.salaryText.text.toString()
                                        getData()
                                    }
                                    return true
                                }
                                return false
                            }
                        })
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun closeSearch() {
        binding.apply {
            searchLayout.isVisible = false
            searchBackground.isVisible = false
        }
        searchItem.setIcon(R.drawable.ic_search)
        view?.let { activity?.hideKeyboard(it) }
        hideBottomNav(false)
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun hideBottomNav(hide: Boolean) {
        (activity as MainActivity).hideBottomNav(hide)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    inner class MainRecyclerAdapter : RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>() {
        private var vacancies: MutableList<Vacancy> = mutableListOf()
        private var initUpdate = true

        fun setData(newVacancyList: MutableList<Vacancy>) {
            val diffUtil = VacancyDiffCallback(vacancies, newVacancyList)
            val diffResult = DiffUtil.calculateDiff(diffUtil)
            if (newVacancyList.size == 0) {
                binding.tvIfNull.isVisible = true
            } else {
                vacancies = newVacancyList
                notifyDataSetChanged()
                binding.tvIfNull.isVisible = false
            }
            diffResult.dispatchUpdatesTo(this)
        }

        fun likeVacancy(position: Int) {
            mainViewModel.addLike(vacancies[position])
            notifyItemChanged(position)
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
                mainViewModel.apply {
                    page += 1
                    getData()
                }
                binding.refresh.isRefreshing = true
                initUpdate = false
            }
        }

        override fun getItemCount() = vacancies.size


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

            override fun onClick(v: View) {
                val action = MainFragmentDirections.actionMainFragmentToVacancyFragment(vacancy)
                findNavController().navigate(action)
            }
        }
    }
}