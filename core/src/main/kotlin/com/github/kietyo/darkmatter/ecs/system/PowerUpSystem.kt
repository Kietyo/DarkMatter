package com.github.kietyo.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.github.kietyo.darkmatter.V_WIDTH
import com.github.kietyo.darkmatter.ecs.component.*
import com.github.kietyo.darkmatter.extensions.getNonNull
import com.github.kietyo.darkmatter.extensions.getRandom
import ktx.ashley.*
import ktx.collections.*
import ktx.log.debug
import ktx.log.error
import ktx.log.logger
import kotlin.math.min

private const val MAX_SPAWN_INTERVAL = 1.5f
private const val MIN_SPAWN_INTERVAL = 3f
private const val POWER_UP_SPEED = -8.75f
private const val BOOST_1_SPEED_GAIN = 3f
private const val BOOST_2_SPEED_GAIN = 3.75f
private const val LIFE_GAIN = 25f
private const val SHIELD_GAIN = 25f

private class SpawnPattern(
    type1: PowerUpType = PowerUpType.NONE,
    type2: PowerUpType = PowerUpType.NONE,
    type3: PowerUpType = PowerUpType.NONE,
    type4: PowerUpType = PowerUpType.NONE,
    type5: PowerUpType = PowerUpType.NONE,
    val types: GdxArray<PowerUpType> = gdxArrayOf(type1, type2, type3, type4, type5)
)

class PowerUpSystem : IteratingSystem(
    allOf(PowerUpComponent::class, TransformComponent::class)
        .exclude(RemoveComponent::class).get()
) {
    private val playerBoundingRectangle = Rectangle()
    private val powerUpBoundingRectangle = Rectangle()
    private val playerEntities by lazy {
        engine.getEntitiesFor(allOf(PlayerComponent::class).exclude(RemoveComponent::class).get())
    }

    private var spawnTime = 0f
    private val spawnPatterns = gdxArrayOf(
        SpawnPattern(
            type1 = PowerUpType.SPEED_1,
            type2 = PowerUpType.SPEED_2,
            type5 = PowerUpType.LIFE,
        ),
        SpawnPattern(
            type2 = PowerUpType.LIFE,
            type3 = PowerUpType.SHIELD,
            type4 = PowerUpType.SPEED_2,
        )
    )

    private val currentSpawnPattern = GdxArray<PowerUpType>()

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        spawnTime -= deltaTime
        if (spawnTime <= 0f) {
            spawnTime = MathUtils.random(MIN_SPAWN_INTERVAL, MAX_SPAWN_INTERVAL)
            if (currentSpawnPattern.isEmpty) {
                currentSpawnPattern.addAll(spawnPatterns.getRandom()!!.types)
                logger.debug { "Next pattern: $currentSpawnPattern" }
            }

            val powerUpType = currentSpawnPattern.removeIndex(0)
            if (powerUpType == PowerUpType.NONE) {
                return
            }

            spawnPowerUp(powerUpType, 1f * MathUtils.random(0, V_WIDTH - 1), 16f)
        }
    }

    private fun spawnPowerUp(powerUpType: PowerUpType, x: Float, y: Float) {
        engine.entity {
            with<TransformComponent> { setInitialPosition(x, y, 0f) }
            with<PowerUpComponent> { type = powerUpType}
            with<AnimationComponent> { type = powerUpType.animationType}
            with<GraphicComponent>()
            with<MoveComponent> {
                speed.y = POWER_UP_SPEED
            }
        }
    }

    override fun processEntity(powerUpEntity: Entity, deltaTime: Float) {
        val transform = powerUpEntity.getNonNull(TransformComponent.mapper)
        if (transform.position.y <= 1f) {
            powerUpEntity.addComponent<RemoveComponent>(engine)
        }

        powerUpBoundingRectangle.set(
            transform.position.x,
            transform.position.y,
            transform.size.x,
            transform.size.y,
        )

        playerEntities.forEach { player ->
            player.getNonNull(TransformComponent.mapper).let { playerTransform ->
                playerBoundingRectangle.set(
                    playerTransform.position.x,
                    playerTransform.position.y,
                    playerTransform.size.x,
                    playerTransform.size.y,
                )

                if (playerBoundingRectangle.overlaps(powerUpBoundingRectangle)) {
                    collectPowerUp(player, powerUpEntity)
                }
            }
        }
    }

    private fun collectPowerUp(player: Entity, powerUpEntity: Entity) {
        val powerUp = powerUpEntity.getNonNull(PowerUpComponent.mapper)
        logger.debug { "Picking up power up of type: ${powerUp.type}"}

        when (powerUp.type) {
            PowerUpType.SPEED_1 -> {
                player.getNonNull(MoveComponent.mapper).apply {
                    speed.y += BOOST_1_SPEED_GAIN
                }
            }
            PowerUpType.SPEED_2 -> {
                player.getNonNull(MoveComponent.mapper).apply {
                    speed.y += BOOST_2_SPEED_GAIN
                }
            }
            PowerUpType.LIFE -> {
                player.getNonNull(PlayerComponent.mapper).apply {
                    life = min(maxLife, life + LIFE_GAIN)
                }
            }
            PowerUpType.SHIELD -> player.getNonNull(PlayerComponent.mapper).apply {
                shield = min(maxShield, shield + SHIELD_GAIN)
            }
            else -> {
                logger.error { "Unsupported power up type: ${powerUp.type}"}
            }
        }

        powerUpEntity.addComponent<RemoveComponent>(engine)
    }

    companion object {
        val logger = logger<PowerUpSystem>()
    }
}