package cn.jzl.graph.render

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils.FrameBuffer

class TextureFrameBuffer(
    width: Int,
    height: Int,
    format: Pixmap.Format
) : FrameBuffer(format, width, height, true, false)