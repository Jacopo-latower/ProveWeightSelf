package com.example.provehomefragments

import android.app.Application
import androidx.multidex.MultiDexApplication
import io.realm.Realm

class MyApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}