package com.github.kietyo.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.github.kietyo.darkmatter.ecs.component.PlayerComponent
import com.github.kietyo.darkmatter.ecs.component.RemoveComponent
import com.github.kietyo.darkmatter.ecs.component.TransformComponent
import com.github.kietyo.darkmatter.event.GameEvent
import com.github.kietyo.darkmatter.event.GameEventManager
import com.github.kietyo.darkmatter.extensions.getNonNull
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import kotlin.math.max

const val DAMAGE_AREA_HEIGHT = 2f
private const val DAMAGE_PER_SECOND = 25f
private const val DEATH_EXPLOSION_DURATION = 0.9f

class DamageSystem(private val gameEventManager: GameEventManager) : IteratingSystem(
    allOf(
        PlayerComponent::class,
        TransformComponent::class
    )
        .exclude(RemoveComponent::class).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.getNonNull(TransformComponent.mapper)
        val player = entity.getNonNull(PlayerComponent.mapper)
        if (transform.position.y <= DAMAGE_AREA_HEIGHT) {
            var damage = DAMAGE_PER_SECOND * deltaTime
            if (player.shield > 0f) {
                val blockAmount = player.shield
                player.shield = max(0f, blockAmount - damage)
                damage -= blockAmount

                if (damage <= 0f) {
                    // Entire damage was blocked by shield.
                    return
                }
            }

            player.life -= damage
            if (player.life <= 0f) {
                gameEventManager.dispatchEvent(GameEvent.PlayerDeath.apply {
                    this.distance = player.distance
                })
                entity.addComponent<RemoveComponent>(engine) {
                    delay = DEATH_EXPLOSION_DURATION
                }
            }
        }
    }
}