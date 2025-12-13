package cn.jzl.sect.ecs.sect

import cn.jzl.di.instance
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.World
import cn.jzl.sect.ecs.InventoryService
import cn.jzl.sect.ecs.Sect

/**
 * 宗门资源服务
 * 封装 InventoryService 提供宗门资源管理 API
 */
class SectResourceService(world: World) : EntityRelationContext(world) {

    private val inventoryService by world.di.instance<InventoryService>()

    /**
     * 存入单个资源到宗门仓库
     * @param sect 宗门实体
     * @param itemPrefab 物品预制体
     * @param amount 数量
     */
    fun deposit(sect: Entity, itemPrefab: Entity, amount: Int) {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        require(amount > 0) { "存入数量必须大于0" }
        inventoryService.addItem(sect, itemPrefab, amount)
    }

    /**
     * 从宗门仓库取出单个资源
     * @param sect 宗门实体
     * @param itemPrefab 物品预制体
     * @param amount 数量
     * @return 成功返回 null，失败返回错误信息
     */
    fun withdraw(sect: Entity, itemPrefab: Entity, amount: Int): ResourceError? {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        require(amount > 0) { "取出数量必须大于0" }

        val current = getResourceAmount(sect, itemPrefab)
        if (current < amount) {
            return ResourceError.InsufficientResource(itemPrefab, amount, current)
        }

        inventoryService.removeItem(sect, itemPrefab, amount)
        return null
    }

    /**
     * 批量存入资源
     * @param sect 宗门实体
     * @param resources 资源映射 (物品预制体 -> 数量)
     */
    fun depositAll(sect: Entity, resources: Map<Entity, Int>) {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        resources.forEach { (itemPrefab, amount) ->
            if (amount > 0) {
                inventoryService.addItem(sect, itemPrefab, amount)
            }
        }
    }

    /**
     * 批量取出资源
     * @param sect 宗门实体
     * @param resources 资源映射 (物品预制体 -> 数量)
     * @return 成功返回 null，失败返回错误信息
     */
    fun withdrawAll(sect: Entity, resources: Map<Entity, Int>): ResourceError? {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }

        // 先检查所有资源是否充足
        val missing = getMissingResources(sect, resources)
        if (missing.isNotEmpty()) {
            return ResourceError.MultipleMissing(missing)
        }

        // 逐一取出
        resources.forEach { (itemPrefab, amount) ->
            if (amount > 0) {
                inventoryService.removeItem(sect, itemPrefab, amount)
            }
        }
        return null
    }

    /**
     * 从成员背包转移资源到宗门仓库
     * @param sect 宗门实体
     * @param member 成员实体
     * @param itemPrefab 物品预制体
     * @param amount 数量
     * @return 成功返回 null，失败返回错误信息
     */
    fun transferFromMember(sect: Entity, member: Entity, itemPrefab: Entity, amount: Int): ResourceError? {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        require(amount > 0) { "转移数量必须大于0" }

        val memberHas = inventoryService.getItemCount(member, itemPrefab)
        if (memberHas < amount) {
            return ResourceError.InsufficientResource(itemPrefab, amount, memberHas)
        }

        inventoryService.removeItem(member, itemPrefab, amount)
        inventoryService.addItem(sect, itemPrefab, amount)
        return null
    }

    /**
     * 从宗门仓库转移资源到成员背包
     * @param sect 宗门实体
     * @param member 成员实体
     * @param itemPrefab 物品预制体
     * @param amount 数量
     * @return 成功返回 null，失败返回错误信息
     */
    fun transferToMember(sect: Entity, member: Entity, itemPrefab: Entity, amount: Int): ResourceError? {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        require(amount > 0) { "转移数量必须大于0" }

        val error = withdraw(sect, itemPrefab, amount)
        if (error != null) {
            return error
        }

        inventoryService.addItem(member, itemPrefab, amount)
        return null
    }

    /**
     * 获取宗门特定资源的数量
     * @param sect 宗门实体
     * @param itemPrefab 物品预制体
     * @return 资源数量
     */
    fun getResourceAmount(sect: Entity, itemPrefab: Entity): Int {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        return inventoryService.getItemCount(sect, itemPrefab)
    }

    /**
     * 获取宗门所有资源
     * @param sect 宗门实体
     * @return 所有物品实体序列
     */
    fun getAllResources(sect: Entity): Sequence<Entity> {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        return inventoryService.getAllItems(sect)
    }

    /**
     * 检查宗门是否有足够的资源
     * @param sect 宗门实体
     * @param itemPrefab 物品预制体
     * @param amount 所需数量
     * @return 是否足够
     */
    fun hasEnoughResource(sect: Entity, itemPrefab: Entity, amount: Int): Boolean {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        return inventoryService.hasEnoughItems(sect, itemPrefab, amount)
    }

    /**
     * 检查宗门是否有足够的批量资源
     * @param sect 宗门实体
     * @param resources 所需资源映射
     * @return 是否全部足够
     */
    fun hasEnoughResources(sect: Entity, resources: Map<Entity, Int>): Boolean {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        return resources.all { (itemPrefab, amount) ->
            inventoryService.hasEnoughItems(sect, itemPrefab, amount)
        }
    }

    /**
     * 获取缺少的资源
     * @param sect 宗门实体
     * @param required 所需资源映射
     * @return 缺少的资源映射 (物品预制体 -> 缺少数量)
     */
    fun getMissingResources(sect: Entity, required: Map<Entity, Int>): Map<Entity, Int> {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        return required.mapNotNull { (itemPrefab, amount) ->
            val current = inventoryService.getItemCount(sect, itemPrefab)
            val missing = amount - current
            if (missing > 0) itemPrefab to missing else null
        }.toMap()
    }
}

/**
 * 资源操作错误
 */
sealed class ResourceError {
    /**
     * 单个资源不足
     */
    data class InsufficientResource(
        val itemPrefab: Entity,
        val required: Int,
        val available: Int
    ) : ResourceError()

    /**
     * 多个资源不足
     */
    data class MultipleMissing(
        val missing: Map<Entity, Int>
    ) : ResourceError()
}

