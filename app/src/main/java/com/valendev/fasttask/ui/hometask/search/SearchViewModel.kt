package com.valendev.fasttask.ui.hometask.search

import androidx.lifecycle.viewModelScope
import com.valendev.fasttask.context.PreferenceManager
import com.valendev.fasttask.data.repository.AppRepository
import com.valendev.fasttask.data.repository.TaskRepository
import com.valendev.fasttask.domain.model.UserModel
import com.valendev.fasttask.domain.viewmodel.TaskViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val preferenceManager: PreferenceManager
): TaskViewModel(appRepository.getTaskRepo(), appRepository.getCategoryRepo()) {

    private val _user = MutableSharedFlow<UserModel>()
    val user = _user


    fun updateDataUser(){
        viewModelScope.launch(Dispatchers.IO) {
            val userModel = appRepository.getUserRepo().getUser(preferenceManager.getIdUser())
            if (userModel != null){
                withContext(Dispatchers.Main){
                    _user.emit(userModel)
                }
            }

        }
    }
}