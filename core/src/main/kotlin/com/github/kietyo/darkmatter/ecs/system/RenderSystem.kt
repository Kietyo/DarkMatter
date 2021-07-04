package com.github.kietyo.darkmatter.ecs.system

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.kietyo.darkmatter.ecs.component.GraphicComponent
import com.github.kietyo.darkmatter.ecs.component.TransformComponent
import com.github.kietyo.darkmatter.extensions.get
import ktx.ashley.allOf
import ktx.graphics.use
import ktx.log.error
import ktx.log.logger

class RenderSystem(private val batch: Batch, private val gameViewport: Viewport) : SortedIteratingSystem
    (allOf(
    TransformComponent::class,
    GraphicComponent::class
).get(),
    compareBy { entity -> entity[TransformComponent.mapper] }
) {
    override fun update(deltaTime: Float) {
        forceSort()
        gameViewport.apply()
        batch.use(gameViewport.camera.combined) {
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        val graphic = entity[GraphicComponent.mapper]

        if (graphic.sprite.texture == null) {
            log.error { "Entity is missing texture. entity=$entity" }
            throw GdxRuntimeException("Entity is missing texture. entity=$entity")
        }

        graphic.sprite.run {
            rotation = transform.rotationDeg
            setBounds(transform.interpolatedPosition.x, transform.interpolatedPosition.y, transform.size.x,
                transform.size.y)
            draw(batch)
        }
    }

    companion object {
        val log = logger<RenderSystem>()
    }
}