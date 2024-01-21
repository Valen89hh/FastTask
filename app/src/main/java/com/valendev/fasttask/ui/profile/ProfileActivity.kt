package com.valendev.fasttask.ui.profile

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.valendev.fasttask.R
import com.valendev.fasttask.databinding.ActivityProfileBinding
import com.valendev.fasttask.domain.constants.URL_PRIVACY_POLICY
import com.valendev.fasttask.domain.usecase.editprofile.EditProfileLifecycleObserver
import com.valendev.fasttask.domain.usecase.permissions.RequestPermissions
import com.valendev.fasttask.ui.hometask.HomeActivity
import com.valendev.fasttask.ui.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private val viewModel by viewModels<ProfileViewModel>()

    private lateinit var editProfileObserver: EditProfileLifecycleObserver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        editProfileObserver = EditProfileLifecycleObserver(this.activityResultRegistry){
            Log.d("EGGSDFGHSDF", "Save")
            viewModel.saveImage(it, this)
        }

        lifecycle.addObserver(editProfileObserver)

        setContentView(binding.root)



        initUi()

        initListeners()

        initCollects()
    }

    private fun initUi() {
        viewModel.updateUser()
        viewModel.updateTotalTasks()


        binding.apply {

        }
    }

    private fun initListeners() {
        binding.apply {
            fabHome.setOnClickListener{
                val intent = Intent(this@ProfileActivity, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            btnGetImage.setOnClickListener {
                Log.d("PERMISSIONsxxx", (ContextCompat.checkSelfPermission(this@ProfileActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED).toString())
                if(ContextCompat.checkSelfPermission(this@ProfileActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    //Permiso no aceptado
                    Log.d("PERMISSIONsxxx", (ActivityCompat.shouldShowRequestPermissionRationale(this@ProfileActivity, Manifest.permission.READ_EXTERNAL_STORAGE)).toString())
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this@ProfileActivity, Manifest.permission.READ_EXTERNAL_STORAGE)){
                        //El usuario ya ha rechazado los permisos
                        Toast.makeText(this@ProfileActivity, "Ir a configuracion y aceptar los permisos", Toast.LENGTH_SHORT).show()
                    }else{
                        //Pedir permisos
                        Toast.makeText(this@ProfileActivity, "pedir permisos", Toast.LENGTH_SHORT).show()

                        ActivityCompat.requestPermissions(this@ProfileActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), RequestPermissions.CODE_REQUEST_PERMISSION_READ_STORAGE)
                    }
                }else{
                    editProfileObserver.selectImage()
                }
            }

            tvLogout.setOnClickListener {
                viewModel.logoutProfile()
                val intent = Intent(this@ProfileActivity, WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            tvRate.setOnClickListener {
                rateApp(this@ProfileActivity)
            }

            tvPrivacity.setOnClickListener {
                openWebsite(URL_PRIVACY_POLICY)
            }

        }
    }

    private fun initCollects() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.user.collect{
                    if(it!=null){
                        binding.apply {
                            tvNameUser.text = it.name
                            tvDescrition.text = it.description
                            try {
                                if(it.image == null || it.image!!.isEmpty()) throw Exception("Not found image")

                                val btImg = BitmapFactory.decodeFile(it.image)
                                circleImageView.setImageBitmap(btImg)
                            }catch (e: Exception){
                                circleImageView.setImageResource(R.drawable.ic_none_usuario_two)
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.totalTasks.collect{
                    binding.tvTotalTasks.text = it.toString()
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.image.collect{
                    Log.d("EGGSDFGHSDF", it)

                    if (it.isNotEmpty()){
                        binding.apply {
                            try {
                                val btImg = BitmapFactory.decodeFile(it)
                                circleImageView.setImageBitmap(btImg)
                                viewModel.saveUserImage(it)
                            }catch (e: Exception){
                                circleImageView.setImageResource(R.drawable.ic_none_usuario)
                            }
                        }
                    }

                }
            }
        }
    }

    private fun openWebsite(url: String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun rateApp(context: Context){
        val packageName = context.packageName

        try {
            //Intent para abrir la aplicacion en la Play Store
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }catch (e: ActivityNotFoundException){
            // Si no hay Play Store, abrir la aplicacion en un navegador web
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}