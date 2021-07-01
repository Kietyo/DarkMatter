package com.github.kietyo.darkmatter

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.kietyo.darkmatter.ecs.system.PlayerAnimationSystem
import com.github.kietyo.darkmatter.ecs.system.PlayerInputSystem
import com.github.kietyo.darkmatter.ecs.system.RenderSystem
import com.github.kietyo.darkmatter.screen.DarkMatterScreen
import com.github.kietyo.darkmatter.screen.GameScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.log.info
import ktx.log.logger

const val UNIT_SCALE = 1 / 16f

class DarkMatter : KtxGame<DarkMatterScreen>() {
    private val defaultRegion: TextureRegion by lazy { TextureRegion(Texture(Gdx.files.internal
        ("graphics/ship_base.png"))) }
    private val leftRegion: TextureRegion by lazy { TextureRegion(Texture(Gdx.files.internal
        ("graphics/ship_left.png"))) }
    private val rightRegion: TextureRegion by lazy { TextureRegion(Texture(Gdx.files.internal
        ("graphics/ship_right.png"))) }

    val gameViewport = FitViewport(9f, 16f)
    val batch: SpriteBatch by lazy { SpriteBatch() }
    val engine: Engine by lazy { PooledEngine().apply {
        addSystem(PlayerInputSystem(gameViewport))
        addSystem(PlayerAnimationSystem(
            defaultRegion, leftRegion, rightRegion
        ))
        addSystem(RenderSystem(batch, gameViewport))
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
        defaultRegion.texture.dispose()
        leftRegion.texture.dispose()
        rightRegion.texture.dispose()
    }

    companion object {
        private val log = logger<DarkMatter>()
    }
}