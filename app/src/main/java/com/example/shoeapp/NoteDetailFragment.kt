package com.example.shoeapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoeapp.Models.noteViewModel
import com.example.shoeapp.databinding.FragmentViewnotesBinding
import noteAdapter

class NoteDetailFragment : Fragment(R.layout.fragment_viewnotes) {
    private lateinit var binding: FragmentViewnotesBinding
    private lateinit var notes: List<Note>
    private lateinit var noteadapter: noteAdapter
    private val viewModel: noteViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentViewnotesBinding.bind(view)

        binding.notedetailActualToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        notes = listOf<Note>()
        noteadapter = noteAdapter(notes)


        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = noteadapter

        viewModel.notesLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { notes ->
            noteadapter.updateData(notes)
        })

        viewModel.fetchData()
    }

}