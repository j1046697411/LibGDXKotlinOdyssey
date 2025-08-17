package cn.jzl.ui.style

// 补充UI单位定义
sealed interface UIUnit {

    fun pixel(availableSpace: Float): Float

    data object Auto : UIUnit {
        override fun pixel(availableSpace: Float): Float {
            return 0f
        }
    }

    data class Pixel(val value: Float) : UIUnit {
        override fun pixel(availableSpace: Float): Float {
            return value
        }
    }

    data class Percent(val value: Float) : UIUnit {

        override fun pixel(availableSpace: Float): Float {
            return availableSpace * value / 100f
        }
    }
}