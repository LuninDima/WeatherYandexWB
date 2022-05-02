package com.example.weatheryandex.app

import android.app.Application


class App: Application() {
    override fun onCreate() {
        super.onCreate()
        appInstane = this
    }

    companion object{
        private var appInstane: App? = null

    }
}