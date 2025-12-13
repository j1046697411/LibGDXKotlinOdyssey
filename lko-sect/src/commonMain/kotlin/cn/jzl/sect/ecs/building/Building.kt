package cn.jzl.sect.ecs.building


/**
 * 建筑类型组件
 */
@JvmInline
value class BuildingTypeComponent(val type: BuildingType)

/**
 * 建筑效率组件
 */
@JvmInline
value class BuildingEfficiency(val value: Float) {
    companion object {
        val DEFAULT = BuildingEfficiency(1.0f)
    }
}

/**
 * 建筑容量组件
 */
@JvmInline
value class BuildingCapacity(val value: Int) {
    companion object {
        val DEFAULT = BuildingCapacity(100)
    }
}

