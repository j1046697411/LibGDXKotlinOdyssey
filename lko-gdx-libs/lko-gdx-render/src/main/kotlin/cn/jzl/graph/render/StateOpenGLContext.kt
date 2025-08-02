package cn.jzl.graph.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GLTexture
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor

class StateOpenGLContext : OpenGLContext {
    private val textureBinder by lazy { DefaultTextureBinder(DefaultTextureBinder.LRU, 1) }
    private var blending = false
    private var blendSFactor = 0
    private var blendDFactor = 0
    private var blendSFactorAlpha = 0
    private var blendDFactorAlpha = 0
    private var depthFunc = 0
    private var depthRangeNear = 0f
    private var depthRangeFar = 0f
    private var depthMask = false
    private var cullFace = 0

    override fun begin() {
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST)
        depthFunc = 0
        Gdx.gl.glDepthMask(true)
        depthMask = true
        Gdx.gl.glDisable(GL20.GL_BLEND)
        blending = false
        Gdx.gl.glDisable(GL20.GL_CULL_FACE)
        blendDFactorAlpha = 0
        blendSFactorAlpha = 0
        blendDFactor = 0
        blendSFactor = 0
        cullFace = 0
        textureBinder.begin()
    }

    override fun end() {
        if (depthFunc != 0) Gdx.gl.glDisable(GL20.GL_DEPTH_TEST)
        if (!depthMask) Gdx.gl.glDepthMask(true)
        if (blending) Gdx.gl.glDisable(GL20.GL_BLEND)
        if (cullFace > 0) Gdx.gl.glDisable(GL20.GL_CULL_FACE)
        textureBinder.end()
    }

    override fun setDepthMask(depthMask: Boolean) {
        if (this.depthMask != depthMask) Gdx.gl.glDepthMask(depthMask.also { this.depthMask = it })
    }

    override fun setDepthTest(depthFunction: Int) {
        setDepthTest(depthFunction, 0f, 1f)
    }

    override fun setDepthTest(depthFunction: Int, depthRangeNear: Float, depthRangeFar: Float) {
        val wasEnabled = depthFunc != 0
        val enabled = depthFunction != 0
        if (depthFunc != depthFunction) {
            depthFunc = depthFunction
            if (enabled) {
                Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
                Gdx.gl.glDepthFunc(depthFunction)
            } else {
                Gdx.gl.glDisable(GL20.GL_DEPTH_TEST)
            }
        }
        if (enabled) {
            if (!wasEnabled || depthFunc != depthFunction) Gdx.gl.glDepthFunc(depthFunction.also { depthFunc = it })
            if (!wasEnabled || this.depthRangeNear != depthRangeNear || this.depthRangeFar != depthRangeFar) Gdx.gl.glDepthRangef(
                depthRangeNear.also { this.depthRangeNear = it },
                depthRangeFar.also { this.depthRangeFar = it })
        }
    }

    override fun setBlending(enabled: Boolean, sFactor: Int, dFactor: Int) {
        setBlendingSeparate(enabled, sFactor, dFactor, sFactor, dFactor)
    }

    override fun setBlendingSeparate(
        enabled: Boolean,
        sFactor: Int,
        dFactor: Int,
        sFactorAlpha: Int,
        dFactorAlpha: Int
    ) {
        if (enabled != blending) {
            blending = enabled
            if (enabled) Gdx.gl.glEnable(GL20.GL_BLEND)
            else Gdx.gl.glDisable(GL20.GL_BLEND)
        }
        if (enabled && (blendSFactor != sFactor || blendDFactor != dFactor || blendSFactorAlpha != sFactorAlpha || blendDFactorAlpha != dFactorAlpha)) {
            if (sFactor == sFactorAlpha && dFactor == dFactorAlpha) Gdx.gl.glBlendFunc(sFactor, dFactor)
            else Gdx.gl.glBlendFuncSeparate(sFactor, dFactor, sFactorAlpha, dFactorAlpha)
            blendSFactor = sFactor
            blendDFactor = dFactor
            blendSFactorAlpha = sFactorAlpha
            blendDFactorAlpha = dFactorAlpha
        }
    }

    override fun setCullFace(face: Int) {
        if (face != cullFace) {
            cullFace = face
            if ((face == GL20.GL_FRONT) || (face == GL20.GL_BACK) || (face == GL20.GL_FRONT_AND_BACK)) {
                Gdx.gl.glEnable(GL20.GL_CULL_FACE)
                Gdx.gl.glCullFace(face)
            } else Gdx.gl.glDisable(GL20.GL_CULL_FACE)
        }
    }

    override fun bindTexture(textureDescriptor: TextureDescriptor<*>): Int {
        return textureBinder.bind(textureDescriptor)
    }

    override fun bindTexture(texture: GLTexture): Int {
        return textureBinder.bind(texture)
    }
}