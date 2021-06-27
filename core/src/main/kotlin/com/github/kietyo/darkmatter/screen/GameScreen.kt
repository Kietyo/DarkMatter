package com.github.kietyo.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.kietyo.darkmatter.DarkMatter
import com.github.kietyo.darkmatter.UNIT_SCALE
import ktx.graphics.use
import ktx.log.debug
import ktx.log.logger

class GameScreen(game: DarkMatter) : DarkMatterScreen(game) {
    private val viewport = FitViewport(9f, 16f)
    private val texture = Texture(Gdx.files.internal("graphics/ship_base.png"))
    private val sprite = Sprite(texture).apply {
//        setSize(9 * UNIT_SCALE, 16 * UNIT_SCALE)
        setSize(9 * UNIT_SCALE, 16 * UNIT_SCALE)
    }

    private val sprite2 = Sprite(texture).apply {
        //        setSize(9 * UNIT_SCALE, 16 * UNIT_SCALE)
        setSize(1f, 1f)
    }

    private val sprites = mutableListOf<Sprite>()

    override fun show() {
        super.show()
        log.debug { "First screen is shown." }
//        sprite.setPosition(1f, 1f)
//        sprite2.setPosition(1f, 2f)
        for (i in 0..9) {
            sprites.add(Sprite(texture).apply {
                setSize(9 * UNIT_SCALE, 16 * UNIT_SCALE)
                setPosition(i.toFloat(), 1f) })
        }
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        super.render(delta)
        batch.use(viewport.camera.combined) {
            for (sprite in sprites) {
                sprite.draw(it)
            }
//            sprite.draw(it)
//            sprite2.draw(it)
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