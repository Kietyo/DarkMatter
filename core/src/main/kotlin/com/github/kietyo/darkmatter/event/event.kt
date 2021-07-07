package com.github.kietyo.darkmatter.event

import com.badlogic.ashley.core.Entity
import com.github.kietyo.darkmatter.ecs.component.PowerUpType
import ktx.collections.*
import java.util.*

enum class GameEventType {
    PLAYER_DEATH,
    COLLECT_POWER_UP
}

interface GameEvent

object GameEventPlayerDeath : GameEvent {
    var distance = 0f
    override fun toString() = "GameEventPlayerDeath(distance=$distance)"
}

object GameEventCollectPowerUp : GameEvent {
    lateinit var player: Entity
    var type = PowerUpType.NONE

    override fun toString() = "GameEventCollectPowerUp(player=$player, type=$type)"
}

interface GameEventListener {
    fun onEvent(type: GameEventType, data: GameEvent? = null)
}

class GameEventManager {
    private val listeners =
        EnumMap<GameEventType, GdxSet<GameEventListener>>(GameEventType::class.java)

    fun addListeners(type: GameEventType, listener: GameEventListener) {
        listeners.computeIfAbsent(type) { GdxSet() }.add(listener)
    }

    fun removeListener(type: GameEventType, listener: GameEventListener) {
        listeners.computeIfAbsent(type) { GdxSet() }.remove(listener)
    }

    fun removeListener(listener: GameEventListener) {
        listeners.values.forEach { it.remove(listener) }
    }

    fun dispatchEvent(type: GameEventType, data: GameEvent? = null) {
        listeners[type]?.forEach {
            it.onEvent(type, data)
        }
    }
}