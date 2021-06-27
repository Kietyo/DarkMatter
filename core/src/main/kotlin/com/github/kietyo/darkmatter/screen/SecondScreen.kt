package com.github.kietyo.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.github.kietyo.darkmatter.DarkMatter
import ktx.app.KtxScreen
import ktx.log.debug
import ktx.log.logger

class SecondScreen(game: DarkMatter) : DarkMatterScreen(game) {
    override fun show() {
        super.show()
        log.debug { "Second screen is shown." }
    }

    override fun render(delta: Float) {
        super.render(delta)
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            game.setScreen<FirstScreen>()
        }
    }

    companion object {
        private val log = logger<SecondScreen>()
    }
}