package cn.jzl.ecs.component

import kotlin.reflect.KClassifier

interface ComponentProvider {
    fun getOrRegisterComponentIdForClass(classifier: KClassifier): ComponentId
}