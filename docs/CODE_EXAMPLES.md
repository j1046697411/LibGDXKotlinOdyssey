# 代码示例与最佳实践

本文件展示如何遵循宗门模拟游戏项目宪法编写高质量代码。

---

## 1. 代码质量示例

### ✓ 正确做法：清晰的命名与注释

```text
package cn.jzl.sect.ecs.sect

/**
 * 宗门建设逻辑：用于校验并执行设施建造。
 *
 * 本模块负责校验宗门资源是否满足建造需求，
 * 并在校验通过后执行相应的状态转换。
 *
 * @see SectConstruction.validateConstruction
 * @see SectConstruction.executeConstruction
 */
object SectConstruction {
    
    /**
     * 表示一次宗门建造请求，包含建筑类型与资源需求。
     */
    data class ConstructionRequest(
        val facilityType: String,
        val requiredResources: Map<String, Int>
    )

    /**
     * 根据宗门资源校验建造请求是否可执行。
     *
     * @param sectResources 当前宗门资源（资金、材料等）
     * @param request 建造请求（类型与资源需求）
     * @return 若资源足够则返回 Result.success，否则返回 Result.failure
     * @throws IllegalArgumentException 当请求参数无效时抛出
     *
     * @see SectResources
     */
    fun validateConstruction(
        sectResources: SectResources,
        request: ConstructionRequest
    ): Result<Unit> {
        require(request.facilityType.isNotEmpty()) { "Facility type cannot be empty" }
        
        if (sectResources.funds < 100) {
            return Result.failure(Exception("Insufficient funds (need 100, have ${sectResources.funds})"))
        }
        if (sectResources.materials < 50) {
            return Result.failure(Exception("Insufficient materials (need 50, have ${sectResources.materials})"))
        }
        
        return Result.success(Unit)
    }

    /**
     * 执行建造操作，扣除宗门相应资源。
     *
     * 假设 [validateConstruction] 已被调用且通过。
     *
     * @param sectResources 当前宗门资源
     * @param request 建造请求（用于日志/审计）
     * @return 扣除资源后的更新宗门资源
     */
    fun executeConstruction(
        sectResources: SectResources,
        request: ConstructionRequest
    ): SectResources {
        return sectResources.copy(
            funds = sectResources.funds - 100,
            materials = sectResources.materials - 50
        )
    }
}
```

### ✗ 错误做法：模糊的命名与缺乏文档

```kotlin
// ✗ 不好: 不清晰的变量名
fun process(s: SectResources, r: ConstructionRequest): Result<Unit> {
    if (s.f < 100) return Result.failure(Exception("error"))
    if (s.m < 50) return Result.failure(Exception("error2"))
    return Result.success(Unit)
}

// ✗ 不好: 无文档注释
fun execute(sectResources: SectResources, request: ConstructionRequest): SectResources {
    return sectResources.copy(funds = s.funds - 100, materials = s.materials - 50)
}
```

---

## 2. 模块化与依赖管理示例

### ✓ 正确做法：通过 Addon 解耦

```text
package cn.jzl.sect.ecs.sect

class SectAddon {
    fun install(world: World) {
        // 注册组件
        // world.componentProvider.configure<SectInfo> { it.dataClass() }
        
        // 注册系统 (通过 pipeline builder，不直接依赖)
        // world.system(SectContext()) { /* ... */ }
    }
}

// ✓ 在 Core 中集中安装 Addon
class Core {
    fun initialize() {
        world = world {
            SectAddon().install(this)
            FacilityAddon().install(this)
            TaskAddon().install(this)
        }
    }
}
```

### ✗ 错误做法：直接依赖和循环引用

```kotlin
// ✗ 不好: 直接创建依赖对象
class SectSystem {
    private val facilitySystem = FacilitySystem()  // ✗ 直接依赖！
    
    fun update() {
        facilitySystem.processUpgrades()  // ✗ 循环依赖！
    }
}
```

---

## 3. 错误处理示例

### ✓ 正确做法：显式错误处理

```kotlin
// ✓ 好: 使用 Result 类型
fun gatherResources(disciple: DiscipleInfo): Result<Int> {
    val gatherAmount = calculateGatherAmount(disciple)
    
    if (gatherAmount <= 0) {
        return Result.failure(Exception("Disciple level too low for gathering"))
    }
    
    return Result.success(gatherAmount)
}

// 调用端处理结果
val result = gatherResources(myDisciple)
result.onSuccess { amount ->
    println("Gathered $amount resources")
}.onFailure { error ->
    println("Error: ${error.message}")
}
```

### ✗ 错误做法：忽略异常

```kotlin
// ✗ 不好: catch 块什么都不做
fun gatherResources(disciple: DiscipleInfo): Int {
    return try {
        calculateGatherAmount(disciple)
    } catch (e: Exception) {
        // ✗ 忽略异常！
        return 0
    }
}
```

---

## 4. 测试示例

### ✓ 正确做法：完整的 AAA 模式

```text
package cn.jzl.sect.ecs.sect

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class SectConstructionTest {
    
    /**
     * 测试：当资源充足时，建造校验应当通过。
     */
    @Test
    fun testValidateConstructionWhenSufficientResourcesExpectSuccess() {
        // Arrange
        val resources = SectResources(funds = 200, materials = 100)
        val request = SectConstruction.ConstructionRequest(
            facilityType = "MAIN_HALL",
            requiredResources = mapOf("funds" to 100, "materials" to 50)
        )

        // Act
        val result = SectConstruction.validateConstruction(resources, request)

        // Assert
        assertTrue(result.isSuccess)
    }

    /**
     * 测试：当资金不足时，建造校验应当失败。
     */
    @Test
    fun testValidateConstructionWhenInsufficientFundsExpectFailure() {
        // Arrange
        val resources = SectResources(funds = 50, materials = 100)
        val request = SectConstruction.ConstructionRequest(
            facilityType = "MAIN_HALL",
            requiredResources = mapOf("funds" to 100, "materials" to 50)
        )

        // Act
        val result = SectConstruction.validateConstruction(resources, request)

        // Assert
        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull()?.message?.contains("Insufficient funds") ?: false)
    }

    /**
     * 测试：执行建造后应正确扣除资源。
     */
    @Test
    fun testExecuteConstructionDeductsResourcesCorrectly() {
        // Arrange
        val resources = SectResources(funds = 200, materials = 100)
        val request = SectConstruction.ConstructionRequest(
            facilityType = "MAIN_HALL",
            requiredResources = emptyMap()
        )

        // Act
        val updated = SectConstruction.executeConstruction(resources, request)

        // Assert
        assertTrue(updated.funds == 100)
        assertTrue(updated.materials == 50)
    }
}
```

### ✗ 错误做法：测试混乱且覆盖不全

```kotlin
// ✗ 不好: 无清晰命名
@Test
fun test() {
    // 没有 Arrange/Act/Assert 分离
    val resources = SectResources(200, 100)
    val result = SectConstruction.validateConstruction(resources, /*....*/)
    // 不清楚在测试什么
}
```

---

## 5. 性能优化示例

### ✓ 正确做法：预查询 & 批量操作

```kotlin
// ✓ 好: 预查询结果缓存，避免重复查询
class TaskAssignmentSystem {
    private var cachedIdleDisciplesQuery: Query<*>? = null
    
    fun assignTasks(world: World, tasks: List<TaskInfo>) {
        // 一次查询所有空闲弟子
        val idleDisciples = cachedIdleDisciplesQuery?.let { query ->
            query.toList()
        } ?: world.queryService.query {
            // 自定义查询条件
        }.also { cachedIdleDisciplesQuery = it }
        
        // 批量分配，而不是逐个循环
        val assignments = tasks.mapIndexed { index, task ->
            if (index < idleDisciples.size) {
                TaskAssignment(task, idleDisciples[index])
            } else null
        }.filterNotNull()
        
        // 批量应用更新
        applyAssignments(world, assignments)
    }
}
```

### ✗ 错误做法：重复查询 & 逐个操作

```kotlin
// ✗ 不好: 在循环中反复查询
fun assignTasks(world: World, tasks: List<TaskInfo>) {
    tasks.forEach { task ->
        // ✗ 每次都查询一遍所有弟子！
        val idleDisciples = world.queryService.query { /* ... */ }
        if (idleDisciples.isNotEmpty()) {
            applyAssignment(world, task, idleDisciples.first())  // ✗ 逐个应用！
        }
    }
}
```

---

## 6. UI 一致性示例

### ✓ 正确做法：统一的界面规范

```kotlin
// ✓ 好: 统一的界面组件和文案
object SectUI {
    fun displayMainScreen(sect: SectInfo) {
        println("═══════════════════════════════════")
        println("  [宗门]: ${sect.name} (等级 ${sect.level})")
        println("═══════════════════════════════════")
        println("")
        println("📊 状态")
        println("  资金: ${sect.resources.funds} ⭐")
        println("  物资: ${sect.resources.materials} 📦")
        println("")
        println("🔧 操作")
        println("  [1] 查看弟子列表")
        println("  [2] 建设设施")
        println("  [3] 发布任务")
        println("  [0] 返回")
        println("")
        print("请选择 (0-3): ")
    }

    fun displayError(message: String) {
        println("❌ 错误: $message")
    }

    fun displaySuccess(message: String) {
        println("✓ 成功: $message")
    }

    fun displayWarning(message: String) {
        println("⚠ 警告: $message")
    }
}
```

### ✗ 错误做法：不一致的界面

```kotlin
// ✗ 不好: 无规范，文案不一致
fun showSect(sect: SectInfo) {
    println("SECT NAME: " + sect.name)  // 大写，无 emoji
    println("funds:" + sect.resources.funds)  // 小写，格式乱
    println("stuff count=" + sect.resources.materials)  // 不同的词，无单位
    println("Choose 1/2/3 or quit with q")  // 不一致的提示格式
}
```

---

## 总结

高质量的代码应该:

✓ **清晰** - 易于理解的命名、充分的文档  
✓ **模块化** - 通过 Addon 解耦、避免循环依赖  
✓ **安全** - 显式错误处理、不忽略异常  
✓ **可测** - 完整的测试覆盖、遵循 AAA 模式  
✓ **高效** - 避免重复查询、批量操作而非逐个  
✓ **一致** - 统一的界面规范、命名约定、交互流程  

详见 [`.specify/memory/constitution.md`](../.specify/memory/constitution.md) 获得完整的项目规则。
