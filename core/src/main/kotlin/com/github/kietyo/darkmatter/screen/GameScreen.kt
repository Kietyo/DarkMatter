package com.github.kietyo.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.kietyo.darkmatter.DarkMatter
import com.github.kietyo.darkmatter.UNIT_SCALE
import com.github.kietyo.darkmatter.ecs.component.FacingComponent
import com.github.kietyo.darkmatter.ecs.component.GraphicComponent
import com.github.kietyo.darkmatter.ecs.component.PlayerComponent
import com.github.kietyo.darkmatter.ecs.component.TransformComponent
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ktx.graphics.use
import ktx.log.debug
import ktx.log.logger

class GameScreen(game: DarkMatter) : DarkMatterScreen(game) {

    override fun show() {
        super.show()
        log.debug { "First screen is shown." }
        repeat(1000) {
            engine.entity {
                with<TransformComponent>() {
                    position.set(MathUtils.random(0f, 9f), MathUtils.random(0f, 16f), MathUtils
                        .random(0f, 900000f))
                }
                with<GraphicComponent>()
                with<PlayerComponent>()
                with<FacingComponent>()
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
    }

    override fun render(delta: Float) {
        super.render(delta)
        engine.update(delta)
    }

    override fun dispose() {
        super.dispose()
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}