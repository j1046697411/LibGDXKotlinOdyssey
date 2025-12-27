package cn.jzl.sect.ecs.cultivation

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.sect.ecs.attribute.AttributeService
import cn.jzl.sect.ecs.attribute.AttributeValue
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.attribute.SectAttributes
import cn.jzl.sect.ecs.attribute.attributeAddon
import cn.jzl.sect.ecs.core.coreAddon
import kotlin.math.min

@JvmInline
value class Cultivation(val level: Int)

sealed class Cultivable
sealed class Breakthrough

val cultivationAddon = createAddon("cultivation") {
    install(coreAddon)
    install(attributeAddon)
    injects { this bind singleton { new(::CultivationService) } }
    components {
        world.componentId<Cultivation>()
        world.componentId<Breakthrough> { it.tag() }
        world.componentId<Cultivable> {
            it.tag()
            it.singleRelation()
        }
    }
}

class CultivationService(world: World) : EntityRelationContext(world) {

    private val attributes by world.di.instance<SectAttributes>()
    private val attributeService by world.di.instance<AttributeService>()

    // 凡人
    val mortal: Entity = realm(Named("mortal"), 0) {
        lifespan(0)
        cultivation(100)
    }

    // 练气
    val qiRefining: List<Entity> = listOf(
        realm(Named("qi_refining_1"), 10) {
            lifespan(5)
            cultivation(1000)
        },
        realm(Named("qi_refining_2"), 11) {
            lifespan(10)
            cultivation(2000)
        },
        realm(Named("qi_refining_3"), 12) {
            lifespan(15)
            cultivation(3000)
        },
        realm(Named("qi_refining_4"), 13) {
            lifespan(20)
            cultivation(4000)
        },
        realm(Named("qi_refining_5"), 14) {
            lifespan(25)
            cultivation(5000)
        },
        realm(Named("qi_refining_6"), 15) {
            lifespan(30)
            cultivation(6000)
        },
        realm(Named("qi_refining_7"), 16) {
            lifespan(35)
            cultivation(7000)
        },
        realm(Named("qi_refining_8"), 17) {
            lifespan(42)
            cultivation(8000)
        },
        realm(Named("qi_refining_9"), 18) {
            lifespan(50)
            cultivation(9000)
        },
    )

    // 筑基
    val foundationEstablishment: List<Entity> = listOf(
        realm(Named("foundation_establishment_1"), 20, true) {
            lifespan(100)
            cultivation(9000)
        },
        realm(Named("foundation_establishment_2"), 21) {
            lifespan(120)
            cultivation(9000)
        },
        realm(Named("foundation_establishment_3"), 22) {
            lifespan(150)
            cultivation(9000)
        },
        realm(Named("foundation_establishment_4"), 23) {
            lifespan(200)
            cultivation(9000)
        },
    )

    // 金丹
    val goldenCore: List<Entity> = listOf(
        realm(Named("golden_core_1"), 30, true) {
            lifespan(400)
            cultivation(9000)
        },
        realm(Named("golden_core_2"), 31) {
            lifespan(420)
            cultivation(9000)
        },
        realm(Named("golden_core_3"), 32) {
            lifespan(460)
            cultivation(9000)
        },
        realm(Named("golden_core_4"), 33) {
            lifespan(520)
            cultivation(9000)
        },
        realm(Named("golden_core_5"), 34) {
            lifespan(600)
            cultivation(9000)
        },
        realm(Named("golden_core_6"), 35) {
            lifespan(700)
            cultivation(9000)
        },
    )

    // 元婴
    val nascentSoul: List<Entity> = listOf(
        realm(Named("nascent_soul_1"), 40, true) {
            lifespan(1500)
            cultivation(9000)
        },
        realm(Named("nascent_soul_2"), 41) {
            lifespan(1700)
            cultivation(9000)
        },
        realm(Named("nascent_soul_3"), 42) {
            lifespan(2100)
            cultivation(9000)
        },
        realm(Named("nascent_soul_4"), 43) {
            lifespan(2500)
            cultivation(9000)
        },
        realm(Named("nascent_soul_5"), 44) {
            lifespan(3000)
            cultivation(9000)
        },
    )

    // 化神
    val spiritTransformation: List<Entity> = listOf(
        realm(Named("spirit_transformation_1"), 50, true) {
            lifespan(5000)
            cultivation(9000)
        },
        realm(Named("spirit_transformation_2"), 51) {
            lifespan(6000)
            cultivation(9000)
        },
        realm(Named("spirit_transformation_3"), 52) {
            lifespan(7200)
            cultivation(9000)
        },
        realm(Named("spirit_transformation_4"), 53) {
            lifespan(8500)
            cultivation(9000)
        },
        realm(Named("spirit_transformation_5"), 54) {
            lifespan(10000)
            cultivation(9000)
        },
    )

    val allRealms: List<Entity> = buildList {
        add(mortal)
        addAll(qiRefining)
        addAll(foundationEstablishment)
        addAll(goldenCore)
        addAll(nascentSoul)
        addAll(spiritTransformation)
    }

    private fun realm(named: Named, level: Int, breakthrough: Boolean = false, block: RealmContext.() -> Unit): Entity {
        return world.entity {
            val realmContext = object : RealmContext {
                override fun lifespan(lifespan: Long) {
                    attributeService.setAttributeValue(this@entity, it, attributes.lifespan, AttributeValue(lifespan))
                }

                override fun cultivation(cultivation: Long) {
                    attributeService.setAttributeValue(this@entity, it, attributes.maxCultivation, AttributeValue(cultivation))
                }
            }
            it.addComponent(named)
            it.addComponent(Cultivation(level))
            if (breakthrough) it.addTag<Breakthrough>()
            realmContext.block()
        }
    }

    fun cultivable(context: EntityCreateContext, entity: Entity, level: Int = -1) = context.run {
        val realm = if (level != -1) {
            allRealms.first { it.getComponent<Cultivation>().level == level }
        } else {
            mortal
        }
        entity.addRelation<Cultivable>(realm)
        attributeService.setAttributeValue(this, entity, attributes.cultivation, AttributeValue(0))
    }

    fun cultivation(entity: Entity, cultivation: Long) {
        require(cultivation > 0) { "Cultivation must be non-negative" }
        val currentRealm = entity.getRelationUp<Cultivation>()
        requireNotNull(currentRealm) { "Entity is not cultivable" }
        val currentCultivation = attributeService.getAttributeValue(entity, attributes.cultivation)?.value ?: 0
        val remainingCultivation = currentCultivation + cultivation
        var realmIndex = allRealms.indexOf(currentRealm)
        while (realmIndex < allRealms.size) {
            val nextRealm = allRealms.getOrNull(realmIndex) ?: break
            if (nextRealm.hasTag<Breakthrough>()) break
            val maxCultivation = attributeService.getAttributeValue(nextRealm, attributes.maxCultivation)?.value ?: 0
            if (remainingCultivation < maxCultivation) {
                break
            }
            realmIndex++
        }
        val targetRealm = allRealms.getOrNull(realmIndex)
        if (targetRealm != null && targetRealm != currentRealm) {
            val maxCultivation = attributeService.getAttributeValue(targetRealm, attributes.maxCultivation)?.value ?: 0
            world.entity(entity) {
                it.addRelation<Cultivable>(targetRealm)
                attributeService.setAttributeValue(
                    this,
                    entity,
                    attributes.cultivation,
                    AttributeValue(min(maxCultivation, remainingCultivation))
                )
            }
        } else {
            val maxCultivation = attributeService.getAttributeValue(currentRealm, attributes.maxCultivation)?.value ?: 0
            world.entity(entity) {
                attributeService.setAttributeValue(
                    this,
                    entity,
                    attributes.cultivation,
                    AttributeValue(min(maxCultivation, remainingCultivation))
                )
            }
        }
    }

    fun breakthrough(entity: Entity) {
        val currentRealm = entity.getRelationUp<Cultivation>()
        requireNotNull(currentRealm) { "Entity is not cultivable" }
        val maxCultivation = attributeService.getAttributeValue(currentRealm, attributes.maxCultivation)?.value ?: 0
        val currentCultivation = attributeService.getAttributeValue(entity, attributes.cultivation)?.value ?: 0
        if (currentCultivation < maxCultivation) return
        val realmIndex = allRealms.indexOf(currentRealm)
        if (realmIndex == -1 || realmIndex + 1 >= allRealms.size) return
        val nextRealm = allRealms[realmIndex + 1]
        world.entity(entity) {
            it.addRelation<Cultivable>(nextRealm)
        }
    }

    interface RealmContext {
        fun lifespan(lifespan: Long)
        fun cultivation(cultivation: Long)
    }
}