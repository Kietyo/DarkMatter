package com.github.kietyo.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.kietyo.darkmatter.ecs.component.GraphicComponent
import com.github.kietyo.darkmatter.ecs.component.TransformComponent
import com.github.kietyo.darkmatter.extensions.getNonNull
import ktx.ashley.allOf
import ktx.graphics.use
import ktx.log.error
import ktx.log.logger

class RenderSystem(
    private val batch: Batch, private val gameViewport: Viewport,
    private val uiViewport: Viewport,
    backgroundTexture: Texture
) :
    SortedIteratingSystem
        (allOf(
        TransformComponent::class,
        GraphicComponent::class
    ).get(),
        compareBy { entity -> entity.getNonNull(TransformComponent.mapper) }
    ) {
    private val background = Sprite(backgroundTexture.apply {
        setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
    })

    private val backgroundScrollSpeed = Vector2(0.03f, -0.25f)

    override fun update(deltaTime: Float) {
        forceSort()
        gameViewport.apply()
        batch.use(uiViewport.camera.combined) {
            // render background
            background.run {
                scroll(backgroundScrollSpeed.x * deltaTime, backgroundScrollSpeed.y * deltaTime)
                draw(batch)
            }
        }

        batch.use(gameViewport.camera.combined) {
            // rendering entities
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.getNonNull(TransformComponent.mapper)
        val graphic = entity.getNonNull(GraphicComponent.mapper)

        if (graphic.sprite.texture == null) {
            log.error { "Entity is missing texture. entity=$entity" }
            throw GdxRuntimeException("Entity is missing texture. entity=$entity")
        }

        graphic.sprite.run {
            rotation = transform.rotationDeg
            setBounds(
                transform.interpolatedPosition.x,
                transform.interpolatedPosition.y,
                transform.size.x,
                transform.size.y
            )
            draw(batch)
        }
    }

    companion object {
        val log = logger<RenderSystem>()
    }
}