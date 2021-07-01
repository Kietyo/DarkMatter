package com.github.kietyo.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.kietyo.darkmatter.ecs.component.FacingComponent
import com.github.kietyo.darkmatter.ecs.component.FacingDirection
import com.github.kietyo.darkmatter.ecs.component.GraphicComponent
import com.github.kietyo.darkmatter.ecs.component.PlayerComponent
import com.github.kietyo.darkmatter.extensions.get
import ktx.ashley.allOf

class PlayerAnimationSystem(
    private val defaultRegion: TextureRegion,
    private val leftRegion: TextureRegion,
    private val rightRegion: TextureRegion,
) : IteratingSystem(
    allOf(PlayerComponent::class, FacingComponent::class, GraphicComponent::class).get
        ()
), EntityListener{
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val facing = entity[FacingComponent.mapper]
        val graphic = entity[GraphicComponent.mapper]
        if (facing.direction == facing.lastDirection && graphic.sprite.texture != null) {
            return
        }

        val region = when (facing.direction) {
            FacingDirection.LEFT -> leftRegion
            FacingDirection.RIGHT -> rightRegion
            else -> defaultRegion
        }
        graphic.setSpriteRegion(region)
    }

    override fun entityAdded(entity: Entity) {
        entity[GraphicComponent.mapper].setSpriteRegion(defaultRegion)
    }

    override fun entityRemoved(entity: Entity?) {
    }

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }
}