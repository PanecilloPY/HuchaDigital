package com.example.huchadigital

import android.app.Application
import com.example.huchadigital.model.Hucha

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Hucha.init(this)
    }
}
