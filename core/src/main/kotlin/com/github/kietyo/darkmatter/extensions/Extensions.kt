package com.github.kietyo.darkmatter.extensions

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array

fun <T : Component> Entity.getNonNull(mapper: ComponentMapper<T>): T =
    mapper.get(this) ?: throw KotlinNullPointerException("Component |${mapper}| is missing from " +
            "entity: $this")

fun <T> Array<T>.getRandom(): T? {
    if (size == 0) return null
    return items[MathUtils.random(0, size - 1)]
}