package com.valendev.fasttask.domain.usecase.editprofile

import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class EditProfileLifecycleObserver(private val registry: ActivityResultRegistry, private val handlerResult: (Uri?)-> Unit): DefaultLifecycleObserver {

    private lateinit var getContent: ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        getContent = registry.register("key", owner, ActivityResultContracts.GetContent()){
            //handle
            Log.d("ImageURI", it.toString()+"---Li")
            handlerResult(it)
        }
    }

    fun selectImage(){
        getContent.launch("image/*")
    }
}