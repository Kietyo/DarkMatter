package com.github.kietyo.darkmatter

import com.badlogic.gdx.Game
import com.github.kietyo.darkmatter.FirstScreen

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class DarkMatter : Game() {
    override fun create() {
        setScreen(FirstScreen())
    }
}