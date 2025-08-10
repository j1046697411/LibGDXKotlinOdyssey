package cn.jzl.graph.render.test

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

fun main() {
    val configuration = Lwjgl3ApplicationConfiguration()
    configuration.setWindowedMode(1440, 810)
    configuration.setTitle("LKO Test")
    configuration.setIdleFPS(60)
    Lwjgl3Application(LKOGame(), configuration)
}

