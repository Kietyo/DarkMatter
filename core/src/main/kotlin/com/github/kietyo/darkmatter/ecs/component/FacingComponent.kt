package com.github.kietyo.darkmatter.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import kotlin.properties.Delegates

enum class FacingDirection {
    LEFT, DEFAULT, RIGHT
}

class FacingComponent : Component, Pool.Poolable {
    var lastDirection by Delegates.notNull<FacingDirection>()
    var direction by Delegates.notNull<FacingDirection>()
    init {
        reset()
    }
    override fun reset() {
        lastDirection = FacingDirection.DEFAULT
        direction = FacingDirection.DEFAULT
    }

    companion object {
        val mapper = mapperFor<FacingComponent>()
    }
}