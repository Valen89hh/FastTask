package com.valendev.fasttask.domain.usecase.listeners

interface OnItemClickListener<T>{


    fun onItemClick(data: T)

    fun onClickCheckBox(check: Boolean, itemId: Long){}
}