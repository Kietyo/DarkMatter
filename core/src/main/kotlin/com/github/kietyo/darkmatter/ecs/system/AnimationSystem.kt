package com.github.kietyo.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.GdxRuntimeException
import com.github.kietyo.darkmatter.ecs.component.Animation2D
import com.github.kietyo.darkmatter.ecs.component.AnimationComponent
import com.github.kietyo.darkmatter.ecs.component.AnimationType
import com.github.kietyo.darkmatter.ecs.component.GraphicComponent
import com.github.kietyo.darkmatter.extensions.getNonNull
import ktx.ashley.allOf
import ktx.log.error
import ktx.log.info
import ktx.log.logger
import java.util.*

class AnimationSystem(private val atlas: TextureAtlas): IteratingSystem(allOf(AnimationComponent::class,
    GraphicComponent::class)
    .get()), EntityListener {
    private val animationCache = EnumMap<AnimationType, Animation2D>(AnimationType::class.java)

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(family, this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun entityAdded(entity: Entity) {
        entity.getNonNull(AnimationComponent.mapper).let {
            it.animation = getAnimation(it.type)
            val frame = it.animation.getKeyFrame(it.stateTime)
            entity.getNonNull(GraphicComponent.mapper).setSpriteRegion(frame)
        }
    }

    override fun entityRemoved(entity: Entity?) = Unit

    private fun getAnimation(type: AnimationType): Animation2D {
        val animation = animationCache.computeIfAbsent(type) {
            var regions = atlas.findRegions(type.atlasKey)
            if (regions.isEmpty) {
                logger.error { "No region found for ${type.atlasKey}" }
                regions = atlas.findRegions("error")
                if (regions.isEmpty) throw GdxRuntimeException("No error region found in atlas")
            } else {
                logger.info { "Adding animation of type $type with ${regions.size} regions." }
            }
            Animation2D(type, regions, type.playMode, type.speedRate)
        }
        return animation
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val animationComponent = entity.getNonNull(AnimationComponent.mapper)
        val graphicComponent = entity.getNonNull(GraphicComponent.mapper)

        if (animationComponent.type == AnimationType.NONE) {
            logger.error { "No type specified for animation component $animationComponent for " +
                    "entity: $entity" }
            return
        }

        if (animationComponent.type == animationComponent.animation.type) {
            animationComponent.stateTime += deltaTime
        } else {
            animationComponent.stateTime = 0f
            animationComponent.animation = getAnimation(animationComponent.type)
        }

        val frame = animationComponent.animation.getKeyFrame(animationComponent.stateTime)
        graphicComponent.setSpriteRegion(frame)
    }

    companion object {
        private val logger = logger<AnimationSystem>()
    }
}