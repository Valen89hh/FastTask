package com.valendev.fasttask.domain.usecase.permissions

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class RequestPermissions() {

    companion object{

        const val CODE_REQUEST_PERMISSION_READ_STORAGE = 1

        fun checkPermission(activity: Activity, permission: String, codeRequest: Int, handlePermission: (Boolean) -> Unit){
            if(ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED){
                //Permiso no aceptado
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //El usuario ya ha rechazado los permisos
                    Toast.makeText(activity, "Ir a configuracion y aceptar los permisos", Toast.LENGTH_SHORT)
                    handlePermission(false)
                }else{
                    //Pedir permisos
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), codeRequest)
                }
            }else{
                handlePermission(true)
            }
        }

    }
}