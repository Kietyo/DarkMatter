package com.github.kietyo.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.kietyo.darkmatter.DarkMatter
import ktx.graphics.use
import ktx.log.debug
import ktx.log.logger

class GameScreen(game: DarkMatter) : DarkMatterScreen(game) {
    private val texture = Texture(Gdx.files.internal("graphics/ship_base.png"))
    private val sprite = Sprite(texture)
    override fun show() {
        super.show()
        log.debug { "First screen is shown." }
        sprite.setPosition(1f, 1f)
    }

    override fun render(delta: Float) {
        super.render(delta)
        batch.use {
            sprite.draw(it)
        }
    }

    override fun dispose() {
        super.dispose()
        texture.dispose()
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}