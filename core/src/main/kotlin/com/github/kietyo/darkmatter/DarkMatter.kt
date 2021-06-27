package com.github.kietyo.darkmatter

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.github.kietyo.darkmatter.screen.DarkMatterScreen
import com.github.kietyo.darkmatter.screen.GameScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.log.info
import ktx.log.logger

class DarkMatter : KtxGame<DarkMatterScreen>() {
    val batch: Batch by lazy { SpriteBatch() }

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        log.info { "Creating game instance" }
        addScreen(GameScreen(this))
        setScreen<GameScreen>()
    }

    companion object {
        private val log = logger<DarkMatter>()
    }
}