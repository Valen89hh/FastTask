package com.valendev.fasttask.ui.hometask.maintask.adapater

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.valendev.fasttask.R
import com.valendev.fasttask.databinding.ItemCategoryLayoutBinding
import com.valendev.fasttask.domain.model.Category
import com.valendev.fasttask.domain.model.CategoryData
import com.valendev.fasttask.domain.usecase.listeners.OnItemClickListener

class CategoryAdapter(
    private var dataList: List<CategoryData> = emptyList(),
    private val listener: OnItemClickListener<Long>
): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    // Poistion
    private var positionItemSelected = 0

    inner class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val binding = ItemCategoryLayoutBinding.bind(itemView)

        fun render(data: CategoryData){
            binding.apply {
                tvCategory.text = data.category.nameCategory
                tvMountCategory.text = "+${data.mount} tasks"
                ivCategory.setImageResource(data.category.imageCategory)
                barraSelected.visibility = if(positionItemSelected == layoutPosition) View.VISIBLE else View.INVISIBLE
            }
            val colorSelected = ContextCompat.getColor(itemView.context, data.category.colorSelected)
            binding.barraSelected.backgroundTintList = ColorStateList.valueOf(colorSelected)

            val elevation = if(positionItemSelected == layoutPosition){
                itemView.context.resources.getDimension(R.dimen.elevation_value)
            }else{
                itemView.context.resources.getDimension(R.dimen.elevation_zero)
            }
            itemView.elevation = elevation
            val color = ContextCompat.getColor(itemView.context, data.category.color)
            itemView.backgroundTintList = ColorStateList.valueOf(color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_layout, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: CategoryViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.render(dataList[position])

        if(position == positionItemSelected){
            // Hacemos feedback del item para el user
        }else{
            // Normal
        }

        holder.itemView.setOnClickListener {
            listener.onItemClick(dataList[position].category.id)

            positionItemSelected = position
            notifyDataSetChanged()
        }
    }



    // funciones propias
    fun updateData(listCategoryData: List<CategoryData>){
        dataList = listCategoryData
        notifyDataSetChanged()
    }

}