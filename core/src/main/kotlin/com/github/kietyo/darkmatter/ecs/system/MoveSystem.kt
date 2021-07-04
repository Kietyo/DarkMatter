package com.github.kietyo.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.github.kietyo.darkmatter.V_HEIGHT
import com.github.kietyo.darkmatter.V_WIDTH
import com.github.kietyo.darkmatter.ecs.component.*
import com.github.kietyo.darkmatter.extensions.get
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.log.info
import ktx.log.logger
import kotlin.math.max
import kotlin.math.min

private const val UPDATE_RATE = 1 / 25f
private const val HORIZONTAL_ACCELERATION = 16.5f
private const val VERTICAL_ACCELERATION = 2.25f
private const val MAX_VERTICAL_NEGATIVE_SPEED = 0.75f
private const val MAX_VERTICAL_POSITIVE_SPEED = 5f
private const val MAX_HORIZONTAL_SPEED = 5.5f

class MoveSystem : IteratingSystem(
    allOf(TransformComponent::class, MoveComponent::class).exclude
        (RemoveComponent::class).get()
) {
    private var accumulator = 0f

    override fun update(deltaTime: Float) {
        accumulator += deltaTime
        while (accumulator >= UPDATE_RATE) {
//            logger.info { "deltaTime: $deltaTime, accumulator: $accumulator, UPDATE_RATE: $UPDATE_RATE" }
            accumulator -= UPDATE_RATE
            entities.forEach { entity ->
                entity[TransformComponent.mapper].let {
                    transform -> transform.prevPosition.set(transform.position)
                }
            }
            super.update(UPDATE_RATE)
        }

        val alpha = accumulator / UPDATE_RATE
        entities.forEach { entity ->
            entity[TransformComponent.mapper].let {
                it.interpolatedPosition.set(
                    MathUtils.lerp(it.prevPosition.x, it.position.x, alpha),
                    MathUtils.lerp(it.prevPosition.y, it.position.y, alpha),
                    it.position.z
                )

            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        val move = entity[MoveComponent.mapper]
        val player = entity[PlayerComponent.mapper]
        val facing = entity[FacingComponent.mapper]
        movePlayer(transform, move, player, facing, deltaTime)
        //        moveEntity(transform, move, deltaTime)
    }

    private fun movePlayer(
        transform: TransformComponent,
        move: MoveComponent,
        player: PlayerComponent,
        facing: FacingComponent,
        deltaTime: Float
    ) {
        move.speed.x = when (facing.direction) {
            FacingDirection.LEFT -> min(0f, move.speed.x - HORIZONTAL_ACCELERATION * deltaTime)
            FacingDirection.DEFAULT -> 0f
            FacingDirection.RIGHT -> max(0f, move.speed.x + HORIZONTAL_ACCELERATION * deltaTime)
        }
        move.speed.x = MathUtils.clamp(move.speed.x, -MAX_HORIZONTAL_SPEED, MAX_HORIZONTAL_SPEED)

        move.speed.y = MathUtils.clamp(
            move.speed.y - VERTICAL_ACCELERATION * deltaTime,
            -MAX_VERTICAL_NEGATIVE_SPEED, MAX_VERTICAL_POSITIVE_SPEED
        )

        moveEntity(transform, move, deltaTime)
    }

    private fun moveEntity(transform: TransformComponent, move: MoveComponent, deltaTime: Float) {
        transform.position.x = MathUtils.clamp(
            transform.position.x + move.speed.x * deltaTime,
            0f,
            V_WIDTH - transform.size.x
        )
        transform.position.y = MathUtils.clamp(
            transform.position.y + move.speed.y * deltaTime,
            1f,
            V_HEIGHT + 1f - transform.size.y
        )
    }

    companion object {
        val logger = logger<MoveSystem>()
    }
}