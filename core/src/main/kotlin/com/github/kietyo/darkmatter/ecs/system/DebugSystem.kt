package com.github.kietyo.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.github.kietyo.darkmatter.ecs.component.PlayerComponent
import com.github.kietyo.darkmatter.ecs.component.TransformComponent
import com.github.kietyo.darkmatter.extensions.get
import ktx.ashley.allOf
import ktx.ashley.getSystem
import kotlin.math.min

private const val WINDOW_INFO_UPDATE_RATE = 0.25f

class DebugSystem : IntervalIteratingSystem(allOf(PlayerComponent::class).get(), WINDOW_INFO_UPDATE_RATE) {
    init {
        setProcessing(true)
    }

    override fun processEntity(entity: Entity) {
        val player = entity[PlayerComponent.mapper]
        val transform = entity[TransformComponent.mapper]

        when {
            Gdx.input.isKeyPressed(Input.Keys.NUM_1) -> {
                // Kill player
                transform.position.y = 1f
                player.life = 1f
                player.shield = 0f
            }
            Gdx.input.isKeyPressed(Input.Keys.NUM_2) -> {
                player.shield = min(player.maxShield, player.shield + 25f)
            }
            Gdx.input.isKeyPressed(Input.Keys.NUM_3) -> {
                // Disable movement
                engine.getSystem<MoveSystem>().setProcessing(false)
            }
            Gdx.input.isKeyPressed(Input.Keys.NUM_4) -> {
                // Enable movement
                engine.getSystem<MoveSystem>().setProcessing(true)
            }
        }

        Gdx.graphics.setTitle("DM Debug - pos: ${transform.position}, life: ${player.life}, " +
                "shield: ${player.shield}")
    }
}