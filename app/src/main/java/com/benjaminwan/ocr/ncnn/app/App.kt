package com.benjaminwan.ocr.ncnn.app

import android.app.Application
import android.util.Log
import com.benjaminwan.ocr.ncnn.BuildConfig
import com.benjaminwan.ocr.ncnn.RecordsRepository
import com.benjaminwan.ocr.ncnn.utils.RecordsRoomDatabase
import com.benjaminwan.ocrlibrary.OcrEngine
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.LogStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class App : Application() {
    companion object {
        lateinit var INSTANCE: App
        lateinit var ocrEngine: OcrEngine
    }

    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { RecordsRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { RecordsRepository(database.recordsDao()) }
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        initLogger()
        initOCREngine()
    }
    private fun initOCREngine() {
        ocrEngine = OcrEngine(this.applicationContext)
    }

    private fun initLogger() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true)
            .tag("OcrLite")
            .logStrategy(LogCatStrategy())
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean = BuildConfig.DEBUG
        })
    }

    inner class LogCatStrategy : LogStrategy {

        private var last: Int = 0

        override fun log(priority: Int, tag: String?, message: String) {
            Log.println(priority, randomKey() + tag!!, message)
        }

        private fun randomKey(): String {
            var random = (10 * Math.random()).toInt()
            if (random == last) {
                random = (random + 1) % 10
            }
            last = random
            return random.toString()
        }
    }
}