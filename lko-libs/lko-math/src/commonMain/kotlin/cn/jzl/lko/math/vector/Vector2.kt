package cn.jzl.lko.math.vector

data class Vector2(val x: Float, val y: Float) {

    companion object {
        val Zero: Vector2 = Vector2(0f, 0f)
    }
}

data class Vector3(val x: Float, val y: Float, val z: Float) {

    companion object {
        val Zero: Vector3 = Vector3(0f, 0f, 0f)
    }
}

data class Vector4(val x: Float, val y: Float, val z: Float, val w: Float) {

    companion object {
        val Zero: Vector4 = Vector4(0f, 0f, 0f, 0f)
    }
}
