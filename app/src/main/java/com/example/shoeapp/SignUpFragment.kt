package com.example.shoeapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.shoeapp.Extensions.toast
import com.example.shoeapp.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream

class SignUpFragment : Fragment(R.layout.fragment_signup) {

    private lateinit var binding: FragmentSignupBinding
    private lateinit var auth : FirebaseAuth
    private val MY_PERMISSIONS_REQUEST_STORAGE = 123


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignupBinding.bind(view)
        auth = FirebaseAuth.getInstance()


        binding.btnSignUp.setOnClickListener {

            if (
                binding.etEmailSignUp.text.isNotEmpty() &&
                binding.etNameSignUp.text.isNotEmpty() &&
                binding.etPasswordSignUp.text.isNotEmpty()
            ) {

                  createUser(binding.etEmailSignUp.text.toString(),binding.etPasswordSignUp.text.toString())


            }else{
                Log.e("Error", "All fields are required")
                requireActivity().toast("All fields are required")
            }
        }
        binding.tvNavigateToSignIn.setOnClickListener {

            requireActivity().supportFragmentManager.popBackStack()
//            Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_signInFragmentFragment)
        }


    }

    private fun createUser(email: String, password: String) {
        Log.d("MSG", password)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {task ->
                if(task.isSuccessful){
                    Log.d("Success", "User Created")

                    requireActivity().supportFragmentManager.popBackStack()

                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ),
                            MY_PERMISSIONS_REQUEST_STORAGE
                        )
                    }else{
                        val filename = "users.txt"
                        val fileContents = binding.etNameSignUp.text.toString() + " " + auth.uid;

                        val externalFile = File(Environment.getExternalStorageDirectory(), filename)

                        try {
                            val outputStream = FileOutputStream(externalFile)
                            outputStream.write(fileContents.toByteArray())
                            outputStream.close()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    requireActivity().toast("New User created & Added to File")
                }
                else{
                    Log.e("Error", "Something wrong")
                    requireActivity().toast(task.exception!!.localizedMessage)
                }

            }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val filename = "users.txt"
                    val fileContents = binding.etNameSignUp.text.toString() + " " + auth.uid;

                    val externalFile = File(Environment.getExternalStorageDirectory(), filename)

                    try {
                        val outputStream = FileOutputStream(externalFile)
                        outputStream.write(fileContents.toByteArray())
                        outputStream.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    Log.d("MSG", "Here!!!!")
                } else {
                    requireActivity().toast("You denied file access")
                }
            }
        }
    }



}