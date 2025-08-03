package cn.jzl.graph.shader.renderer.strategy

enum class Order {
    BackToFront {
        override fun result(distance: Float): Int {
            return when {
                distance > 0 -> 1
                distance < 0 -> -1
                else -> 0
            }
        }
    },
    FrontToBack {
        override fun result(distance: Float): Int {
            return when {
                distance > 0 -> -1
                distance < 0 -> 1
                else -> 0
            }
        }
    };

    internal abstract fun result(distance: Float): Int
}