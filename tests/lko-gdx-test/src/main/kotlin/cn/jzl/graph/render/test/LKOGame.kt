package cn.jzl.graph.render.test

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.kotcrab.vis.ui.VisUI
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin
import ktx.style.set

class LKOGame : KtxGame<KtxScreen>() {
    override fun create() {
        super.create()
        Gdx.app.logLevel = Application.LOG_DEBUG
        val atlas = TextureAtlas(Gdx.files.internal("uiskin.atlas"))
        VisUI.load(VisUI.SkinScale.X1)
        val skin = VisUI.getSkin()
        atlas.regions.forEach { atlasRegion ->
            val name = atlasRegion.name
            if (name.startsWith("connector-")) {
                val sprite = Sprite(atlasRegion)
                sprite.setColor(Color(1f, 0.3f, 0.3f, 1f))
                skin.add("$name-invalid", SpriteDrawable(sprite), Drawable::class.java)
            }
            if (name == "graph-rounded-background") {
                val ninePatch = NinePatch(atlasRegion, 7, 7, 7, 7)
                ninePatch.color = Color(0.25f, 0.3f, 0.25f, 0.7f)
                skin.add(name, NinePatchDrawable(ninePatch), Drawable::class.java)
            } else {
                skin.add(name, SpriteDrawable(Sprite(atlasRegion)), Drawable::class.java)
            }
        }
        Scene2DSkin.defaultSkin = skin
        addScreen(RenderScreen())
        addScreen(UIScreen())
        setScreen<UIScreen>()
    }
}