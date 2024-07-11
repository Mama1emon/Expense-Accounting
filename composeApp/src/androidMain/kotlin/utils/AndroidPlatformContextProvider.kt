package utils

import android.content.Context
import kotlin.properties.Delegates

object AndroidPlatformContextProvider {
    private var appContext: Context by Delegates.notNull()

    val context: Context get() = appContext

    fun setContext(context: Context) {
        appContext = context
    }
}