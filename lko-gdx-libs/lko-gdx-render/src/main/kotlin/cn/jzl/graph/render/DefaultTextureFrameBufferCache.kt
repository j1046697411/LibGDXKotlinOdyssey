package cn.jzl.graph.render

import cn.jzl.ecs.Prioritized
import cn.jzl.ecs.Updatable
import com.badlogic.gdx.graphics.Pixmap
import ktx.collections.isNotEmpty
import kotlin.time.Duration

internal class DefaultTextureFrameBufferCache : TextureFrameBufferCache, Updatable, Prioritized {
    override val priority: Int = Int.MAX_VALUE

    private val newTextureFrameBuffers = ktx.collections.GdxArray<TextureFrameBuffer>(false, 16)
    private val oldTextureFrameBuffers = ktx.collections.GdxArray<TextureFrameBuffer>(false, 16)

    override fun update(deltaTime: Duration) {
        if (oldTextureFrameBuffers.isNotEmpty()) {
            oldTextureFrameBuffers.forEach { it.dispose() }
            oldTextureFrameBuffers.clear()
        }
        if (newTextureFrameBuffers.isNotEmpty()) {
            oldTextureFrameBuffers.addAll(newTextureFrameBuffers)
            newTextureFrameBuffers.clear()
        }
    }

    override fun obtainFrameBuffer(width: Int, height: Int, format: Pixmap.Format): TextureFrameBuffer {
        if (newTextureFrameBuffers.isNotEmpty()) {
            val index = newTextureFrameBuffers.indexOfFirst { it.width == width && it.height == height }
            if (index != -1) return newTextureFrameBuffers.removeIndex(index)
        }
        if (oldTextureFrameBuffers.isNotEmpty()) {
            val index = oldTextureFrameBuffers.indexOfFirst { it.width == width && it.height == height }
            if (index != -1) return oldTextureFrameBuffers.removeIndex(index)
        }
        return TextureFrameBuffer(width, height, format)
    }

    override fun freeFrameBuffer(frameBuffer: TextureFrameBuffer) {
        newTextureFrameBuffers.add(frameBuffer)
    }
}