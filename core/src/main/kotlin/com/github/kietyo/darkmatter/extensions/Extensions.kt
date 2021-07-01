package com.github.kietyo.darkmatter.extensions

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity

operator fun <T : Component> Entity.get(mapper: ComponentMapper<T>): T =
    mapper.get(this) ?: throw KotlinNullPointerException("Component |${mapper}| is missing from " +
            "entity: $this")