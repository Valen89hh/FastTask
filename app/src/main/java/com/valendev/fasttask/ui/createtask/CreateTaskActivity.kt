package com.valendev.fasttask.ui.createtask
import android.content.Intent
import android.os.Build
import java.time.LocalDate
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.valendev.fasttask.R
import com.valendev.fasttask.databinding.ActivityCreateTaskBinding
import com.valendev.fasttask.domain.constants.KEY_UPDATE_TASK_ACTIVITY
import com.valendev.fasttask.domain.model.Category
import com.valendev.fasttask.domain.model.Task
import com.valendev.fasttask.domain.usecase.date.AppDate
import com.valendev.fasttask.domain.usecase.listeners.OnItemClickListener
import com.valendev.fasttask.extension.addHour
import com.valendev.fasttask.extension.toDate
import com.valendev.fasttask.extension.toStringDate
import com.valendev.fasttask.extension.toStringTime
import com.valendev.fasttask.extension.toTime
import com.valendev.fasttask.resource.Status
import com.valendev.fasttask.ui.createtask.adpater.CreateTaskAdapter
import com.valendev.fasttask.ui.hometask.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.properties.Delegates

@AndroidEntryPoint
class CreateTaskActivity : AppCompatActivity() {


    private lateinit var binding: ActivityCreateTaskBinding
    private val viewModel by viewModels<CreateTaskViewModel>()
    private var cateogoryId: Long? = null

    private var isUpdateActivity: Boolean? = null
    private var taskUpdateId: Long? = null
    private lateinit var taskUpdate: Task

    private val appDate = AppDate(this)
    private val adapterCategories = CreateTaskAdapter(listener = object : OnItemClickListener<Category>{

        override fun onItemClick(data: Category) {
            Log.d("DATACATEVDFGS", data.toString())
            cateogoryId = data.id
        }


    })

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initData()

        initUi()

        initListeners()

        initCollects()

    }



    private fun initData() {
        taskUpdateId = intent.getLongExtra(KEY_UPDATE_TASK_ACTIVITY, -1L)
        Log.d("IDLKFKLLKJFAS", taskUpdateId.toString())
        isUpdateActivity = taskUpdateId != -1L
    }

    private fun navHomeTask(){
        val intent = Intent(this@CreateTaskActivity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun initCollects() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.categories.collect{
                    adapterCategories.updateData(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.statusDb.collect{status ->
                    when(status){
                        is Status.Error -> Toast.makeText(this@CreateTaskActivity, status.message, Toast.LENGTH_SHORT).show()
                        is Status.Success -> {
                            Toast.makeText(this@CreateTaskActivity, status.data, Toast.LENGTH_SHORT).show()
                            navHomeTask()
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.taskUpdate.collect{
                    Log.d("IDLKFKLLKJFAS", it.toString())

                    when(it){
                        is Status.Error -> Toast.makeText(this@CreateTaskActivity, it.message, Toast.LENGTH_SHORT).show()
                        is Status.Success -> {
                            Log.d("IDLKFKLLKJFAS", it.toString())
                            taskUpdate = it.data
                            cateogoryId = it.data.categoryId
                            addDataTaskFromActivity(taskUpdate)
                        }
                    }
                }
            }
        }
    }

    private fun initUi(){
        binding.apply {

            //Verificamos si iniciamos la ui para crear o actualizar
            if(isUpdateTaskActivity()){
                Log.d("IDLKFKLLKJFAS", isUpdateActivity.toString())
                alertScreenIsUpdateTask()
            }else{
                etDate.setText(appDate.currentDate.toStringDate())
                etStartTime.setText(appDate.currentDate.toStringTime())

                // Hora posible para finalizar
                val dateStart = etStartTime.text.toString().toTime()
                val dateEnd = dateStart.addHour(1)
                etEndTime.hint = dateEnd.toStringTime()
            }
        }

        binding.rvCategoriesTag.layoutManager = GridLayoutManager(this, 3)
        binding.rvCategoriesTag.adapter = adapterCategories

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListeners(){
        binding.apply {

            btnAddTask.setOnClickListener {
                val nameTask = etTitle.text.toString()
                val description = etDescription.text.toString()
                val dateCreate = etDate.text.toString()
                val dateUpdate = etDate.text.toString()
                val startTime = etStartTime.text.toString()
                val endTime = if(etEndTime.text.isEmpty()) etEndTime.hint.toString() else etEndTime.text.toString()
                // validaciones
                if(nameTask.isEmpty()){
                    etTitle.error = "It cant be empty"

                    return@setOnClickListener
                }

                if(description.isEmpty()){
                    etDescription.error = "It cant be empty"

                    return@setOnClickListener
                }

                if(dateCreate.isEmpty()){
                    etDate.error = "It cant be empty"
                    return@setOnClickListener
                }

                val date = dateCreate.toDate()
                if(appDate.isBefore(date, if(!isUpdateTaskActivity()) appDate.currentDate else taskUpdate.dateCreate.toDate() )){
                    etDate.error = "The selected date cannot be earlier than the current date. Please choose a valid date"

                    return@setOnClickListener
                }


                val dateStart = startTime.toTime()
                val dateEnd = endTime.toTime()
                val compare = dateEnd.compareTo(dateStart)
                if(compare == 0 || compare < 0){
                    etEndTime.error = "The time must be greater than the start time"
                    return@setOnClickListener
                }


                Log.d("VALIDATIONTASK", "PASE LA VALIDACION")

                if(isUpdateTaskActivity()){
                    if(::taskUpdate.isInitialized && cateogoryId!=null){
                        taskUpdate.nameTask = nameTask
                        taskUpdate.description = description
                        taskUpdate.dateCreate = dateCreate
                        taskUpdate.dateUpdate = dateUpdate
                        taskUpdate.startTime = startTime
                        taskUpdate.endTime = endTime
                        taskUpdate.categoryId = cateogoryId!!

                        viewModel.saveTask(taskUpdate)
                    }
                    else Toast.makeText(this@CreateTaskActivity, "The activity is not configured to update the task", Toast.LENGTH_LONG).show()

                }else{
                    //Creamos la task
                    viewModel.createTask(
                        nameTask = nameTask,
                        description = description,
                        dateCreate = dateCreate,
                        dateUpdate = dateUpdate,
                        startTime = startTime,
                        endTime = endTime,
                        categoryId = cateogoryId
                    )
                }

            }

            etDate.setOnClickListener{
                appDate.showDatePickerDialog { view, year, monthOfYear, dayOfMonth ->
                    val selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year".toDate().toStringDate()
                    etDate.setText(selectedDate)
                }
            }

            etStartTime.setOnClickListener{
                appDate.showTimePickerDialog { view, hourOfDay, minute ->
                    Log.d("TIMEDATE", "$hourOfDay:$minute ${view.is24HourView}")
                    etStartTime.setText(appDate.formatTimeToString(hourOfDay, minute))
                    Log.d("TIMEDATE", appDate.convertStringToDateTime(etStartTime.text.toString()).toString())

                }
            }

            etEndTime.setOnClickListener{
                appDate.showTimePickerDialog { view, hourOfDay, minute ->
                    etEndTime.setText(appDate.formatTimeToString(hourOfDay, minute))
                }
            }

            btnBackNav.setOnClickListener{
                navHomeTask()
            }
        }
    }


    private fun alertScreenIsUpdateTask(){
        viewModel.getTaskForUpdate(taskUpdateId!!)
    }

    private fun addDataTaskFromActivity(task: Task){
        binding.apply {
            createNewTask.text = "Update task"
            etTitle.setText(task.nameTask)
            etDescription.setText(task.description)
            etDate.setText(task.dateCreate)
            etStartTime.setText(task.startTime)
            etEndTime.setText(task.endTime)

            btnAddTask.text = "Save Task"
        }

        adapterCategories.updatePositionById(task.categoryId)
    }

    fun isUpdateTaskActivity(): Boolean{
        return isUpdateActivity != null && isUpdateActivity == true
    }

}