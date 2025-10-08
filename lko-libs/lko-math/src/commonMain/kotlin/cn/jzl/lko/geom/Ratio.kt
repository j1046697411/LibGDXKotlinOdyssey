package cn.jzl.lko.geom

@JvmInline
value class Ratio(val value: Float) {
    companion object {
        val ZERO = Ratio(0f)
        val QUARTER = Ratio(0.25f)
        val HALF = Ratio(0.5f)
        val THREE_QUARTER = Ratio(0.75f)
        val ONE = Ratio(1f)
        val NAN = Ratio(Float.NaN)
    }
}