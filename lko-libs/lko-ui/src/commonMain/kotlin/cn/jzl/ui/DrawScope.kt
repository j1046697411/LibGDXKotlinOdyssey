package cn.jzl.ui

import cn.jzl.ecs.EntityComponentContext
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

interface DrawScope {
    fun EntityComponentContext.draw(shape: ShapeRenderer)
}