package com.valendev.fasttask.domain.adaptertask

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.valendev.fasttask.R
import com.valendev.fasttask.databinding.ItemTaskLayoutBinding
import com.valendev.fasttask.domain.model.Task
import com.valendev.fasttask.domain.model.TaskData
import com.valendev.fasttask.domain.usecase.listeners.OnItemClickListener
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter(
    private val listener: OnItemClickListener<Long>
): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    val spf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private var dataTask: MutableList<TaskData> = mutableListOf()
        set(value){
            field = value
            notifyDataSetChanged()
        }

    inner class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        private val binding = ItemTaskLayoutBinding.bind(itemView)

        fun render(taskData: TaskData){
            binding.apply {
                ivTask.setImageResource(taskData.image)
                tvNameTask.text = taskData.task.nameTask
                tvTimeTask.text = taskData.task.dateCreate
                cbTask.isChecked = taskData.task.isTerminate

                cbTask.setOnClickListener {
                    Log.d("TAfklajf", taskData.toString())
                    listener.onClickCheckBox(cbTask.isChecked, taskData.task.id)
                }
            }

            itemView.setOnClickListener {
                //Pasamos la id cuando se hace click
                listener.onItemClick(taskData.task.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task_layout, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount() = dataTask.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.render(dataTask[position])
    }


    fun updateData(newData: List<TaskData>){
        dataTask = newData.toMutableList()
        Log.d("TAflajdfldf", dataTask.toString())
        notifyDataSetChanged()
    }

    fun updateItem(taskData: TaskData){
        val posUpdate = dataTask.indexOfFirst { it.task.id == taskData.task.id }
        Log.d("Uflasdjkf", posUpdate.toString())

//        if(posUpdate != -1){
//            dataTask = dataTask.toMutableList().apply {
//                set(posUpdate, taskData)
//            }
//            Log.d("Uflasdjkf", posUpdate.toString())
//
//            Log.d("Uflasdjkf", taskData.task.toString())
//            notifyItemChanged(posUpdate)
//        }
    }

    fun removeItem(position: Int): Long {
        val itemIdremove = dataTask[position].task.id
        dataTask.removeAt(position)
        notifyItemRemoved(position)
        return itemIdremove
    }
}