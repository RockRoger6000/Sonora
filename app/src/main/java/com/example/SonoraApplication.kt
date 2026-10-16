package com.example

import android.app.Application
import com.example.database.SonoraDatabase
import com.example.playback.SonoraAudioEngine

class SonoraApplication : Application() {
    lateinit var database: SonoraDatabase
        private set

    lateinit var audioEngine: SonoraAudioEngine
        private set

    override fun onCreate() {
        super.onCreate()
        database = SonoraDatabase.getInstance(this)
        audioEngine = SonoraAudioEngine(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        audioEngine.release()
    }
}
