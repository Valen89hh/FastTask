package com.valendev.fasttask.ui.hometask.maintask

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideExperiments
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.valendev.fasttask.R
import com.valendev.fasttask.context.PreferenceManager
import com.valendev.fasttask.databinding.FragmentMainTaskBinding
import com.valendev.fasttask.domain.adaptertask.SwipeToDeleteCallback
import com.valendev.fasttask.domain.adaptertask.TaskAdapter
import com.valendev.fasttask.domain.constants.KEY_UPDATE_TASK_ACTIVITY
import com.valendev.fasttask.domain.model.UserModel
import com.valendev.fasttask.domain.usecase.listeners.OnItemClickListener
import com.valendev.fasttask.domain.usecase.permissions.RequestPermissions
import com.valendev.fasttask.resource.Status
import com.valendev.fasttask.resource.TaskStatus
import com.valendev.fasttask.ui.createtask.CreateTaskActivity
import com.valendev.fasttask.ui.hometask.maintask.adapater.CategoryAdapter
import com.valendev.fasttask.ui.hometask.maintask.decorators.CategoryDecorator
import com.valendev.fasttask.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import kotlin.math.log

@AndroidEntryPoint
class MainTaskFragment : Fragment() {


    private lateinit var binding: FragmentMainTaskBinding
    private val viewModel by viewModels<MainTaskViewModel>()

    private val listStatusTask = listOf(TaskStatus.NEW, TaskStatus.PENDING, TaskStatus.COMPLETED)

    private val adapterCategory = CategoryAdapter( listener = object : OnItemClickListener<Long>{
        override fun onItemClick(data: Long) {
            // Filtramos las tareas por categoria
            Toast.makeText(requireContext(), data.toString(), Toast.LENGTH_SHORT).show()
            viewModel.updateListDataTask(data)
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
        //viewModel.updateCategories()
    }

    private fun navToActivityCreateTask(idTask: Long? = null){
        val intent = Intent(requireActivity(), CreateTaskActivity::class.java)
        if(idTask!=null) intent.putExtra(KEY_UPDATE_TASK_ACTIVITY, idTask)
        startActivity(intent)
    }

    private fun navToProfileActivity(){
        val intent = Intent(requireActivity(), ProfileActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainTaskBinding.inflate(inflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Actualizamos la data
        viewModel.updateListDataTask(TaskStatus.NEW)
        viewModel.updateCategories()

        //: )

        initUI()

        initListeners()

        initCollects()
    }

    fun initUI(){

        binding.apply {

            rvCategories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvCategories.adapter = adapterCategory
            rvCategories.addItemDecoration(CategoryDecorator(resources.getDimensionPixelSize(R.dimen.space_category)))

            rvTasks.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvTasks.adapter = adapterTask

            val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
            itemTouchHelper.attachToRecyclerView(rvTasks)



        }
    }



    fun initListeners(){
        binding.apply {
            civUser.setOnClickListener{
                navToProfileActivity()
            }



            for (statusTask in listStatusTask){
                tabFilterTask.newTab().setText(statusTask.state).let { tabFilterTask.addTab(it) }
            }

            for(i in listStatusTask.indices){
                val textView = LayoutInflater.from(requireContext()).inflate(R.layout.tab_title, null) as TextView
                tabFilterTask.getTabAt(i)?.customView = textView
            }

            tabFilterTask.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if(tab != null){
                        var selectedTab = tab.position
                        viewModel.updateListDataTask(listStatusTask[selectedTab])
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    //not
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    //not
                }

            })
            btnAddTask.setOnClickListener {
                navToActivityCreateTask()
            }

            btnAllCategories.setOnClickListener {
                viewModel.updateListDataTask()
            }
        }
    }

    fun initCollects(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.user.collect{user ->
                    if (user != null){
                        binding.apply {
                            tvSaludo.text = "Hello ${user.name}!"
                        }
                        try {
                            Log.d("EGGSDFGHSDF", user.image.toString())
                            if(user.image == null || user.image!!.isEmpty()) throw Exception("Not found image")

                            val bitmap = BitmapFactory.decodeFile(user.image)
                            binding.civUser.setImageBitmap(bitmap)
                        }catch (e:Exception){
                            binding.civUser.setImageResource(R.drawable.ic_none_usuario)
                        }
                    }

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.categories.collect{
                    adapterCategory.updateData(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.dataTasks.collect{
                    adapterTask.updateData(it)

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.statusUpdateItemTask.collect{
                    when(it){
                        is Status.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                        }
                        is Status.Success -> {
                            //adapterTask.updateItem(it.data)
                            adapterTask.updateItem(it.data)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.statusDeleteTask.collect{
                    when(it){
                        is Status.Error -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                        is Status.Success -> viewModel.updateCategories()
                    }
                }
            }
        }
    }

}