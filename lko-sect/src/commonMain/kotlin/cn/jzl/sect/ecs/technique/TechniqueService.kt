package cn.jzl.sect.ecs.technique

import cn.jzl.di.instance
import cn.jzl.ecs.*
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.query
import cn.jzl.sect.ecs.*
import cn.jzl.sect.ecs.core.Description
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.core.OwnedBy

/**
 * 功法服务
 * 提供功法的创建、学习、查询等功能
 */
class TechniqueService(world: World) : EntityRelationContext(world) {

    private val sectService by world.di.instance<SectService>()
    private val attributeService by world.di.instance<AttributeService>()

    /**
     * 创建功法
     */
    @ECSDsl
    fun createTechnique(
        named: Named,
        grade: TechniqueGrade,
        type: TechniqueType,
        requirement: TechniqueRequirement = TechniqueRequirement(),
        effect: TechniqueEffect = TechniqueEffect(),
        description: Description? = null,
        block: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity = world.entity {
        it.addTag<Technique>()
        it.addComponent(named)
        it.addComponent(TechniqueGradeComponent(grade))
        it.addComponent(TechniqueTypeComponent(type))
        it.addComponent(requirement)
        it.addComponent(effect)
        description?.let { desc -> it.addComponent(desc) }
        block(it)
    }

    /**
     * 将功法添加到藏经阁
     */
    fun addToLibrary(library: Entity, technique: Entity): Boolean {
        require(library.hasTag<Library>()) { "实体${library.id}不是藏经阁" }
        require(technique.hasTag<Technique>()) { "实体${technique.id}不是功法" }

        val existing = library.getRelation<LibraryContains?>(technique)
        if (existing != null) return false

        world.entity(library) {
            it.addRelation(technique, LibraryContains(technique, System.currentTimeMillis()))
        }
        return true
    }

    /**
     * 检查弟子是否可以学习功法
     */
    fun canLearn(technique: Entity, disciple: Entity, sect: Entity): TechniqueLearnError? {
        require(technique.hasTag<Technique>()) { "实体${technique.id}不是功法" }
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }

        val memberData = sectService.getMemberData(sect, disciple)
            ?: return TechniqueLearnError.NotSectMember

        val library = getLibraryBySect(sect)
        if (library == null || !isInLibrary(library, technique)) {
            return TechniqueLearnError.NotInLibrary
        }

        val requirement = technique.getComponent<TechniqueRequirement?>() ?: TechniqueRequirement()

        if (memberData.role !in requirement.allowedRoles) {
            return TechniqueLearnError.RoleNotAllowed(memberData.role)
        }

        val discipleLevel = getDiscipleLevel(disciple)
        if (discipleLevel < requirement.minLevel) {
            return TechniqueLearnError.LevelTooLow(requirement.minLevel, discipleLevel)
        }

        if (memberData.contribution.value < requirement.contributionCost) {
            return TechniqueLearnError.InsufficientContribution(
                requirement.contributionCost,
                memberData.contribution.value
            )
        }

        if (hasLearned(disciple, technique)) {
            return TechniqueLearnError.AlreadyLearned
        }

        val missingPrerequisites = requirement.prerequisiteTechniques.filter { prereq ->
            !hasLearned(disciple, prereq)
        }.toSet()
        if (missingPrerequisites.isNotEmpty()) {
            return TechniqueLearnError.MissingPrerequisites(missingPrerequisites)
        }

        return null
    }

    /**
     * 学习功法
     */
    fun learn(technique: Entity, disciple: Entity, sect: Entity): TechniqueLearnError? {
        val error = canLearn(technique, disciple, sect)
        if (error != null) return error

        val requirement = technique.getComponent<TechniqueRequirement?>() ?: TechniqueRequirement()
        val memberData = sectService.getMemberData(sect, disciple)!!

        if (requirement.contributionCost > 0) {
            val newContribution = memberData.contribution.value - requirement.contributionCost
            world.entity(sect) {
                it.addRelation(disciple, memberData.copy(contribution = Contribution(newContribution)))
            }
        }

        world.entity(disciple) {
            it.addRelation(technique, TechniqueLearned(technique, System.currentTimeMillis()))
        }

        return null
    }

    /**
     * 获取藏经阁中的所有功法
     */
    fun getTechniquesByLibrary(library: Entity): Sequence<Entity> {
        require(library.hasTag<Library>()) { "实体${library.id}不是藏经阁" }
        return library.getRelationsWithData<LibraryContains>().map { it.data.technique }
    }

    /**
     * 获取弟子已学习的所有功法
     */
    fun getLearnedTechniques(disciple: Entity): Sequence<Entity> {
        return disciple.getRelationsWithData<TechniqueLearned>().map { it.data.technique }
    }

    /**
     * 检查弟子是否已学习某功法
     */
    fun hasLearned(disciple: Entity, technique: Entity): Boolean {
        return disciple.getRelation<TechniqueLearned?>(technique) != null
    }

    /**
     * 获取弟子学习某功法的记录
     */
    fun getLearningRecord(disciple: Entity, technique: Entity): TechniqueLearned? {
        return disciple.getRelation<TechniqueLearned?>(technique)
    }

    /**
     * 检查功法是否在藏经阁中
     */
    fun isInLibrary(library: Entity, technique: Entity): Boolean {
        return library.getRelation<LibraryContains?>(technique) != null
    }

    private fun getLibraryBySect(sect: Entity): Entity? {
        return world.query { LibraryQueryContext(this) }.entities.firstOrNull { library ->
            library.getRelationUp<OwnedBy>() == sect
        }
    }

    private fun getDiscipleLevel(disciple: Entity): Long {
        val levelAttribute = attributeService.attribute(Named("level"))
        return disciple.getRelation<AttributeValue?>(levelAttribute)?.value ?: 1L
    }

    @PublishedApi
    internal class LibraryQueryContext(world: World) : EntityQueryContext(world) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            component<Library>()
        }
    }
}

