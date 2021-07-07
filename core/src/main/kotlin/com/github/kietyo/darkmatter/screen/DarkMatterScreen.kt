package com.github.kietyo.darkmatter.screen

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.kietyo.darkmatter.DarkMatter
import ktx.app.KtxScreen

abstract class DarkMatterScreen(
    val game: DarkMatter, val batch: Batch = game.batch,
    val gameViewport: FitViewport = game.gameViewport,
    val uiViewport: FitViewport = game.uiViewport,
    val engine: Engine = game.engine
) : KtxScreen {
    override fun resize(width: Int, height: Int) {
        gameViewport.update(width, height, true)
        uiViewport.update(width, height, true)
    }
}