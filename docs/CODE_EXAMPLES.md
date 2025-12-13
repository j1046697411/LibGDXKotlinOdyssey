# 代码示例与最佳实践

本文件展示如何遵循宗门模拟游戏项目宪法编写高质量代码。

---

## 1. 代码质量示例

### ✓ 正确做法：清晰的命名与注释

```kotlin
package cn.jzl.sect.ecs.sect

/**
 * Sect construction logic: validates and executes facility construction.
 * 
 * This module handles validation of sect resources against construction
 * requirements, and executes state transitions upon successful validation.
 * 
 * @see SectConstruction.validateConstruction
 * @see SectConstruction.executeConstruction
 */
object SectConstruction {
    
    /**
     * Represents a sect construction request with type and resource requirements.
     */
    data class ConstructionRequest(
        val facilityType: String,
        val requiredResources: Map<String, Int>
    )

    /**
     * Validates a construction request against sect resources.
     * 
     * @param sectResources current sect resources (funds, materials)
     * @param request construction request with type and requirements
     * @return Result.success if resources are sufficient, Result.failure otherwise
     * @throws IllegalArgumentException if request is invalid
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
     * Executes construction by deducting resources from the sect.
     * 
     * Assumes [validateConstruction] has been called and passed.
     * 
     * @param sectResources current sect resources
     * @param request construction request (for logging/audit)
     * @return updated sect resources with deducted amounts
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

```kotlin
// ✓ 好: 通过 Addon 系统隔离模块
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

```kotlin
package cn.jzl.sect.ecs.sect

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class SectConstructionTest {
    
    /**
     * Test that construction validation passes with sufficient resources.
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
     * Test that construction validation fails when funds are insufficient.
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
     * Test that construction execution properly deducts resources.
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

详见 [`constitution.md`](../../specs/003-sect-simulation-game/constitution.md) 获得完整的项目规则。

