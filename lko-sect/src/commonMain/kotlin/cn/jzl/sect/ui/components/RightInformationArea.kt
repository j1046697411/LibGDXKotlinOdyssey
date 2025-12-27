package cn.jzl.sect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jzl.sect.ui.MenuOption

/**
 * 右侧信息区域组件
 * 
 * 根据当前选中的菜单选项显示不同的信息内容，包括各种统计数据、快捷操作和相关信息
 * 
 * @param modifier 修饰符
 * @param currentMenu 当前选中的菜单选项
 */
@Composable
fun RightInformationArea(modifier: Modifier, currentMenu: MenuOption) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            hoveredElevation = 12.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 根据当前菜单显示不同内容
            when (currentMenu) {
                MenuOption.ZONGMEN_OVERVIEW -> {
                    // 掌门信息和快捷操作
                    ZongmenLeaderInfoContent()
                }
                MenuOption.DISCIPLE_MANAGEMENT -> {
                    // 弟子管理右侧信息
                    DiscipleManagementRightContent()
                }
                MenuOption.TECHNIQUE_HALL -> {
                    // 功法堂右侧信息
                    TechniqueHallRightContent()
                }
                MenuOption.TASK_HALL -> {
                    // 任务大厅右侧信息
                    TaskHallRightContent()
                }
                MenuOption.RESOURCE_MANAGEMENT -> {
                    // 资源管理右侧信息
                    ResourceManagementRightContent()
                }
                MenuOption.MAP_EXPLORATION -> {
                    // 地图探索右侧信息
                    MapExplorationRightContent()
                }
                MenuOption.FACILITY_CONSTRUCTION -> {
                    // 设施建设右侧信息
                    FacilityConstructionRightContent()
                }
                MenuOption.ALCHEMY_ROOM -> {
                    // 炼丹房右侧信息
                    AlchemyRoomRightContent()
                }
                MenuOption.FORGING_ROOM -> {
                    // 炼器房右侧信息
                    ForgingRoomRightContent()
                }
                MenuOption.SOCIAL -> {
                    // 社交右侧信息
                    SocialRightContent()
                }
                MenuOption.SETTINGS -> {
                    // 设置右侧信息
                    SettingsRightContent()
                }
                else -> {
                    // 角色信息
                    CharacterInfoContent()
                }
            }
        }
    }
}

// 宗门仓库内容
@Composable
private fun ZongmenWarehouseContent(
    textColor: Color,
    sectionTitleColor: Color,
    statusColor: Color
) {
    val borderColor = Color(0xFFBDBDBD)
    
    Column {
        // 仓库标题
        Text(
            text = "📦 宗门仓库 - 资源类",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 仓库基本信息
        Text(
            text = "🔹 仓库容量：250/1000",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 物品总数：128",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 资源物品
        Text(
            text = "【资源物品】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🌿 灵草 × 45 · ⛏️ 矿石 × 32 · 🌲 木材 × 28",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "💧 泉水 × 20 · ⭐ 贡献点 × 15600",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 丹药物品
        Text(
            text = "【丹药物品】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "💊 回气丹 × 12 · 💊 疗伤丹 × 8 · 💊 聚气丹 × 5",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "💊 筑基丹 × 2 · 💊 解毒丹 × 3",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 装备物品
        Text(
            text = "【装备物品】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "⚔️ 青云剑 × 1 · 🛡️ 玄铁护甲 × 1",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "📿 储物袋 × 3 · 💍 聚气戒指 × 1",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 快捷操作
        Text(
            text = "【快捷操作】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[📊仓库管理] [🔄刷新] [📤取出] [📥存入]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 筛选选项
        Text(
            text = "🔹 筛选：【全部】【资源】【丹药】【装备】【功法】",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[🔀切换分类]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 最新入库
        Text(
            text = "【最新入库】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔔 灵草 × 15 (1小时前)",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔔 聚气丹 × 3 (2小时前)",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔔 矿石 × 10 (3小时前)",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

// 掌门信息和快捷操作内容
@Composable
private fun ZongmenLeaderInfoContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 掌门信息
        TerminalCard(title = "掌门信息") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 林玄风·金丹中期")
                Text(
                    text = "💗 2850/2850 · 💠 1240/1800",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "⏳ 寿元：128年 · ✨ 贡献：2450",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 宗门快捷操作
        TerminalCard(title = "宗门快捷操作") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "[👥弟子管理] [📦资源管理] [🏗️设施建设] [🗺️地图探索]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "[📋任务大厅] [📚功法堂] [🧪炼丹房] [⚒️炼器房]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 当前状态加成
        TerminalCard(title = "当前状态加成") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "✅ 青灵殿+15% · ✅ 聚气丹+20%",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "✅ 灵田+10% · ✅ 聚灵阵+5%",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 宗门当前状态
        TerminalCard(title = "宗门当前状态") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 运行状态：稳定 · 发展阶段：快速增长")
                Text(text = "🔹 在线弟子：89/128 · AI活跃：112/128")
            }
        }

        // 基础快捷操作
        TerminalCard(title = "基础快捷操作") {
            Text(
                text = "/搜索 | F1帮助 | S设置 | ESC菜单",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 角色信息内容
@Composable
private fun CharacterInfoContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 角色信息
        TerminalCard(title = "角色信息") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "👤林玄风·金丹中期",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "💗2850/2850 💠1240/1800",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "⏳寿元：128年 · ✨贡献：2450",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "✅青灵殿+15% · ✅聚气丹+20%",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 快捷操作
        TerminalCard(title = "快捷操作") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "/搜索 | F1帮助")
                Text(text = "S设置 | ESC菜单")
            }
        }

        // 当前状态
        TerminalCard(title = "当前状态") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "正在执行：任务大厅 · 效率：120%")
                Text(text = "操作中任务：0/5")
            }
        }

        // 状态加成
        TerminalCard(title = "状态加成") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "✅青灵殿+15% · ✅聚气丹+20%",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "✅灵田+10% · ✅聚灵阵+5%",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// 功法堂右侧信息内容
@Composable
private fun TechniqueHallRightContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 功法统计
        TerminalCard(title = "📊 功法统计") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 功法总数：35种")
                Text(text = "🔹 已学习：12种")
                Text(text = "🔹 可学习：8种")
            }
        }

        // 功法快捷操作
        TerminalCard(title = "功法快捷操作") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "[📚功法列表] [📊功法统计] [🔄刷新] [📋学习记录]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "[💡学习建议] [📤快速学习] [📥快速修炼]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 已学习功法
        TerminalCard(title = "已学习功法") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 青灵心法 (木·灵级)")
                Text(text = "🔹 玄水诀 (水·灵级)")
                Text(text = "🔹 烈火掌 (火·凡级)")
            }
        }

        // 修炼中功法
        TerminalCard(title = "修炼中功法") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🔹 青冥剑诀 (金·天级) - 进度：35%",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "🔹 玄铁防御诀 (土·地级) - 进度：65%",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// 任务大厅右侧信息内容
@Composable
private fun TaskHallRightContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 任务统计
        TerminalCard(title = "📊 任务统计") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 已接任务：3/10")
                Text(text = "🔹 可接任务：12/20")
                Text(text = "🔹 完成任务：45/100")
            }
        }

        // 任务快捷操作
        TerminalCard(title = "任务快捷操作") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "[📋任务列表] [📊任务统计] [🔄刷新] [⏱️任务日志]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "[💡任务建议] [📤快速接受] [📥快速完成]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 今日完成
        TerminalCard(title = "今日完成") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 日常任务：2/5")
                Text(text = "🔹 委托任务：1/3")
                Text(text = "🔹 试炼任务：0/2")
            }
        }

        // 最新任务
        TerminalCard(title = "最新任务") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🔔 前往黑风林采集木材",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "🔔 帮助张长老炼制丹药",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "🔔 探索青灵山脉",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// 资源管理右侧信息内容
@Composable
private fun ResourceManagementRightContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 仓库基本信息
        TerminalCard(title = "📦 宗门仓库") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 仓库容量：250/1000")
                Text(text = "🔹 物品总数：128")
            }
        }

        // 仓库快捷操作
        TerminalCard(title = "仓库快捷操作") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "[📊仓库管理] [🔄刷新] [📤取出] [📥存入]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "[💰交易] [🔄转换] [📋分配] [🔥消耗]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 资源流动
        TerminalCard(title = "资源流动") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 今日收入：灵石+1200、灵草+250")
                Text(text = "🔹 今日支出：灵石-850、矿石-300")
            }
        }

        // 最新入库
        TerminalCard(title = "最新入库") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🔔 灵草×15 (1小时前)",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "🔔 聚气丹×3 (2小时前)",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "🔔 矿石×10 (3小时前)",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// 地图探索右侧信息内容
@Composable
private fun MapExplorationRightContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 探索统计
        TerminalCard(title = "📊 探索统计") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 已探索区域：5个")
                Text(text = "🔹 已占领区域：3个")
                Text(text = "🔹 正在探索：2个")
            }
        }

        // 探索快捷操作
        TerminalCard(title = "探索快捷操作") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "[🔍探索] [🏰占领] [📊管理] [🔄刷新]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "[📋任务] [⚔️试炼] [📦资源] [📈事件]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 探索建议
        TerminalCard(title = "探索建议") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "💡 建议探索青灵山脉",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "💡 建议占领黑风林",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 探索队伍
        TerminalCard(title = "探索队伍") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 探索队伍：2支")
                Text(text = "🔹 空闲队伍：3支")
            }
        }
    }
}

// 设施建设右侧信息内容
@Composable
private fun FacilityConstructionRightContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 设施统计
        TerminalCard(title = "📊 设施统计") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 设施总数：12/20")
                Text(text = "🔹 正在建设：2座")
                Text(text = "🔹 可升级：4座")
            }
        }

        // 设施快捷操作
        TerminalCard(title = "设施快捷操作") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "[🏗️新建设施] [📈批量升级] [🛠️批量维护]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "[🔄刷新] [📋建设记录] [💡建设建议]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 建设队列
        TerminalCard(title = "建设队列") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 队列：2/3")
                Text(
                    text = "1. 炼丹房 (75%) - 45分钟",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "2. 炼器阁 (35%) - 1小时30分钟",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 升级建议
        TerminalCard(title = "升级建议") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "💡 建议升级青灵殿至4级",
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "💡 建议扩建灵田至3级",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        // 维护提醒
        TerminalCard(title = "维护提醒") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "⚠️ 3座设施需要维护",
                    color = MaterialTheme.colorScheme.error
                )
                Text(text = "🔹 伐木场 - 效率85%")
                Text(text = "🔹 采矿场 - 效率75%")
                Text(text = "🔹 聚灵阵 - 效率90%")
            }
        }
    }
}

// 炼丹房右侧信息内容
@Composable
private fun AlchemyRoomRightContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 炼丹统计
        TerminalCard(title = "📊 炼丹统计") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 今日炼制：5次")
                Text(text = "🔹 成功率：85%")
                Text(text = "🔹 总炼制：128次")
            }
        }

        // 炼丹快捷操作
        TerminalCard(title = "炼丹快捷操作") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "[🧪开始炼制] [⏩加速炼制] [🔄刷新列表]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "[📋炼制历史] [💡炼制建议] [📈炼丹统计]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 当前炼制
        TerminalCard(title = "当前炼制") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔥 聚气丹 (二阶)")
                Text(text = "🔹 进度：65% | 剩余：45分钟")
                Text(text = "🔹 成功率：85% | 预计：5-8枚")
            }
        }

        // 材料消耗
        TerminalCard(title = "材料消耗") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 灵草：10/1250")
                Text(text = "🔹 泉水：5/850")
                Text(text = "🔹 聚气草：3/120")
            }
        }

        // 炼制建议
        TerminalCard(title = "炼制建议") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "💡 建议炼制回气丹",
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "💡 建议升级丹炉",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// 炼器房右侧信息内容
@Composable
private fun ForgingRoomRightContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 炼器统计
        TerminalCard(title = "📊 炼器统计") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 今日锻造：3次")
                Text(text = "🔹 成功率：75%")
                Text(text = "🔹 总锻造：89次")
            }
        }

        // 炼器快捷操作
        TerminalCard(title = "炼器快捷操作") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "[⚒️开始锻造] [⏩加速锻造] [🔄刷新列表]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "[📋锻造历史] [💡锻造建议] [📈炼器统计]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 当前锻造
        TerminalCard(title = "当前锻造") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔥 玄铁剑 (一阶)")
                Text(text = "🔹 进度：42% | 剩余：1小时15分钟")
                Text(text = "🔹 成功率：75% | 预计品质：精良")
            }
        }

        // 材料消耗
        TerminalCard(title = "材料消耗") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 玄铁：15/150")
                Text(text = "🔹 铁矿石：10/280")
                Text(text = "🔹 木炭：5/120")
            }
        }

        // 锻造建议
        TerminalCard(title = "锻造建议") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "💡 建议锻造玄铁护甲",
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "💡 建议升级熔炉",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// 社交右侧信息内容
@Composable
private fun SocialRightContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 社交统计
        TerminalCard(title = "📊 社交统计") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 友好宗门：2个")
                Text(text = "🔹 敌对宗门：2个")
                Text(text = "🔹 消息通知：5条")
            }
        }

        // 社交快捷操作
        TerminalCard(title = "社交快捷操作") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "[📧消息中心] [👥好友管理] [🏛️宗门关系]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "[🎉活动报名] [💬发起聊天] [🎁赠送礼物]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 最近消息
        TerminalCard(title = "最近消息") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🔔 玄水宗使者来访",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "🔔 弟子张无忌突破",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "🔔 青风谷赠送灵草",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 好友列表
        TerminalCard(title = "好友列表") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "✅ 玄水宗掌门 (在线)",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(text = "✅ 青风谷长老 (离线)")
                Text(
                    text = "✅ 弟子张无忌 (在线)",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 社交建议
        TerminalCard(title = "社交建议") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "💡 建议提升玄水宗友好度",
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "💡 建议参与宗门大比",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// 设置右侧信息内容
@Composable
private fun SettingsRightContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 快捷设置
        TerminalCard(title = "⚡ 快捷设置") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 游戏速度：【正常×1】[快速×2] [极速×3]")
                Text(text = "🔹 音效：【开启】[关闭]")
                Text(text = "🔹 自动存档：【开启】[关闭]")
            }
        }

        // 系统信息
        TerminalCard(title = "💻 系统信息") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 游戏版本：v1.2.3")
                Text(text = "🔹 系统：Windows 10 64位")
                Text(text = "🔹 内存：8GB | CPU：i7-8700K")
            }
        }

        // 帮助与支持
        TerminalCard(title = "❓ 帮助与支持") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "[📖游戏指南] [🎯常见问题] [📧联系客服]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "[🔔更新日志] [🔍反馈bug] [📝建议提交]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// 弟子管理右侧信息内容
@Composable
private fun DiscipleManagementRightContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 弟子统计
        TerminalCard(title = "📊 弟子统计") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 弟子总数：128/200")
                Text(text = "🔹 在线弟子：89/128")
                Text(text = "🔹 可招收：72人")
            }
        }

        // 弟子快捷操作
        TerminalCard(title = "弟子快捷操作") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "[👥招收弟子] [📋批量管理] [📚批量培养]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "[🔄刷新列表] [📈弟子统计] [💡培养建议]",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 在线弟子
        TerminalCard(title = "在线弟子") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "✅ 张无忌 (筑基中期)",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "✅ 赵敏 (炼气后期)",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "✅ 杨逍 (金丹初期)",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 最新招收
        TerminalCard(title = "最新招收") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🔹 小昭 (炼气中期) - 2小时前")
                Text(text = "🔹 周芷若 (筑基初期) - 5小时前")
            }
        }
    }
}