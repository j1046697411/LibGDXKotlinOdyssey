package cn.jzl.ecs

import kotlin.reflect.KClassifier

interface ComponentProvider {

    val world: World

    fun getOrRegisterEntityForClass(classifier: KClassifier): Entity

    fun holdsData(relation: Relation): Boolean
}