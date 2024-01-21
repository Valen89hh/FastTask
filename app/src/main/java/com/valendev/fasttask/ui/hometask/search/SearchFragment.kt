package com.valendev.fasttask.ui.hometask.search

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.valendev.fasttask.R
import com.valendev.fasttask.databinding.FragmentSearchBinding
import com.valendev.fasttask.domain.adaptertask.SwipeToDeleteCallback
import com.valendev.fasttask.domain.adaptertask.TaskAdapter
import com.valendev.fasttask.domain.constants.KEY_UPDATE_TASK_ACTIVITY
import com.valendev.fasttask.domain.usecase.listeners.OnItemClickListener
import com.valendev.fasttask.ui.createtask.CreateTaskActivity
import com.valendev.fasttask.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()
    //Adpater
    private val adapterTask = TaskAdapter(object : OnItemClickListener<Long>{
        override fun onItemClick(data: Long) {
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


    private fun navToProfileActivity(){
        val intent = Intent(requireActivity(), ProfileActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initUi()

        initListeners()

        initCollects()
    }

    private fun initUi() {

        viewModel.updateDataUser()
        binding.apply {
            rvTasks.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvTasks.adapter = adapterTask

            val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
            itemTouchHelper.attachToRecyclerView(rvTasks)
        }
    }

    private fun initListeners() {
        binding.apply {

            civUser.setOnClickListener {
                navToProfileActivity()
            }

            etSearch.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(string: Editable?) {
                    if(string != null){
                        viewModel.updateListDataTask(string.toString())
                    }
                }

            })
        }
    }


    private fun initCollects() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.dataTasks.collect{
                    adapterTask.updateData(it)
                }
            }
        }

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.user.collect{
                    try {
                        if(it.image == null || it.image!!.isEmpty()) throw Exception("Not found image")
                        val bitmapImg = BitmapFactory.decodeFile(it.image)
                        binding.civUser.setImageBitmap(bitmapImg)

                    }catch (e: Exception){
                        binding.civUser.setImageResource(R.drawable.ic_none_usuario)
                    }
                }
            }
        }
    }
}