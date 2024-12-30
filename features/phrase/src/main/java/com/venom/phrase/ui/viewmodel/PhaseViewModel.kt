package com.venom.phrase.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venom.phrase.data.repo.PhraseRepository
import com.venom.phrase.data.model.Category
import com.venom.phrase.data.model.Phrase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhrasebookViewModel @Inject constructor(
    private val repository: PhraseRepository
) : ViewModel() {

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _phrases = MutableLiveData<List<Phrase>>()
    val phrases: LiveData<List<Phrase>> = _phrases

    fun loadCategories() {
        viewModelScope.launch {
            _categories.value = repository.getAllCategories()
        }
    }

    fun loadPhrasesForSection(sectionId: Int) {
        viewModelScope.launch {
            _phrases.value = repository.getPhrasesForSection(sectionId)
        }
    }
}