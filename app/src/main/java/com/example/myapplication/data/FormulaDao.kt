package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FormulaDao {
    @Query("SELECT * FROM formulas")
    fun getAllFormulas(): Flow<List<Formula>>

    @Query("SELECT DISTINCT topic FROM formulas")
    fun getAllTopics(): Flow<List<String>>

    @Query("SELECT * FROM formulas WHERE topic = :topic")
    fun getFormulasByTopic(topic: String): Flow<List<Formula>>

    @Query("SELECT * FROM formulas WHERE isFavorite = 1")
    fun getFavoriteFormulas(): Flow<List<Formula>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFormulas(formulas: List<Formula>)

    @Update
    suspend fun updateFormula(formula: Formula)
}
