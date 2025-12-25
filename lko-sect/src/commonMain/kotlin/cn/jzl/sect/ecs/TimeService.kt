package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.World
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId
import cn.jzl.ecs.entity
import cn.jzl.ecs.observers.emit
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.system.system
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * 游戏时间组件
 * 存储从游戏开始累计的游戏总时间（已应用时间比例）
 *
 * 注意：这里存储的是游戏时间，不是真实时间
 * 例如：如果时间比例是1:8640，那么1秒真实时间 = 8640秒游戏时间
 */
@JvmInline
value class Timer(val gameTime: Duration)

data class DateSeason(
    val season: Season,
    val year: Int,
    val month: Int,  // 1-12
    val day: Int // 1-30
) {
    /**
     * 格式化时间显示
     * @param includeSymbol 是否包含季节符号，默认true
     * @return 格式化的时间字符串，如"1年🌸春·一月·1"或"1年春·一月·1"
     */
    fun formatTime(includeSymbol: Boolean = true): String {
        val seasonSymbol = if (includeSymbol) season.symbol else ""
        val seasonName = season.displayName

        val monthName = when (month) {
            1 -> "一"
            2 -> "二"
            3 -> "三"
            4 -> "四"
            5 -> "五"
            6 -> "六"
            7 -> "七"
            8 -> "八"
            9 -> "九"
            10 -> "十"
            11 -> "十一"
            12 -> "十二"
            else -> month.toString()
        }

        return if (includeSymbol) {
            "${year}年${seasonSymbol}${seasonName}·${monthName}月·${day}"
        } else {
            "${year}年${seasonName}·${monthName}月·${day}"
        }
    }

    /**
     * 获取季节符号
     */
    fun getSeasonSymbol(): String = season.symbol

    override fun toString(): String = formatTime(includeSymbol = true)
}

/**
 * 时间速度控制
 * 支持暂停和不同倍速
 */
sealed class TimeSpeed(val speedMultiplier: Double = 1.0) {
    data object Paused : TimeSpeed(0.0)
    data object Normal : TimeSpeed(1.0)
    data object Fast : TimeSpeed(2.0)
    data object VeryFast : TimeSpeed(5.0)
    data object UltraFast : TimeSpeed(10.0)
}

/**
 * 季节枚举
 * 使用符合中国传统文化元素的符号
 */
enum class Season(val symbol: String, val displayName: String) {
    SPRING("🌿", "春"),   // 春 - 嫩芽/草（万物复苏）
    SUMMER("🌾", "夏"),   // 夏 - 稻穗（农忙时节）
    AUTUMN("🍁", "秋"),   // 秋 - 枫叶（秋高气爽）
    WINTER("❄️", "冬")    // 冬 - 雪花（瑞雪兆丰年）
}

/**
 * 日期切换事件
 * 当游戏日期发生变化时触发（每天）
 */
data class OnDayChanged(
    val dateSeason: DateSeason,
    val previousDateSeason: DateSeason
)

/**
 * 月度切换事件
 * 当游戏月份发生变化时触发（每月）
 */
data class OnMonthChanged(
    val dateSeason: DateSeason,
    val previousDateSeason: DateSeason
)

/**
 * 季度切换事件
 * 当游戏季节发生变化时触发（每季度）
 */
data class OnSeasonChanged(
    val dateSeason: DateSeason,
    val previousDateSeason: DateSeason,
    val newSeason: Season,
    val previousSeason: Season
)

/**
 * 年度切换事件
 * 当游戏年份发生变化时触发（每年）
 */
data class OnYearChanged(
    val dateSeason: DateSeason,
    val previousDateSeason: DateSeason,
    val newYear: Int,
    val previousYear: Int
)

/**
 * 时间服务
 *
 * 管理游戏世界的时间流逝、季节变化等功能
 *
 * 功能：
 * 1. 时间流逝：根据帧率和速度倍数累积游戏时间
 * 2. 季节计算：根据游戏时间计算当前季节
 * 3. 时间格式化：提供友好的时间显示格式
 * 4. 速度控制：支持暂停和不同倍速
 *
 * 时间比例说明（以月为最小单位）：
 * - 基础时间比例：1秒真实时间 = 2.4小时游戏时间（1:8640）- 默认
 * - 这意味着：1小时真实时间 = 8640小时游戏时间 = 1年游戏时间
 * - 游戏时间1年（12个月）= 1小时真实时间 ⭐
 * - 游戏时间1个月 = 5分钟真实时间
 * - 资源产出周期（每月）= 5分钟真实时间
 *
 * 推荐比例方案（修真游戏，时间流逝很快，以月为单位）：
 * - 【推荐】方案1：1:8640（1秒真实=2.4小时游戏）- 1年=1小时真实，1月=5分钟真实 ⭐
 * - 方案2：1:12960（1秒真实=3.6小时游戏）- 1年=0.67小时真实，1月=3.3分钟真实（更快）
 * - 方案3：1:4320（1秒真实=1.2小时游戏）- 1年=2小时真实，1月=10分钟真实（较慢）
 */
val timeAddon = createAddon("timeAddon") {
    install(coreAddon)
    injects {
        this bind singleton { new(::TimeService) }
    }
    components {
        world.componentId<Timer>()
        world.componentId<TimeSpeed>()
        world.componentId<DateSeason>()
        world.componentId<OnDayChanged>()
        world.componentId<OnMonthChanged>()
        world.componentId<OnSeasonChanged>()
        world.componentId<OnYearChanged>()
    }
    systems {
        /**
         * 将游戏时间转换为日期季节信息
         * @param gameTime 游戏时间（Duration）
         * @return DateSeason 日期季节信息
         */
        fun getDateSeason(gameTime: Duration): DateSeason {
            // 将游戏时间转换为天数（1个月 = 30天游戏时间）
            val totalDays = gameTime.inWholeDays
            val totalMonths = (totalDays / 30).toInt()

            // 计算年份（从1开始）
            val year = (totalMonths / 12) + 1

            // 计算月份（1-12）
            val month = if (totalMonths % 12 == 0) 12 else (totalMonths % 12) + 1

            // 计算日期（1-30）
            val day = (totalDays % 30).toInt() + 1

            // 根据月份确定季节
            val season = when (month) {
                in 1..3 -> Season.SPRING
                in 4..6 -> Season.SUMMER
                in 7..9 -> Season.AUTUMN
                in 10..12 -> Season.WINTER
                else -> Season.SPRING
            }

            return DateSeason(season, year, month, day)
        }

        val timeService by world.di.instance<TimeService>()
        system(TimeService.TimeContext(world), name = "timeUpdate").exec { delta ->
            // delta 是真实时间增量（Duration）
            // timeSpeed.speedMultiplier 是速度倍数（如1.0, 2.0等）
            // timeService.baseTimeRatio 是时间比例（默认8640.0，即1秒真实=8640秒游戏）

            // 计算游戏时间增量 = 真实时间增量 × 速度倍数 × 时间比例
            val gameDelta = delta * timeSpeed.speedMultiplier * timeService.baseTimeRatio

            // 更新游戏总时间（Timer存储的是游戏时间）
            val newGameTime = timer.gameTime + gameDelta

            // 保存之前的日期信息
            val previousDateSeason = dateSeason

            // 更新游戏时间和日期
            timer = Timer(newGameTime)
            val newDateSeason = getDateSeason(newGameTime)
            dateSeason = newDateSeason

            // 检测并触发各种时间切换事件
            val timeEntity = entity

            // 1. 检测日期切换（每天）
            if (newDateSeason.day != previousDateSeason.day) {
                world.emit(
                    timeEntity,
                    OnDayChanged(
                        dateSeason = newDateSeason,
                        previousDateSeason = previousDateSeason
                    )
                )
            }

            // 2. 检测月度切换（每月）
            if (newDateSeason.month != previousDateSeason.month || newDateSeason.year != previousDateSeason.year) {
                world.emit(
                    timeEntity,
                    OnMonthChanged(
                        dateSeason = newDateSeason,
                        previousDateSeason = previousDateSeason
                    )
                )
            }

            // 3. 检测季度切换（每季度，季节变化）
            if (newDateSeason.season != previousDateSeason.season) {
                world.emit(
                    timeEntity,
                    OnSeasonChanged(
                        dateSeason = newDateSeason,
                        previousDateSeason = previousDateSeason,
                        newSeason = newDateSeason.season,
                        previousSeason = previousDateSeason.season
                    )
                )
            }

            // 4. 检测年度切换（每年）
            if (newDateSeason.year != previousDateSeason.year) {
                world.emit(
                    timeEntity,
                    OnYearChanged(
                        dateSeason = newDateSeason,
                        previousDateSeason = previousDateSeason,
                        newYear = newDateSeason.year,
                        previousYear = previousDateSeason.year
                    )
                )
            }
        }
    }
}

class TimeService(world: World) : EntityRelationContext(world) {

    /**
     * 基础时间比例：1秒真实时间 = 多少秒游戏时间
     * 默认：8640.0（1秒真实 = 2.4小时游戏 = 1年游戏 = 1小时真实）
     */
    var baseTimeRatio: Double = 8640.0
        private set

    // 游戏时间实体（单例）
    val timeEntity: Entity = world.entity {
        it.addComponent(Timer(0.seconds))  // 初始游戏时间为0
        it.addComponent<TimeSpeed>(TimeSpeed.Normal)
        it.addComponent(DateSeason(Season.SPRING, 1, 1, 1))
    }

    /**
     * 获取当前游戏时间
     * @return 当前游戏时间（Duration）
     */
    fun getCurrentGameTime(): Duration {
        return timeEntity.getComponent<Timer>()?.gameTime ?: Duration.ZERO
    }

    /**
     * 获取当前日期季节信息
     * @return 当前日期季节信息
     */
    fun getCurrentDate(): DateSeason {
        return timeEntity.getComponent<DateSeason>()
    }

    /**
     * 设置时间速度
     * @param speed 时间速度（Paused, Normal, Fast等）
     */
    fun setTimeSpeed(speed: TimeSpeed) {
        world.entity(timeEntity) {
            it.addComponent(speed)
        }
    }

    /**
     * 设置基础时间比例
     * @param ratio 1秒真实时间对应的游戏时间秒数（推荐：8640.0）
     */
    fun setBaseTimeRatio(ratio: Double) {
        require(ratio > 0) { "时间比例必须大于0" }
        baseTimeRatio = ratio
    }

    /**
     * 格式化时间显示
     * @return 格式化的时间字符串，如"1年春·一月·1"
     */
    fun formatTime(): String {
        return getCurrentDate().toString()
    }

    /**
     * 将真实时间转换为游戏时间
     * @param realTime 真实时间（Duration）
     * @return 游戏时间（Duration）
     */
    fun realTimeToGameTime(realTime: Duration): Duration {
        return realTime * baseTimeRatio
    }

    /**
     * 将游戏时间转换为真实时间
     * @param gameTime 游戏时间（Duration）
     * @return 真实时间（Duration）
     */
    fun gameTimeToRealTime(gameTime: Duration): Duration {
        return gameTime / baseTimeRatio
    }

    /**
     * 时间上下文（用于系统查询）
     */
    class TimeContext(world: World) : EntityQueryContext(world) {
        var timer by component<Timer>()
        var dateSeason by component<DateSeason>()
        val timeSpeed by component<TimeSpeed>()
    }
}