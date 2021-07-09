package com.github.kietyo.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool
import com.github.kietyo.darkmatter.event.GameEvent
import com.github.kietyo.darkmatter.event.GameEventListener
import com.github.kietyo.darkmatter.event.GameEventManager
import ktx.collections.*

private class CameraShake : Pool.Poolable {
    var maxDistortion = 0f
    var duration = 0f
    private var currentDuration = 0f

    lateinit var camera: Camera
    private var storeCameraPos = true
    private var originalCameraPos = Vector3()

    /**
     * Returns true if the current shake has finished.
     */
    fun update(deltaTime: Float): Boolean {
        if (storeCameraPos) {
            storeCameraPos = false
            originalCameraPos.set(camera.position)
        }

        if (currentDuration < duration) {
            val currentPower = maxDistortion * ((duration - currentDuration) / duration)

            camera.position.x = originalCameraPos.x + MathUtils.random(-1f, 1f) * maxDistortion
            camera.position.y = originalCameraPos.y + MathUtils.random(-1f, 1f) * maxDistortion
            camera.update()

            currentDuration += deltaTime
            return false
        }

        camera.position.set(originalCameraPos)
        camera.update()
        return true
    }

    override fun reset() {
        maxDistortion = 0f
        duration = 0f
        currentDuration = 0f

        storeCameraPos = true
        originalCameraPos = Vector3.Zero
    }
}

private class CameraShakePool(private val gameCamera: Camera) : Pool<CameraShake>() {
    override fun newObject() = CameraShake().apply {
        this.camera = gameCamera
    }
}

class CameraShakeSystem(camera: Camera, private val gameEventManager: GameEventManager) :
    EntitySystem(), GameEventListener {
    private val cameraShakePool = CameraShakePool(camera)
    private val activeShakes = GdxArray<CameraShake>()
    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        gameEventManager.addListeners<GameEvent.PlayerHit>(this)
    }

    override fun removedFromEngine(engine: Engine?) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(this)
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        if (!activeShakes.isEmpty) {
            val shake = activeShakes.first()
            if (shake.update(deltaTime)) {
                activeShakes.removeIndex(0)
                cameraShakePool.free(shake)
            }
        }
    }

    override fun onEvent(gameEvent: GameEvent) {
        if (activeShakes.size < 4) {
            activeShakes.add(cameraShakePool.obtain().apply {
                duration = 0.25f
                maxDistortion = 0.25f
            })
        }
    }
}