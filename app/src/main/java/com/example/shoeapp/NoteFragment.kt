package com.example.shoeapp

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.shoeapp.Extensions.toast
import com.example.shoeapp.databinding.FragmentCartpageBinding
import com.example.shoeapp.databinding.FragmentNoteBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.log

class NoteFragment : Fragment(R.layout.fragment_note) {
    private lateinit var binding: FragmentNoteBinding

    companion object {
        lateinit var database: AppDatabase
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNoteBinding.bind(view)

        initializeDatabase()

        binding.btnAddNote.setOnClickListener{

            val noteTitle = binding.noteTitle.text.toString()
            val noteDesc = binding.noteDescription.text.toString()

            Log.d("msg", noteTitle)

            if(noteTitle.isEmpty() || noteDesc.isEmpty()){
                requireActivity().toast("All Fields are required")
            }else{
                GlobalScope.launch(Dispatchers.IO) {

                    database.noteDao().insert(Note(title = noteTitle, content = noteDesc))
                }

                requireActivity().toast("Added To Database")
                binding.noteTitle.setText("")
                binding.noteDescription.setText("")
            }


        }

        binding.btnViewNotes.setOnClickListener{
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_layout, NoteDetailFragment())
                .addToBackStack(null)
            fragmentTransaction.commit()
        }

    }

    private fun initializeDatabase() {
        GlobalScope.launch(Dispatchers.IO) {
            database = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "notes")
                .build()
        }
    }
}