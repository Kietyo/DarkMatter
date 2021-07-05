package com.github.kietyo.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.kietyo.darkmatter.ecs.component.FacingComponent
import com.github.kietyo.darkmatter.ecs.component.FacingDirection
import com.github.kietyo.darkmatter.ecs.component.PlayerComponent
import com.github.kietyo.darkmatter.ecs.component.TransformComponent
import com.github.kietyo.darkmatter.extensions.getNonNull
import ktx.ashley.allOf
import ktx.log.logger

private const val TOUCH_TOLERANCE_DISTANCE = 0.1f

class PlayerInputSystem(private val gameViewport: Viewport) : IteratingSystem(
    allOf(
        PlayerComponent::class,
        TransformComponent::class, FacingComponent::class
    ).get()
) {
    private val tmpVec = Vector2()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val facing = entity.getNonNull(FacingComponent.mapper)
        val transform = entity.getNonNull(TransformComponent.mapper)

        tmpVec.x = Gdx.input.x.toFloat()
//        logger.info { "tmpVec before: $tmpVec" }
        gameViewport.unproject(tmpVec)
//        logger.info { "tmpVec after: $tmpVec" }

        facing.lastDirection = facing.direction

//        logger.info { "transform.position: ${transform.position}" }
//        logger.info { "transform.size: ${transform.size}" }

        val diffX = tmpVec.x - transform.position.x - transform.size.x * 0.5
        facing.direction = when  {
            diffX < -TOUCH_TOLERANCE_DISTANCE -> FacingDirection.LEFT
            diffX > TOUCH_TOLERANCE_DISTANCE -> FacingDirection.RIGHT
            else -> FacingDirection.DEFAULT
        }
    }

    companion object {
        val logger = logger<PlayerInputSystem>()
    }
}