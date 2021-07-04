package com.github.kietyo.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.github.kietyo.darkmatter.ecs.component.AttachComponent
import com.github.kietyo.darkmatter.ecs.component.GraphicComponent
import com.github.kietyo.darkmatter.ecs.component.RemoveComponent
import com.github.kietyo.darkmatter.ecs.component.TransformComponent
import com.github.kietyo.darkmatter.extensions.get as getNonNull
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.info
import ktx.log.logger

class AttachSystem : IteratingSystem(
    allOf(
        AttachComponent::class, TransformComponent::class,
        GraphicComponent::class
    ).get()
), EntityListener {
    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun entityAdded(entity: Entity) = Unit

    override fun entityRemoved(removedEntity: Entity) {
        entities.forEach {entity ->
            entity.getNonNull(AttachComponent.mapper).let { attach ->
                if (attach.entity == removedEntity) {
                    logger.info { "Adding remove component to entity: $entity"}
                    entity.addComponent<RemoveComponent>(engine)
                }
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
//        logger.info { "Processing entity: $entity" }
        val attach = entity.getNonNull(AttachComponent.mapper)
        val graphic = entity.getNonNull(GraphicComponent.mapper)
        val transform = entity.getNonNull(TransformComponent.mapper)

        // Get the transform of the entity that we're attaching to.
        // Set the transform of the attacher based on the above.
        attach.entity[TransformComponent.mapper]?.let { attachTransform ->
            transform.interpolatedPosition.set(
                attachTransform.interpolatedPosition.x + attach.offset.x,
                attachTransform.interpolatedPosition.y + attach.offset.y,
                transform.position.z
            )
        }

        attach.entity[GraphicComponent.mapper]?.let { attachGraphic ->
            graphic.sprite.setAlpha(attachGraphic.sprite.color.a)
        }
    }

    companion object {
        val logger = logger<AttachSystem>()
    }
}
