package com.github.kietyo.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.kietyo.darkmatter.DarkMatter
import com.github.kietyo.darkmatter.UNIT_SCALE
import com.github.kietyo.darkmatter.ecs.component.GraphicComponent
import com.github.kietyo.darkmatter.ecs.component.TransformComponent
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ktx.graphics.use
import ktx.log.debug
import ktx.log.logger

class GameScreen(game: DarkMatter) : DarkMatterScreen(game) {
    private val viewport = FitViewport(9f, 16f)
    private val playerTexture = Texture(Gdx.files.internal("graphics/ship_base.png"))
    private val player = game.engine.entity {
        with<TransformComponent>() {
            position.set(1f, 1f, 1f)
        }
        with<GraphicComponent>() {
            sprite.run {
                setRegion(playerTexture)
                setSize(texture.width * UNIT_SCALE, texture.height * UNIT_SCALE)
                setOriginCenter()
            }
        }
    }

    override fun show() {
        super.show()
        log.debug { "First screen is shown." }
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        super.render(delta)
        engine.update(delta )
        batch.use(viewport.camera.combined) { batch ->
            player[GraphicComponent.mapper]!!.let { graphic ->
                player[TransformComponent.mapper]!!.let { transform ->
                    graphic.sprite.run {
                        rotation = transform.rotationDeg
                        setBounds(transform.position.x, transform.position.y, transform.size.x,
                            transform.size.y)
                        draw(batch)
                    }
                }
            }
        }
    }

    override fun dispose() {
        super.dispose()
        playerTexture.dispose()
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}