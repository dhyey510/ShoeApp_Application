package com.example.shoeapp

import android.app.Application
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.shoeapp.databinding.ActivityMainpageBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileInputStream

class MainPageActivity : AppCompatActivity(){

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainpageBinding

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(baseContext, "Landscape Mode", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Toast.makeText(baseContext, "Portrait Mode", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Load portrait layout
            setContentView(R.layout.activity_mainpage)
        } else {
            // Load landscape layout
            setContentView(R.layout.activity_mainpage)
        }

        binding = ActivityMainpageBinding.inflate(layoutInflater);
        auth = FirebaseAuth.getInstance()


        val filename = "users.txt"
        val fileContents: String

        val externalFile = File(Environment.getExternalStorageDirectory(), filename)

        try {
            val inputStream = FileInputStream(externalFile)
            fileContents = inputStream.bufferedReader().use { it.readText() }

            val rootView: View = findViewById(android.R.id.content)
            val snackbar = Snackbar.make(rootView, fileContents, Snackbar.LENGTH_LONG)
            snackbar.setBackgroundTint(Color.BLUE)
            snackbar.setTextColor(Color.WHITE)
            snackbar.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }


        setContentView(binding.root)


        replaceFragment(MainFragment())

        binding.BtmNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mainFragment -> replaceFragment(MainFragment())
                R.id.likeFragment -> replaceFragment(LikeFragment())
                R.id.cartFragment -> replaceFragment(CartFragment())
                R.id.noteFragment -> replaceFragment(NoteFragment())
                R.id.profileFragment -> {

                    auth.signOut()
                    finish()
                }

                else -> {

                }
            }
            true

        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}