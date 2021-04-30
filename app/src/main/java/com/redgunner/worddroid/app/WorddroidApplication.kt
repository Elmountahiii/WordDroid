package com.redgunner.worddroid.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WorddroidApplication: Application()  {

    override fun onCreate() {
        super.onCreate()


    }
}