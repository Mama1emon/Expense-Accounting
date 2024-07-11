package com.mama1emon.exac

import android.app.Application
import di.initKoin
import utils.AndroidPlatformContextProvider

/**
 * @author Andrew Khokhlov on 11/07/2024
 */
class EaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidPlatformContextProvider.setContext(this) // TODO use Koin

        initKoin()
    }
}