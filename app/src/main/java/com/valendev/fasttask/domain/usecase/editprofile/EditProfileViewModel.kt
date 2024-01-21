package com.valendev.fasttask.domain.usecase.editprofile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valendev.fasttask.data.repository.UserRepository
import com.valendev.fasttask.domain.model.UserModel
import com.valendev.fasttask.resource.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {


    private val _image by lazy {
        MutableStateFlow<String>("")
    }
    val image = _image

    private val _userId = MutableStateFlow<Int?>(null)
    val userId = _userId

    private val _statusDb = MutableSharedFlow<Status<Int>>()
    val statusDb = _statusDb


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

                    // Ahora "outputFile" contiene la imagen guardada en tu aplicación
                    withContext(Dispatchers.Main){
                        _image.value = outputFile.absolutePath
                    }
                }
            }

        }


    }


    fun saveDataUser(name: String, lastName: String, description: String){
        //Guardamos en db

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = UserModel(name=name, lastName = lastName, description = description, image = _image.value)

                val id = userRepository.createUser(user)
                Log.d("USERBBB", "$id--")
                withContext(Dispatchers.Main){
                    _statusDb.emit(Status.Success(id))
                }
            }
            catch (e: Exception){
                withContext(Dispatchers.Main){
                    _statusDb.emit(Status.Error(e.message.toString()))
                }
            }

        }

    }
}