package com.valendev.fasttask.ui.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valendev.fasttask.context.PreferenceManager
import com.valendev.fasttask.data.repository.TaskRepository
import com.valendev.fasttask.data.repository.UserRepository
import com.valendev.fasttask.domain.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val preferenceManager: PreferenceManager
): ViewModel() {

    private val _user = MutableStateFlow<UserModel?>(null)
    val user = _user

    private val _image = MutableSharedFlow<String>()
    val image = _image

    private val _totalTasks = MutableStateFlow<Int>(0)
    val totalTasks = _totalTasks

    fun updateUser(){
        viewModelScope.launch(Dispatchers.IO) {
            val userModel = userRepository.getUser(preferenceManager.getIdUser())
            withContext(Dispatchers.Main){
                _user.value = userModel
            }
        }
    }

    fun updateTotalTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            val total = taskRepository.getMountTasks()
            withContext(Dispatchers.Main){
                _totalTasks.value = total
            }
        }
    }

    fun saveImage(uri: Uri?, context: Context){
        Log.d("ImageURI", uri.toString()+"--EMIT")
        viewModelScope.launch(Dispatchers.IO) {
            // Supongamos que "imageUri" es la Uri de la imagen seleccionada
            if (uri!=null){
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

                if (inputStream != null) {
                    val outputFile: File = File(context.filesDir, "nombre_archivo.jpg")
                    val outputStream: OutputStream = FileOutputStream(outputFile)
                    val buffer = ByteArray(4 * 1024)
                    var read: Int

                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                    }

                    outputStream.flush()
                    outputStream.close()
                    inputStream.close()

                    withContext(Dispatchers.Main){
                        Log.d("EGGSDFGHSDF", "FILE")

                        _image.emit(outputFile.absolutePath)
                    }

                }
            }

        }


    }

    fun saveUserImage(file: String){
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepository.getUser(preferenceManager.getIdUser())
            if (user != null){
                user.image = file
                userRepository.updateUser(user)
            }
        }
    }

    fun logoutProfile() {
        preferenceManager.setFirstTimeLaunch(false)
    }

}