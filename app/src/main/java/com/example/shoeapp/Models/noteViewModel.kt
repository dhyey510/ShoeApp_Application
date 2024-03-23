package com.example.shoeapp.Models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoeapp.Note
import com.example.shoeapp.NoteFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class noteViewModel : ViewModel() {

    // LiveData to observe the result
    val notesLiveData: MutableLiveData<List<Note>> = MutableLiveData()

    fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = NoteFragment.database.noteDao().getAllNotes()
            notesLiveData.postValue(result)
        }
    }
}
