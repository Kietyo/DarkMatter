package com.github.kietyo.darkmatter.ecs.system

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.kietyo.darkmatter.ecs.component.GraphicComponent
import com.github.kietyo.darkmatter.ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.graphics.use
import ktx.log.error
import ktx.log.logger

operator fun <T : Component> Entity.get(mapper: ComponentMapper<T>): T =
    mapper.get(this) ?: throw KotlinNullPointerException("Component |${mapper}| is missing from " +
            "entity: $this")

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
        }

        graphic.sprite.run {
            rotation = transform.rotationDeg
            setBounds(transform.position.x, transform.position.y, transform.size.x,
                transform.size.y)
            draw(batch)
        }
    }

    companion object {
        val log = logger<RenderSystem>()
    }
}