package cn.jzl.graph.ui.graph

import cn.jzl.graph.common.config.MenuNodeConfiguration

class EditorNodeConfiguration(
    val closeable: Boolean,
    private val nodeConfiguration: MenuNodeConfiguration
) : MenuNodeConfiguration by nodeConfiguration