package com.github.kietyo.darkmatter

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.kietyo.darkmatter.ecs.system.*
import com.github.kietyo.darkmatter.event.GameEventManager
import com.github.kietyo.darkmatter.screen.DarkMatterScreen
import com.github.kietyo.darkmatter.screen.GameScreen
import ktx.app.KtxGame
import ktx.log.info
import ktx.log.logger

const val UNIT_SCALE = 1 / 16f

const val V_WIDTH_PIXELS = 135
const val V_HEIGHT_PIXELS = 240

const val V_WIDTH = 9
const val V_HEIGHT = 16

class DarkMatter : KtxGame<DarkMatterScreen>() {

    private val graphicsAtlas : TextureAtlas by lazy { TextureAtlas(Gdx.files.internal
        ("graphics/graphics.atlas")) }
    private val backgroundTexture by lazy { Texture("graphics/background.png") }

    val uiViewport = FitViewport(V_WIDTH_PIXELS.toFloat(), V_HEIGHT_PIXELS.toFloat())
    val gameViewport = FitViewport(V_WIDTH.toFloat(), V_HEIGHT.toFloat())

    val gameEventManager = GameEventManager()

    val batch: SpriteBatch by lazy { SpriteBatch() }
    val engine: Engine by lazy { PooledEngine().apply {
        addSystem(PlayerInputSystem(gameViewport))
        addSystem(MoveSystem())
        addSystem(PowerUpSystem(gameEventManager))
        addSystem(DamageSystem(gameEventManager))
        addSystem(CameraShakeSystem(gameViewport.camera, gameEventManager))
        addSystem(PlayerAnimationSystem(
            graphicsAtlas.findRegion("ship_base"),
            graphicsAtlas.findRegion("ship_left"),
            graphicsAtlas.findRegion("ship_right"),
        ))
        addSystem(AttachSystem())
        addSystem(AnimationSystem(graphicsAtlas))
        addSystem(RenderSystem(batch, gameViewport, uiViewport, backgroundTexture, gameEventManager))
        addSystem(RemoveSystem())
        addSystem(DebugSystem())
    } }

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        log.info { "Creating game instance" }
        addScreen(GameScreen(this))
        setScreen<GameScreen>()
    }

    override fun dispose() {
        super.dispose()
        log.info { "Disposing ${batch.maxSpritesInBatch} sprites." }
        batch.dispose()
        graphicsAtlas.dispose()
        backgroundTexture.dispose()
    }

    companion object {
        private val log = logger<DarkMatter>()
    }
}