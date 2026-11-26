package com.example.myapplication.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.Formula
import com.example.myapplication.data.FormulaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FormulaRepository

    init {
        val formulaDao = AppDatabase.getDatabase(application).formulaDao()
        repository = FormulaRepository(formulaDao)
    }

    val allTopics: StateFlow<List<String>> = repository.allTopics.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allFormulas: StateFlow<List<Formula>> = repository.allFormulas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toggleFavorite(formula: Formula) {
        viewModelScope.launch {
            repository.updateFormula(formula.copy(isFavorite = !formula.isFavorite))
        }
    }
}
