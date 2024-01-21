package com.valendev.fasttask.ui.createtask.adpater

import android.graphics.drawable.StateListDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.animation.Positioning

import com.valendev.fasttask.R
import com.valendev.fasttask.databinding.ItemCategoryTagLayoutBinding
import com.valendev.fasttask.domain.model.Category
import com.valendev.fasttask.domain.usecase.listeners.OnItemClickListener
import kotlinx.coroutines.flow.MutableStateFlow


class CreateTaskAdapter(
    private var dataList: List<Category> = emptyList(),
    private val listener: OnItemClickListener<Category>
): RecyclerView.Adapter<CreateTaskAdapter.CreateTaskViewHolder>() {

    private var positionItemSelected = 0

    inner class CreateTaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val binding = ItemCategoryTagLayoutBinding.bind(itemView)

        fun render(category: Category){
            binding.apply {
                tvCategory.text = category.nameCategory
                Log.d("RECYCLERLCCLIK", "$positionItemSelected == $layoutPosition")
                tvCategory.isActivated = positionItemSelected == layoutPosition
            }
        }

        fun addListenerClick(){
            itemView.setOnClickListener {
                listener.onItemClick(dataList[layoutPosition])
                positionItemSelected = layoutPosition
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateTaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_tag_layout, parent, false)
        return CreateTaskViewHolder(view)
    }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: CreateTaskViewHolder, position: Int) {

        holder.render(dataList[position])
        holder.addListenerClick()
    }


    fun updateData(newData: List<Category>){
        dataList = newData

        if(newData.isNotEmpty()) listener.onItemClick(newData[positionItemSelected])

        notifyDataSetChanged()
    }

    fun getCategoryByPosition(position: Int): Category{
        return dataList[position]
    }

    fun updatePositionById(categoryId: Long){
        val pos = dataList.indexOfFirst { it.id == categoryId}
        if(pos != -1){
            positionItemSelected = pos
            notifyDataSetChanged()
        }

    }
}