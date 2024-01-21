package com.valendev.fasttask.ui.hometask.calendary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.data.LocalUriFetcher
import com.valendev.fasttask.databinding.FragmentCalendaryBinding
import com.valendev.fasttask.domain.adaptertask.SwipeToDeleteCallback
import com.valendev.fasttask.domain.adaptertask.TaskAdapter
import com.valendev.fasttask.domain.constants.KEY_UPDATE_TASK_ACTIVITY
import com.valendev.fasttask.domain.usecase.date.AppDate
import com.valendev.fasttask.domain.usecase.listeners.OnItemClickListener
import com.valendev.fasttask.resource.Status
import com.valendev.fasttask.ui.createtask.CreateTaskActivity
import com.valendev.fasttask.ui.hometask.HomeActivity
import com.valendev.fasttask.ui.hometask.calendary.adapters.DayOfMonthAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CalendaryFragment : Fragment() {

    private lateinit var binding: FragmentCalendaryBinding

    private val viewModel by viewModels<CalendarViewModel>()

    //date
    private val dateApp by lazy { AppDate(requireActivity()) }

    //Adapters
    private val adapterDayOffMonth = DayOfMonthAdapter(listener = object : OnItemClickListener<Int>{
        override fun onItemClick(data: Int) {
            Log.d("LJDLJSD", data.toString())
            viewModel.updateTaskByDate(data)
        }
    })

    private val adapterTask = TaskAdapter(listener = object : OnItemClickListener<Long>{
        override fun onItemClick(data: Long) {
            //Navegamos hacia la pantalla de creacion de task, pero para editar pasamo el id de la task
            navToActivityCreateTask(data)
        }

        override fun onClickCheckBox(check: Boolean, itemId: Long) {
            // Update isTerminate in DB
            viewModel.updateTerminateTask(check, itemId)
        }

    })

    private val swipeToDeleteCallback = SwipeToDeleteCallback{ position ->
        val itemId:Long = adapterTask.removeItem(position)
        viewModel.deleteTask(itemId)
    }

    private fun navToActivityCreateTask(idTask: Long? = null){
        val intent = Intent(requireActivity(), CreateTaskActivity::class.java)
        if(idTask!=null) intent.putExtra(KEY_UPDATE_TASK_ACTIVITY, idTask)
        startActivity(intent)
    }

    private fun navHomeTask(){
        val intent = Intent(requireContext(), HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendaryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

        initListerns()

        initCollects()
    }

    private fun initUI() {
        viewModel.updateDaysOffMonth()

        binding.apply {
            rvDayOfMonth.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvDayOfMonth.adapter = adapterDayOffMonth

            rvTasks.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvTasks.adapter = adapterTask

            val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
            itemTouchHelper.attachToRecyclerView(rvTasks)
        }
    }

    private fun initListerns() {
        binding.apply {



            tvDate.setOnClickListener {
                dateApp.showDatePickerDialog { view, year, monthOfYear, dayOfMonth ->
                    viewModel.updateDaysOffMonth(year, monthOfYear, dayOfMonth)
                }
            }

            btnAddTask.setOnClickListener {
                navToActivityCreateTask()
            }

            ivBack.setOnClickListener{
                navHomeTask()
            }
        }
    }

    private fun initCollects() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.daysOffMonth.collect{
                    adapterDayOffMonth.updateData(it)
                }
            }

        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.dateSelected.collect{
                    if (it!=null){
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.YEAR, it.year)
                        calendar.set(Calendar.MONTH, it.month)
                        val spf = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                        val formataDate = spf.format(calendar.time)
                        binding.tvDate.text = formataDate.capitalize()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.statusDataCalendar.collect{
                    when(it){
                        is Status.Error -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        is Status.Success -> viewModel.saveDayOffMonth(it.data)
                    }
                }
            }
        }

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.daySelected.collect{
                    if (it!=null) adapterDayOffMonth.updateDay(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.dataTasks.collect{
                    adapterTask.updateData(it)
                }
            }
        }
    }

}