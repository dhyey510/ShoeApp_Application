package com.example.shoeapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.recreate
import androidx.navigation.fragment.findNavController
import com.example.shoeapp.Extensions.toast
import com.example.shoeapp.databinding.FragmentSigninBinding
import com.example.shoeapp.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class SignInFragmentFragment : Fragment(R.layout.fragment_signin) {

    private lateinit var binding: FragmentSigninBinding
    private lateinit var auth: FirebaseAuth


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSigninBinding.bind(view)
        auth = FirebaseAuth.getInstance()



        if (auth.currentUser != null) {
            val intent = Intent(activity, MainPageActivity::class.java)
            requireActivity().startActivity(intent)
//            Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
//                .navigate(R.id.action_signInFragmentFragment_to_mainFragment)
        }

        binding.btnSignIn.setOnClickListener {

            if (
                binding.etEmailSignIn.text.isNotEmpty() &&
                binding.etPasswordSignIn.text.isNotEmpty()
            ) {


                signinUser(binding.etEmailSignIn.text.toString(),
                    binding.etPasswordSignIn.text.toString())


            } else {
                requireActivity().toast("Some Fields are Empty")
            }
        }
        loadLocate()

        binding.btnLanguage.setOnClickListener {
            showChangeLang()
        }

        binding.tvNavigateToSignUp.setOnClickListener {

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainerView, SignUpFragment())
                .addToBackStack(null)
            fragmentTransaction.commit()
//            Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
//                .navigate(R.id.action_signInFragmentFragment_to_signUpFragment)
        }


    }

    private fun showChangeLang() {

        val listItems = arrayOf("English", "عربي", "हिंदी", "中国人")

        val builder = androidx.appcompat.app.AlertDialog.Builder(requireActivity())
        builder.setTitle("Choose Language")
        builder.setSingleChoiceItems(listItems, -1) {dialog, which ->
            when (which) {
                0 -> {
                    setLocale("en")
                }
                1 -> {
                    setLocale("ar")
                }
                2 -> {
                    setLocale("hi")
                }
                3->{
                    setLocale("zh")
                }
            }

            dialog.dismiss()

            requireActivity().recreate()

        }
        val mDialog = builder.create()
        mDialog.show()
    }

    private fun setLocale(Lang: String) {

        val locale = Locale(Lang)

        Locale.setDefault(locale)

        val config = Configuration()

        config.locale = locale
        val context = requireContext()
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("My_Lang", Lang)
        editor.apply()

//        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
//
//        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
//        editor.putString("My_Lang", Lang)
//        editor.apply()
    }

    private fun loadLocate() {
        val sharedPreferences = requireContext().getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")
        if (language != null) {
            setLocale(language)
        }
    }

    private fun signinUser(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    requireActivity().toast("Sign In Successful")
                    Log.d("Success", "Sign In successful")

                    val intent = Intent(activity, MainPageActivity::class.java)
                    requireActivity().startActivity(intent)

                } else {
                    Log.e("Error", "Something wrong")
                    requireActivity().toast("Invalid Credentials")

                }


            }

    }


}