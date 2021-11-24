package com.yelloyew.careerhabr.ui

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import com.yelloyew.careerhabr.MainActivity
import com.yelloyew.careerhabr.R
import com.yelloyew.careerhabr.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private var skill = ""
    private var remote = ""
    private var notify = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        (requireActivity() as MainActivity).title = getString(R.string.notification_title)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //можно удалённо кнопка
        binding.remoteTextview.setOnClickListener {
            if (remote == "") {
                binding.remoteTextview.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_done
                    ), null, null, null
                )
                remote = "true"
            } else if (remote == "true") {
                binding.remoteTextview.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_close
                    ), null, null, null
                )
                remote = ""
            }
        }

        //спиннер квалификации
        val items = resources.getStringArray(R.array.skills)
        val adapter = ArrayAdapter(requireContext(), R.layout.textview_item, items)
        (binding.menuSkillEdittext as? AutoCompleteTextView)?.setAdapter(adapter)
        binding.menuSkillEdittext.setOnItemClickListener { _, _, i, _ ->
            when (i) {
                0 -> {
                    skill = ""
                }
                1 -> {
                    skill = "1"
                }
                2 -> {
                    skill = "3"
                }
                3 -> {
                    skill = "4"
                }
                4 -> {
                    skill = "5"
                }
                5 -> {
                    skill = "6"
                }
            }
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
                    //
                    return true
                }
                return false
            }
        })

        // нажатие enter в цене
        binding.salaryText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    //
                    return true
                }
                return false
            }
        })

        // кнопка уведомлять
        binding.notificationButton.setOnClickListener {
            if (!notify) {
                notify = true
                binding.notificationButton.apply {
                    background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.white_stroke_green)
                    text = getString(R.string.notification_yes)
                    setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_done
                        ), null, null, null
                    )
                }
            } else if (notify) {
                notify = false
                binding.notificationButton.apply {
                    background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.white_stroke_red)
                    text = getString(R.string.notification_no)
                    setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_close
                        ), null, null, null
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}