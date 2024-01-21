package com.valendev.fasttask.ui.hometask.maintask

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valendev.fasttask.context.PreferenceManager
import com.valendev.fasttask.data.repository.AppRepository
import com.valendev.fasttask.data.repository.CategoryRepository
import com.valendev.fasttask.data.repository.TaskRepository
import com.valendev.fasttask.data.repository.UserRepository
import com.valendev.fasttask.domain.model.Category
import com.valendev.fasttask.domain.model.CategoryData
import com.valendev.fasttask.domain.model.TaskData
import com.valendev.fasttask.domain.model.UserModel
import com.valendev.fasttask.domain.viewmodel.TaskViewModel
import com.valendev.fasttask.resource.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainTaskViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val preferenceManager: PreferenceManager,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
): TaskViewModel(taskRepository, categoryRepository) {

    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user

    private val _categories = MutableStateFlow<List<CategoryData>>(emptyList())
    val categories = _categories



    init {
        viewModelScope.launch {
            _user.value = appRepository.getUserRepo().getUser(preferenceManager.getIdUser())
        }

    }


    fun updateCategories(){
        viewModelScope.launch(Dispatchers.IO) {
            val categoriesData: MutableList<CategoryData> = mutableListOf()
            val categories = appRepository.getCategoryRepo().getAllCategories()
            Log.d("CategoryCollect", categories.toString())

            for (category in categories){
                val mount = appRepository.getTaskRepo().geMountTaskByCategory(category.id)
                //Creamos Nuestro Data de Category
                val dataCategory = CategoryData(
                    category,
                    mount
                )
                categoriesData.add(dataCategory)
            }
            withContext(Dispatchers.IO){
                _categories.value = categoriesData
            }
        }
    }



}