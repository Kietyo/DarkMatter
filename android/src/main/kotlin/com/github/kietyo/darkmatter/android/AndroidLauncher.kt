package com.github.kietyo.darkmatter.android

import com.badlogic.gdx.backends.android.AndroidApplication
import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.github.kietyo.darkmatter.DarkMatter

/** Launches the Android application.  */
class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val configuration = AndroidApplicationConfiguration()
        initialize(DarkMatter(), configuration)
    }
}