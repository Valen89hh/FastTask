package com.valendev.fasttask.ui.welcome.login

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.valendev.fasttask.context.PreferenceManager
import com.valendev.fasttask.databinding.FragmentLoginBinding
import com.valendev.fasttask.domain.constants.CODE_OPEN_READ_DOCUMENT
import com.valendev.fasttask.domain.usecase.editprofile.EditProfileLifecycleObserver
import com.valendev.fasttask.domain.usecase.editprofile.EditProfileViewModel
import com.valendev.fasttask.domain.usecase.permissions.RequestPermissions
import com.valendev.fasttask.resource.Status
import com.valendev.fasttask.ui.hometask.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: EditProfileViewModel by viewModels()
    private lateinit var editProfileObserver: EditProfileLifecycleObserver


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        editProfileObserver = EditProfileLifecycleObserver(requireActivity().activityResultRegistry){
            viewModel.saveImage(it, requireActivity())
        }

        lifecycle.addObserver(editProfileObserver)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()




        initUIStarted()

    }

    fun initListeners(){
        binding.btnGetImage.setOnClickListener {
            Log.d("PERMISSIONsxxx", (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED).toString())
            if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //Permiso no aceptado
                Log.d("PERMISSIONsxxx", (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)).toString())
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //El usuario ya ha rechazado los permisos
                    Toast.makeText(activity, "Ir a configuracion y aceptar los permisos", Toast.LENGTH_SHORT).show()
                }else{
                    //Pedir permisos
                    Toast.makeText(requireActivity(), "pedir permisos", Toast.LENGTH_SHORT).show()

                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), RequestPermissions.CODE_REQUEST_PERMISSION_READ_STORAGE)
                }
            }else{
                openStorage()
            }
        }

        binding.btnLogin.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val lastName  =binding.etLastName.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()


            if (name.isEmpty()){
                binding.etName.error = "The field must not be empty"
                return@setOnClickListener
            }

            if (lastName.isEmpty()){
                binding.etLastName.error = "The field must not be empty"
                return@setOnClickListener
            }
            if (description.isEmpty()){
                binding.etDescription.error = "The field must not be empty"
                return@setOnClickListener
            }


            Log.d("USERBBB", "PASE LA VALIDACION")
            viewModel.saveDataUser(
                name,
                lastName,
                description
            )


        }
    }
    

    private fun openStorage(){
        editProfileObserver.selectImage()
    }



    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == RequestPermissions.CODE_REQUEST_PERMISSION_READ_STORAGE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openStorage()
            }else{
                //el permiso no ha sido aceptado
                Toast.makeText(requireActivity(), "Permiso rechazado", Toast.LENGTH_SHORT)
            }
        }
    }

    fun initUIStarted(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.image.collect{
                    if(it.isNotEmpty()){
                        val bitmap = BitmapFactory.decodeFile(it)
                        binding.circleImageView.setImageBitmap(bitmap)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.statusDb.collect{status ->
                    Log.d("USERBBB", status.toString())

                    when(status){
                        is Status.Error -> {
                            Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                        }
                        is Status.Success -> {
                            Log.d("USERBBB", status.data.toString())
                            //Ir a la actividad principal
                            val intent = Intent(requireActivity(), HomeActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                            withContext(Dispatchers.IO){
                                //Activamos el login
                                val sharendPreference = PreferenceManager(requireActivity())
                                sharendPreference.setUser(status.data)
                                sharendPreference.setFirstTimeLaunch(true)
                            }
                        }
                    }

                }

            }
        }
    }


}