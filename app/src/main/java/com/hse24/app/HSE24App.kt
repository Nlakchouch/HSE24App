package com.hse24.app

import android.app.Application
import com.hse24.app.db.AppDatabase

/**
 * Android Application class. Used for accessing singletons.
 */
class HSE24App : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    private fun getDatabase(): AppDatabase? { return AppDatabase.getInstance(this)}
    fun getRepository(): DataRepository? { return getDatabase()?.let { DataRepository.getInstance(it) } }
}