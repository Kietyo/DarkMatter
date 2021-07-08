package com.github.kietyo.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g3d.model.Animation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.kietyo.darkmatter.DarkMatter
import com.github.kietyo.darkmatter.UNIT_SCALE
import com.github.kietyo.darkmatter.V_WIDTH
import com.github.kietyo.darkmatter.ecs.component.*
import com.github.kietyo.darkmatter.ecs.system.DAMAGE_AREA_HEIGHT
import com.github.kietyo.darkmatter.event.GameEvent
import com.github.kietyo.darkmatter.event.GameEventListener
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ktx.graphics.use
import ktx.log.debug
import ktx.log.logger
import kotlin.math.min

private const val MAX_DELTA_TIME_SEC = 1 / 20f

class GameScreen(game: DarkMatter) : DarkMatterScreen(game), GameEventListener {

    override fun show() {
        super.show()
        log.debug { "First screen is shown." }
        gameEventManager.addListeners<GameEvent.PlayerDeath>(this)
        spawnPlayer()

        engine.entity {
            with<TransformComponent>() {
                size.set(V_WIDTH.toFloat(), DAMAGE_AREA_HEIGHT)
            }
            with<AnimationComponent>() {
                type = AnimationType.DARK_MATTER
            }
            with<GraphicComponent>()
        }
    }

    override fun hide() {
        super.hide()
        gameEventManager.removeListener(this)
    }

    private fun spawnPlayer() {
        val playerShip = engine.entity {
            with<TransformComponent>() {
                setInitialPosition(4.5f, 8f, -1f)
            }
            with<MoveComponent>()
            with<GraphicComponent>()
            with<PlayerComponent>()
            with<FacingComponent>()
        }

        engine.entity {
            with<TransformComponent>()
            with<AttachComponent> {
                entity = playerShip
                offset.set(1f * UNIT_SCALE, -6f * UNIT_SCALE)
            }
            with<GraphicComponent>()
            with<AnimationComponent> {
                type = AnimationType.FIRE
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
    }

    override fun render(delta: Float) {
        super.render(delta)
        engine.update(min(MAX_DELTA_TIME_SEC, delta))
    }

    override fun dispose() {
        super.dispose()
    }

    companion object {
        private val log = logger<GameScreen>()
    }

    override fun onEvent(gameEvent: GameEvent) {
        if (gameEvent is GameEvent.PlayerDeath) {
            spawnPlayer()
        }
    }
}