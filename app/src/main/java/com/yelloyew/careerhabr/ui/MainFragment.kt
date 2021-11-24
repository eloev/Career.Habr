package com.yelloyew.careerhabr.ui

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
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
import com.yelloyew.careerhabr.viewmodel.MainViewModel
import com.yelloyew.careerhabr.R
import com.yelloyew.careerhabr.databinding.FragmentMainBinding
import com.yelloyew.careerhabr.databinding.ItemVacancyBinding
import com.yelloyew.careerhabr.model.Vacancy
import androidx.navigation.fragment.findNavController
import com.yelloyew.careerhabr.viewmodel.LikedViewModel
import com.yelloyew.careerhabr.adapter.MainSwipeAdapter
import com.yelloyew.careerhabr.adapter.VacancyDiffCallback

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var adapter = MainRecyclerAdapter()
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var searchItem: MenuItem

    private var query = "kotlin"
    private var remote = ""
    private var salary = ""
    private var qid = ""
    private var response = "&q=$query&remote=$remote&salary=$salary&qid=$qid"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.eraseList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
            refresh.isRefreshing = true
            refresh.setOnRefreshListener {
                refresh.isRefreshing = false
            }
        }

        setHasOptionsMenu(true)
        (requireActivity() as MainActivity).title = getString(R.string.app_name)

        val itemTouchHelper = ItemTouchHelper(MainSwipeAdapter(adapter))
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainViewModel.getData(response).observe(
            viewLifecycleOwner, {
                binding.tvIfNull.isVisible = it.size == 0
                adapter.setData(it)
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
                    hideBottomNav(true)
                    binding.apply {
                        searchLayout.isVisible = true
                        searchBackground.isVisible = true
                        // фон поиска
                        searchBackgroundClick.setOnClickListener {
                            closeSearch()
                        }
                    }

                    //можно удалённо кнопка
                    binding.remoteTextview.setOnClickListener {
                        if (remote == "") {
                            binding.remoteTextview.setCompoundDrawablesWithIntrinsicBounds(
                                getDrawable(
                                    requireContext(),
                                    R.drawable.ic_done
                                ), null, null, null
                            )
                            remote = "true"
                        } else if (remote == "true") {
                            binding.remoteTextview.setCompoundDrawablesWithIntrinsicBounds(
                                getDrawable(
                                    requireContext(),
                                    R.drawable.ic_close
                                ), null, null, null
                            )
                            remote = ""
                        }
                        sendResponse()
                        binding.refresh.isRefreshing = true
                    }

                    //спиннер квалификации
                    val items = resources.getStringArray(R.array.skills)
                    val adapter = ArrayAdapter(requireContext(), R.layout.textview_item, items)
                    (binding.menuSkillEdittext as? AutoCompleteTextView)?.setAdapter(adapter)
                    binding.menuSkillEdittext.setOnItemClickListener { _, _, i, _ ->
                        when (i) {
                            0 -> {
                                qid = ""
                            }
                            1 -> {
                                qid = "1"
                            }
                            2 -> {
                                qid = "3"
                            }
                            3 -> {
                                qid = "4"
                            }
                            4 -> {
                                qid = "5"
                            }
                            5 -> {
                                qid = "6"
                            }
                        }
                        sendResponse()
                        binding.refresh.isRefreshing = true
                    }
                    // Выводим выпадающий список
                    binding.menuSkillEdittext.onFocusChangeListener =
                        View.OnFocusChangeListener { _, hasFocus ->
                            if (hasFocus) {
                                binding.menuSkillEdittext.showDropDown()
                            }
                        }

                    // нажатие enter в поиске
                    binding.searchText.setOnKeyListener(object : View.OnKeyListener {
                        override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                                closeSearchAndSearch()
                                return true
                            }
                            return false
                        }
                    })

                    // нажатие enter в цене
                    binding.salaryText.setOnKeyListener(object : View.OnKeyListener {
                        override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                                closeSearchAndSearch()
                                return true
                            }
                            return false
                        }
                    })

                }
                //search end
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun closeSearchAndSearch() {
        binding.apply {
            searchText.isCursorVisible = false
            salaryText.isCursorVisible = false
            refresh.isRefreshing = true
        }
        query = binding.searchText.text.toString()
        salary = binding.salaryText.text.toString()
        sendResponse()
        closeSearch()
    }

    private fun sendResponse(){
        response = "&q=$query&remote=$remote&salary=$salary&qid=$qid"
        mainViewModel.getData(response)
    }

    private fun closeSearch() {
        binding.apply {
            searchLayout.isVisible = false
            searchBackground.isVisible = false
        }
        hideBottomNav(false)
        searchItem.setIcon(R.drawable.ic_search)

        view?.let { activity?.hideKeyboard(it) }
    }

    private fun hideBottomNav(hide: Boolean) {
        (activity as MainActivity).hideBottomNav(hide)
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun Context.vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        100,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
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

    inner class MainRecyclerAdapter : RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>() {
        private var vacancies: MutableList<Vacancy> = mutableListOf()
        private var initUpdate = true

        fun setData(newVacancyList: MutableList<Vacancy>) {
            val diffUtil = VacancyDiffCallback(vacancies, newVacancyList)
            val diffResult = DiffUtil.calculateDiff(diffUtil)
            vacancies = newVacancyList
            notifyDataSetChanged()
            diffResult.dispatchUpdatesTo(this)
        }

        fun likeVacancy(position: Int) {
            LikedViewModel().addLike(vacancies[position])
            notifyItemChanged(position)
            context!!.vibrate()
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
                mainViewModel.page += 1
                sendResponse()
                binding.refresh.isRefreshing = true
                initUpdate = false
            }
        }

        override fun getItemCount() = vacancies.size


        inner class ViewHolder(private val bindingItem: ItemVacancyBinding) :
            RecyclerView.ViewHolder(bindingItem.root) {

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
                Glide.with(context!!)
                    .load(vacancy.logo)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(bindingItem.logo)

                itemView.setOnClickListener {
                    val action = MainFragmentDirections.actionMainFragmentToVacancyFragment(vacancy)
                    findNavController().navigate(action)
                }
            }
        }
    }
}