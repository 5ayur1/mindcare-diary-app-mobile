package com.fiap.mindcarediary

import android.app.Application
import com.fiap.mindcarediary.service.RetrofitClient

class MindCareApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        RetrofitClient.initialize(this)
    }
}