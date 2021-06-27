package com.github.kietyo.darkmatter

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.github.kietyo.darkmatter.screen.FirstScreen
import com.github.kietyo.darkmatter.screen.SecondScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.log.info
import ktx.log.logger

class DarkMatter : KtxGame<KtxScreen>() {
    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        log.info { "Creating game instance" }
        addScreen(FirstScreen(this))
        addScreen(SecondScreen(this))
        setScreen<FirstScreen>()
    }

    companion object {
        private val log = logger<DarkMatter>()
    }
}