package cn.jzl.ecs.v2

import cn.jzl.di.singleton

abstract class ECSBasicTest {
    fun createWorld(): World = world {
        this bind singleton { 10 }
    }
}