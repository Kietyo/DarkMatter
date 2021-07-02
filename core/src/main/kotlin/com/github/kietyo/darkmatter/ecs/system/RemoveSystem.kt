package com.github.kietyo.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.github.kietyo.darkmatter.ecs.component.RemoveComponent
import com.github.kietyo.darkmatter.extensions.get
import ktx.ashley.allOf

class RemoveSystem : IteratingSystem(allOf(RemoveComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val remove = entity[RemoveComponent.mapper]
        remove.delay -= deltaTime
        if (remove.delay <= 0f) {
            engine.removeEntity(entity)
        }
    }
}