package com.example.myapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Formula::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun formulaDao(): FormulaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "formula_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate database
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val sampleFormulas = listOf(
                                    Formula(topic = "Physics", title = "Newton's Second Law", equation = "F = m \\cdot a", legend = "F: Force\nm: Mass\na: Acceleration"),
                                    Formula(topic = "Physics", title = "Einstein's Mass-Energy", equation = "E = m \\cdot c^2", legend = "E: Energy\nm: Mass\nc: Speed of light"),
                                    Formula(topic = "Math", title = "Quadratic Formula", equation = "x = \\frac{-b \\pm \\sqrt{b^2 - 4ac}}{2a}", legend = "x: Roots of the equation\na, b, c: Coefficients"),
                                    Formula(topic = "Math", title = "Pythagorean Theorem", equation = "a^2 + b^2 = c^2", legend = "a, b: Legs of the right triangle\nc: Hypotenuse"),
                                    Formula(topic = "Chemistry", title = "Ideal Gas Law", equation = "P \\cdot V = n \\cdot R \\cdot T", legend = "P: Pressure\nV: Volume\nn: Number of moles\nR: Ideal gas constant\nT: Temperature")
                                )
                                database.formulaDao().insertFormulas(sampleFormulas)
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
