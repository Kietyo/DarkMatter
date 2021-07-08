package com.github.kietyo.darkmatter.event

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.ObjectMap
import com.github.kietyo.darkmatter.ecs.component.PowerUpType
import ktx.collections.*
import java.util.*
import kotlin.reflect.KClass

sealed class GameEvent {
    object PlayerDeath : GameEvent() {
        var distance = 0f
        override fun toString() = "PlayerDeath(distance=$distance)"
    }

    object CollectPowerUp : GameEvent() {
        lateinit var player: Entity
        var type = PowerUpType.NONE

        override fun toString() = "CollectPowerUp(player=$player, type=$type)"
    }
}

interface GameEventListener {
    fun onEvent(gameEvent: GameEvent)
}

class GameEventManager {
    val listeners =
        ObjectMap<KClass<out GameEvent>, GdxSet<GameEventListener>>()

    inline fun <reified T : GameEvent> addListeners(listener: GameEventListener) {
        listeners.getOrPut(T::class) {GdxSet()}.add(listener)
    }

    inline fun <reified T : GameEvent> removeListeners(listener: GameEventListener) {
        listeners.getOrPut(T::class) {GdxSet()}.remove(listener)
    }

    fun removeListener(listener: GameEventListener) {
        listeners.values().forEach { it.remove(listener) }
    }

    fun dispatchEvent(gameEvent: GameEvent) {
        listeners[gameEvent::class]?.forEach {
            it.onEvent(gameEvent)
        }
    }
}