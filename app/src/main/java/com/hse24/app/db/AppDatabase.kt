package com.hse24.app.db

import android.content.Context

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

import com.hse24.app.db.dao.*
import com.hse24.app.db.entity.*

@Database(
    entities = [CategoryEntity::class, ImageUriEntity::class, ProductEntity::class, CartEntity::class, CategoryCountEntity::class, VariationEntity::class],
    version = 16
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun imageDao(): ImageDao
    abstract fun cartDao(): CartDao
    abstract fun categoryCountDao(): CategoryCountDao
    abstract fun variationDao(): VariationDao

    private val mIsDatabaseCreated = MutableLiveData<Boolean>()

    companion object {
        @Volatile
        private var sInstance: AppDatabase? = null

        const val DATABASE_NAME = "hse24-db"

        fun getInstance(context: Context): AppDatabase? {
            if (sInstance == null) {
                synchronized(this) {
                    if (sInstance == null) {
                        sInstance = buildDatabase(context.applicationContext)
                        sInstance!!.updateDatabaseCreated(context.applicationContext)
                    }
                }
            }
            return sInstance
        }

        private fun buildDatabase(appContext: Context): AppDatabase {
            return Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Generate the data for pre-population
                        val database = getInstance(appContext)
                        // notify that the database was created and it's ready to be used
                        database!!.setDatabaseCreated()
                    }
                })
                .build()
        }
    }

    /**
     * Check whether the database already exists and expose it via [.getDatabaseCreated]
     */
    private fun updateDatabaseCreated(context: Context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated()
        }
    }

    private fun setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true)
    }

    fun getDatabaseCreated(): LiveData<Boolean> {  return  mIsDatabaseCreated }

}