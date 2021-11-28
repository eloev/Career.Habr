package com.yelloyew.careerhabr.ui

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.work.*
import com.yelloyew.careerhabr.MainActivity
import com.yelloyew.careerhabr.R
import com.yelloyew.careerhabr.databinding.FragmentNotificationBinding
import com.yelloyew.careerhabr.utils.PollWorker
import com.yelloyew.careerhabr.utils.RequestPreferences
import java.util.concurrent.TimeUnit

private const val NOTIFY_WORK = "NOTIFY_WORK"

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private var query = ""
    private var salary = ""
    private var remote = ""
    private var skill = ""

    private var notify = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        (requireActivity() as MainActivity).title = getString(R.string.notification_title)

        val notifyRequest =
            RequestPreferences.getNotifyRequest(requireContext()).split(",").toTypedArray()
        if (notifyRequest.size == 4) {
            query = notifyRequest[0]
            if (notifyRequest[1].isNotBlank()) remoteButton()
            salary = notifyRequest[2]
            skill = notifyRequest[3]
            if (query.isNotBlank()) binding.searchText.text =
                Editable.Factory.getInstance().newEditable(query)
            if (salary.isNotBlank()) binding.salaryText.text =
                Editable.Factory.getInstance().newEditable(salary)
        }

        if (RequestPreferences.getNotifyState(requireContext())) notifyButton()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //можно удалённо кнопка
        binding.remoteTextview.setOnClickListener {
            remoteButton()
            saveParams()
        }

        //спиннер квалификации
        val items = resources.getStringArray(R.array.skills)
        binding.menuSkillEdittext.apply {
            when (skill) {
                "1" -> text = Editable.Factory.getInstance().newEditable(items[1])
                "3" -> text = Editable.Factory.getInstance().newEditable(items[2])
                "4" -> text = Editable.Factory.getInstance().newEditable(items[3])
                "5" -> text = Editable.Factory.getInstance().newEditable(items[4])
                "6" -> text = Editable.Factory.getInstance().newEditable(items[5])
            }
        }
        val adapter = ArrayAdapter(requireContext(), R.layout.textview_item, items)
        (binding.menuSkillEdittext as? AutoCompleteTextView)?.setAdapter(adapter)
        binding.menuSkillEdittext.setOnItemClickListener { _, _, i, _ ->
            when (i) {
                0 -> skill = ""
                1 -> skill = "1"
                2 -> skill = "3"
                3 -> skill = "4"
                4 -> skill = "5"
                5 -> skill = "6"
            }
            saveParams()
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
                    saveParams()
                    return true
                }
                return false
            }
        })

        // нажатие enter в цене
        binding.salaryText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    saveParams()
                    return true
                }
                return false
            }
        })

        // кнопка уведомлять
        binding.notificationButton.setOnClickListener {
            notifyButton()
            saveParams()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun saveParams() {
        binding.apply {
            searchText.isCursorVisible = false
            salaryText.isCursorVisible = false
        }
        query = binding.searchText.text.toString()
        salary = binding.salaryText.text.toString()
        RequestPreferences.setNotifyRequest(requireContext(), "$query,$remote,$salary,$skill")
    }

    private fun saveNotifyState() {
        RequestPreferences.setNotifyState(context!!, notify)
    }

    private fun enableNotify() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_ROAMING)
            .build()
        val periodicRequest = PeriodicWorkRequest
            .Builder(PollWorker::class.java, 2, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context!!)
            .enqueueUniquePeriodicWork(
                NOTIFY_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
    }

    private fun remoteButton() {
        if (remote == "") {
            remote = "true"
            binding.remoteTextview.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_done
                ), null, null, null
            )
        } else if (remote == "true") {
            remote = ""
            binding.remoteTextview.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_close
                ), null, null, null
            )
        }
    }

    private fun notifyButton() {
        if (!notify) {
            notify = true
            enableNotify()
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
            WorkManager.getInstance(context!!).cancelUniqueWork(NOTIFY_WORK)
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
        saveNotifyState()
    }
}