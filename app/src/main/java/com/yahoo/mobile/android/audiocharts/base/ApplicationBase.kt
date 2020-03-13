package com.yahoo.mobile.android.audiocharts.base

import android.app.Application

open class ApplicationBase : Application() {

    companion object {
        lateinit var instance: ApplicationBase
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
    }
}
