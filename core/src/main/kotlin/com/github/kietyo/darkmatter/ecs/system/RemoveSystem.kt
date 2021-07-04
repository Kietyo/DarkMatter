package com.github.kietyo.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.github.kietyo.darkmatter.ecs.component.RemoveComponent
import com.github.kietyo.darkmatter.extensions.get
import ktx.ashley.allOf
import ktx.log.info
import ktx.log.logger

class RemoveSystem : IteratingSystem(allOf(RemoveComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val remove = entity[RemoveComponent.mapper]
        remove.delay -= deltaTime
        if (remove.delay <= 0f) {
            logger.info { "Removing entity: $entity" }
            engine.removeEntity(entity)
        }
    }

    companion object {
        val logger = logger<RemoveSystem>()
    }
}