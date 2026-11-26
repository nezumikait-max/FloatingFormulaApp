package com.example.myapplication.data

import kotlinx.coroutines.flow.Flow

class FormulaRepository(private val formulaDao: FormulaDao) {
    val allFormulas: Flow<List<Formula>> = formulaDao.getAllFormulas()
    val allTopics: Flow<List<String>> = formulaDao.getAllTopics()
    val favoriteFormulas: Flow<List<Formula>> = formulaDao.getFavoriteFormulas()

    fun getFormulasByTopic(topic: String): Flow<List<Formula>> {
        return formulaDao.getFormulasByTopic(topic)
    }

    suspend fun updateFormula(formula: Formula) {
        formulaDao.updateFormula(formula)
    }
}
