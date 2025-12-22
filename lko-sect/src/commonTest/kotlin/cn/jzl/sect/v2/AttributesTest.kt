package cn.jzl.sect.v2

import cn.jzl.di.instance
import cn.jzl.ecs.entity
import cn.jzl.ecs.world
import cn.jzl.sect.ecs.AttributeProvider
import cn.jzl.sect.ecs.AttributeService
import cn.jzl.sect.ecs.AttributeValue
import cn.jzl.sect.ecs.Named
import cn.jzl.sect.ecs.attributeAddon
import cn.jzl.sect.ecs.attributes
import kotlin.test.Test
import kotlin.test.assertEquals

class AttributesTest {

    @Test
    fun attributeService_createsAndCachesAttributesByName() {
        val world = world {
            install(attributeAddon)
        }

        val attributeService by world.di.instance<AttributeService>()

        val a1 = attributeService.attribute(Named("attack"))
        val a2 = attributeService.attribute(Named("attack"))

        assertEquals(a1, a2)
        assertEquals(listOf(Named("attack")), attributeService.attributeNames.toList())
    }

    @Test
    fun totalAttributeValue_includesProviders() {
        val world = world {
            attributes {
                provider {
                    AttributeProvider { _, _, _ -> AttributeValue(5) }
                }
            }
        }

        val attributeService by world.di.instance<AttributeService>()

        val owner = world.entity { }
        val attr = attributeService.attribute(Named("power"))

        // no explicit base value set => default zero + provider(5)
        assertEquals(5, attributeService.getTotalAttributeValue(owner, attr).value)
    }
}
