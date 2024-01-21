package com.valendev.fasttask.ui.hometask.calendary.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.valendev.fasttask.R
import com.valendev.fasttask.databinding.ItemDayLayoutBinding
import com.valendev.fasttask.domain.model.DayOffMonth
import com.valendev.fasttask.domain.usecase.listeners.OnItemClickListener

class DayOfMonthAdapter(
    private var dataList: List<DayOffMonth> = emptyList(),
    private val listener: OnItemClickListener<Int>
): RecyclerView.Adapter<DayOfMonthAdapter.ViewHolder>() {

    private var positionItemSelected = RecyclerView.NO_POSITION

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val binding = ItemDayLayoutBinding.bind(itemView)

        fun render(day: DayOffMonth){
            binding.apply {
                tvNumberDay.text = day.number.toString()
                tvNameDay.text = day.name
            }

            itemView.isActivated = positionItemSelected == layoutPosition
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val day = dataList[position]
        holder.render(day)
        holder.itemView.setOnClickListener {
            listener.onItemClick(day.number)
            positionItemSelected = position
            notifyDataSetChanged()
        }
    }


    fun updateData(newData: List<DayOffMonth>){
        dataList = newData
        positionItemSelected = dataList.indexOfFirst { it.currentDay }
        if (positionItemSelected != -1){
            listener.onItemClick(dataList[positionItemSelected].number)
        }
        notifyDataSetChanged()
    }

    fun updateDay(day: Int) {
        positionItemSelected = dataList.indexOfFirst { it.number == day }
        if (positionItemSelected != -1){
            listener.onItemClick(dataList[positionItemSelected].number)
        }
        notifyDataSetChanged()
    }
}