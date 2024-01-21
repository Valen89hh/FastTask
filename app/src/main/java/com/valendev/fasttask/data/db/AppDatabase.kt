package com.valendev.fasttask.data.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.valendev.fasttask.R
import com.valendev.fasttask.data.dao.CategoryDao
import com.valendev.fasttask.data.dao.TaskDao
import com.valendev.fasttask.data.dao.UserDao
import com.valendev.fasttask.domain.constants.DATABASE_NAME
import com.valendev.fasttask.domain.model.Category
import com.valendev.fasttask.domain.model.Task
import com.valendev.fasttask.domain.model.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Database(
    entities = [UserModel::class, Task::class, Category::class],
    version = 1,
    exportSchema = true,

)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_2_3: Migration = object : Migration(2,3){
            override fun migrate(database: SupportSQLiteDatabase) {
// Crear la nueva tabla con la columna image
                database.execSQL("CREATE TABLE users_new (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, lastName TEXT, description TEXT, image BLOB)")

                // Copiar los datos de la tabla antigua a la nueva
                database.execSQL("INSERT INTO users_new (id, name, lastName, description, image) SELECT id, name, lastName, description, CAST(image AS BLOB) FROM users")

                // Eliminar la tabla antigua
                database.execSQL("DROP TABLE users")

                // Renombrar la nueva tabla para que tenga el nombre original
                database.execSQL("ALTER TABLE users_new RENAME TO users")
            }
        }

        fun getDatabase(context: Context): AppDatabase{
            return  INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(object : RoomDatabase.Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch{
                                initDataDb(context)
                            }
                        }
                    })
                    .build()

                INSTANCE = instance

                instance
            }
        }

        private suspend fun initDataDb(context: Context){
            val categoryDao = getDatabase(context).categoryDao()

            categoryDao.insertAll(getDatosPredeterminados())

        }

        private fun getDatosPredeterminados(): List<Category>{
            return listOf(
                Category(
                    nameCategory = "Work",
                    imageCategory = R.drawable.ic_maletin_24,
                    color = R.color.accent_yellow_suave,
                    colorSelected = R.color.accent_yellow
                ),
                Category(
                    nameCategory = "Sport",
                    imageCategory = R.drawable.ic_corazon_24,
                    color = R.color.accent_green_suave,
                    colorSelected = R.color.accent_green
                ),
                Category(
                    nameCategory = "Habits",
                    imageCategory = R.drawable.ic_image_24,
                    color = R.color.accent_purple_suave,
                    colorSelected = R.color.accent_purple
                ),
                Category(
                    nameCategory = "Study",
                    imageCategory = R.drawable.ic_estudy_24,
                    color = R.color.accent_orange_suave,
                    colorSelected = R.color.accent_orange
                ),
            )
        }
    }

}