package cn.jzl.sect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jzl.sect.ui.MenuOption

@Composable
fun RightInformationArea(modifier: Modifier, currentMenu: MenuOption) {
    val contentColor = Color.White
    val borderColor = Color(0xFFBDBDBD)
    val textColor = Color(0xFF212121)
    val sectionTitleColor = Color(0xFF2196F3)
    val attributeColor = Color(0xFF4CAF50)
    val statusColor = Color(0xFFFFC107)
    
    Column(
        modifier = modifier
            .background(contentColor)
            .border(1.dp, borderColor)
            .padding(8.dp)
    ) {
        // 根据当前菜单显示不同内容
        when (currentMenu) {
            MenuOption.ZONGMEN_OVERVIEW -> {
                // 掌门信息和快捷操作
                ZongmenLeaderInfoContent(textColor, sectionTitleColor, attributeColor, statusColor)
            }
            MenuOption.DISCIPLE_MANAGEMENT -> {
                // 弟子管理右侧信息
                DiscipleManagementRightContent(textColor, sectionTitleColor, attributeColor, statusColor, borderColor)
            }
            MenuOption.TECHNIQUE_HALL -> {
                // 功法堂右侧信息
                TechniqueHallRightContent(textColor, sectionTitleColor, attributeColor, statusColor, borderColor)
            }
            MenuOption.TASK_HALL -> {
                // 任务大厅右侧信息
                TaskHallRightContent(textColor, sectionTitleColor, attributeColor, statusColor, borderColor)
            }
            MenuOption.RESOURCE_MANAGEMENT -> {
                // 资源管理右侧信息
                ResourceManagementRightContent(textColor, sectionTitleColor, attributeColor, statusColor, borderColor)
            }
            MenuOption.MAP_EXPLORATION -> {
                // 地图探索右侧信息
                MapExplorationRightContent(textColor, sectionTitleColor, attributeColor, statusColor, borderColor)
            }
            MenuOption.FACILITY_CONSTRUCTION -> {
                // 设施建设右侧信息
                FacilityConstructionRightContent(textColor, sectionTitleColor, attributeColor, statusColor, borderColor)
            }
            MenuOption.ALCHEMY_ROOM -> {
                // 炼丹房右侧信息
                AlchemyRoomRightContent(textColor, sectionTitleColor, attributeColor, statusColor, borderColor)
            }
            MenuOption.FORGING_ROOM -> {
                // 炼器房右侧信息
                ForgingRoomRightContent(textColor, sectionTitleColor, attributeColor, statusColor, borderColor)
            }
            MenuOption.SOCIAL -> {
                // 社交右侧信息
                SocialRightContent(textColor, sectionTitleColor, attributeColor, statusColor, borderColor)
            }
            MenuOption.SETTINGS -> {
                // 设置右侧信息
                SettingsRightContent(textColor, sectionTitleColor, attributeColor, statusColor, borderColor)
            }
            else -> {
                // 角色信息
                CharacterInfoContent(textColor, sectionTitleColor, attributeColor, statusColor)
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
private fun ZongmenLeaderInfoContent(
    textColor: Color,
    sectionTitleColor: Color,
    attributeColor: Color,
    statusColor: Color
) {
    val borderColor = Color(0xFFBDBDBD)
    
    Column {
        // 掌门信息标题
        Text(
            text = "👤 掌门信息",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 掌门基本信息
        Text(
            text = "🔹 林玄风·金丹中期",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "💗 2850/2850 · 💠 1240/1800",
            color = attributeColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "⏳ 寿元：128年 · ✨ 贡献：2450",
            color = attributeColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 宗门快捷操作
        Text(
            text = "【宗门快捷操作】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[👥弟子管理] [📦资源管理] [🏗️设施建设] [🗺️地图探索]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[📋任务大厅] [📚功法堂] [🧪炼丹房] [⚒️炼器房]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 当前状态加成
        Text(
            text = "【当前状态加成】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "✅ 青灵殿+15% · ✅ 聚气丹+20%",
            color = statusColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "✅ 灵田+10% · ✅ 聚灵阵+5%",
            color = statusColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 宗门当前状态
        Text(
            text = "【宗门当前状态】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 运行状态：稳定 · 发展阶段：快速增长",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 在线弟子：89/128 · AI活跃：112/128",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 基础快捷操作
        Text(
            text = "【基础快捷操作】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "/搜索 | F1帮助 | S设置 | ESC菜单",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
    }
}

// 角色信息内容
@Composable
private fun CharacterInfoContent(
    textColor: Color,
    sectionTitleColor: Color,
    attributeColor: Color,
    statusColor: Color
) {
    val borderColor = Color(0xFFBDBDBD)
    
    Column {
        // 角色信息
        Text(
            text = "👤林玄风·金丹中期",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 角色属性
        Text(
            text = "💗2850/2850 💠1240/1800",
            color = attributeColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "⏳寿元：128年 · ✨贡献：2450",
            color = attributeColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "✅青灵殿+15% · ✅聚气丹+20%",
            color = statusColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 快捷操作
        Text(
            text = "【快捷操作】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "/搜索 | F1帮助",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "S设置 | ESC菜单",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 当前状态
        Text(
            text = "【当前状态】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "正在执行：任务大厅 · 效率：120%",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "操作中任务：0/5",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 状态加成
        Text(
            text = "【状态加成】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "✅青灵殿+15% · ✅聚气丹+20%",
            color = statusColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "✅灵田+10% · ✅聚灵阵+5%",
            color = statusColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

// 功法堂右侧信息内容
@Composable
private fun TechniqueHallRightContent(
    textColor: Color,
    sectionTitleColor: Color,
    attributeColor: Color,
    statusColor: Color,
    borderColor: Color
) {
    Column {
        // 功法统计
        Text(
            text = "📊 功法统计",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 统计信息
        Text(
            text = "🔹 功法总数：35种",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 已学习：12种",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 可学习：8种",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 功法快捷操作
        Text(
            text = "【功法快捷操作】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[📚功法列表] [📊功法统计] [🔄刷新] [📋学习记录]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[💡学习建议] [📤快速学习] [📥快速修炼]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 已学习功法
        Text(
            text = "【已学习功法】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 青灵心法 (木·灵级)",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 玄水诀 (水·灵级)",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 烈火掌 (火·凡级)",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 修炼中功法
        Text(
            text = "【修炼中功法】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 青冥剑诀 (金·天级) - 进度：35%",
            color = statusColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 玄铁防御诀 (土·地级) - 进度：65%",
            color = statusColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

// 任务大厅右侧信息内容
@Composable
private fun TaskHallRightContent(
    textColor: Color,
    sectionTitleColor: Color,
    attributeColor: Color,
    statusColor: Color,
    borderColor: Color
) {
    Column {
        // 任务统计
        Text(
            text = "📊 任务统计",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 统计信息
        Text(
            text = "🔹 已接任务：3/10",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 可接任务：12/20",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 完成任务：45/100",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 任务快捷操作
        Text(
            text = "【任务快捷操作】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[📋任务列表] [📊任务统计] [🔄刷新] [⏱️任务日志]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[💡任务建议] [📤快速接受] [📥快速完成]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 今日完成
        Text(
            text = "【今日完成】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 日常任务：2/5",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 委托任务：1/3",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 试炼任务：0/2",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 最新任务
        Text(
            text = "【最新任务】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔔 前往黑风林采集木材",
            color = statusColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔔 帮助张长老炼制丹药",
            color = statusColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔔 探索青灵山脉",
            color = statusColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

// 资源管理右侧信息内容
@Composable
private fun ResourceManagementRightContent(
    textColor: Color,
    sectionTitleColor: Color,
    attributeColor: Color,
    statusColor: Color,
    borderColor: Color
) {
    Column {
        // 仓库基本信息
        Text(
            text = "📦 宗门仓库",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 仓库信息
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
        
        // 仓库快捷操作
        Text(
            text = "【仓库快捷操作】",
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
        Text(
            text = "[💰交易] [🔄转换] [📋分配] [🔥消耗]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 资源流动
        Text(
            text = "【资源流动】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 今日收入：灵石+1200、灵草+250",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 今日支出：灵石-850、矿石-300",
            color = textColor,
            fontSize = 12.sp,
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
            text = "🔔 灵草×15 (1小时前)",
            color = statusColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔔 聚气丹×3 (2小时前)",
            color = statusColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔔 矿石×10 (3小时前)",
            color = statusColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

// 地图探索右侧信息内容
@Composable
private fun MapExplorationRightContent(
    textColor: Color,
    sectionTitleColor: Color,
    attributeColor: Color,
    statusColor: Color,
    borderColor: Color
) {
    Column {
        // 探索统计
        Text(
            text = "📊 探索统计",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 统计信息
        Text(
            text = "🔹 已探索区域：5个",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 已占领区域：3个",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 正在探索：2个",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 探索快捷操作
        Text(
            text = "【探索快捷操作】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[🔍探索] [🏰占领] [📊管理] [🔄刷新]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[📋任务] [⚔️试炼] [📦资源] [📈事件]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 探索建议
        Text(
            text = "【探索建议】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "💡 建议探索青灵山脉",
            color = statusColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "💡 建议占领黑风林",
            color = statusColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 探索队伍
        Text(
            text = "【探索队伍】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 探索队伍：2支",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 空闲队伍：3支",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

// 设施建设右侧信息内容
@Composable
private fun FacilityConstructionRightContent(
    textColor: Color,
    sectionTitleColor: Color,
    attributeColor: Color,
    statusColor: Color,
    borderColor: Color
) {
    // 添加缺失的颜色变量
    val warningColor = Color(0xFFFFC107)
    
    Column {
        // 设施统计
        Text(
            text = "📊 设施统计",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 统计信息
        Text(
            text = "🔹 设施总数：12/20",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 正在建设：2座",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 可升级：4座",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 设施快捷操作
        Text(
            text = "【设施快捷操作】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[🏗️新建设施] [📈批量升级] [🛠️批量维护]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[🔄刷新] [📋建设记录] [💡建设建议]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 建设队列
        Text(
            text = "【建设队列】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 队列：2/3",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "1. 炼丹房 (75%) - 45分钟",
            color = statusColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "2. 炼器阁 (35%) - 1小时30分钟",
            color = statusColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 升级建议
        Text(
            text = "【升级建议】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "💡 建议升级青灵殿至4级",
            color = warningColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "💡 建议扩建灵田至3级",
            color = warningColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 维护提醒
        Text(
            text = "【维护提醒】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "⚠️ 3座设施需要维护",
            color = warningColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 伐木场 - 效率85%",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 采矿场 - 效率75%",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 聚灵阵 - 效率90%",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

// 炼丹房右侧信息内容
@Composable
private fun AlchemyRoomRightContent(
    textColor: Color,
    sectionTitleColor: Color,
    attributeColor: Color,
    statusColor: Color,
    borderColor: Color
) {
    // 添加缺失的颜色变量
    val warningColor = Color(0xFFFFC107)
    
    Column {
        // 炼丹统计
        Text(
            text = "📊 炼丹统计",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 统计信息
        Text(
            text = "🔹 今日炼制：5次",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 成功率：85%",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 总炼制：128次",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 炼丹快捷操作
        Text(
            text = "【炼丹快捷操作】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[🧪开始炼制] [⏩加速炼制] [🔄刷新列表]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[📋炼制历史] [💡炼制建议] [📈炼丹统计]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 当前炼制
        Text(
            text = "【当前炼制】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔥 聚气丹 (二阶)",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 进度：65% | 剩余：45分钟",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 成功率：85% | 预计：5-8枚",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 材料消耗
        Text(
            text = "【材料消耗】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 灵草：10/1250",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 泉水：5/850",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 聚气草：3/120",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 炼制建议
        Text(
            text = "【炼制建议】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "💡 建议炼制回气丹",
            color = warningColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "💡 建议升级丹炉",
            color = warningColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

// 炼器房右侧信息内容
@Composable
private fun ForgingRoomRightContent(
    textColor: Color,
    sectionTitleColor: Color,
    attributeColor: Color,
    statusColor: Color,
    borderColor: Color
) {
    // 添加缺失的颜色变量
    val warningColor = Color(0xFFFFC107)
    
    Column {
        // 炼器统计
        Text(
            text = "📊 炼器统计",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 统计信息
        Text(
            text = "🔹 今日锻造：3次",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 成功率：75%",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 总锻造：89次",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 炼器快捷操作
        Text(
            text = "【炼器快捷操作】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[⚒️开始锻造] [⏩加速锻造] [🔄刷新列表]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[📋锻造历史] [💡锻造建议] [📈炼器统计]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 当前锻造
        Text(
            text = "【当前锻造】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔥 玄铁剑 (一阶)",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 进度：42% | 剩余：1小时15分钟",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 成功率：75% | 预计品质：精良",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 材料消耗
        Text(
            text = "【材料消耗】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 玄铁：15/150",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 铁矿石：10/280",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 木炭：5/120",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 锻造建议
        Text(
            text = "【锻造建议】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "💡 建议锻造玄铁护甲",
            color = warningColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "💡 建议升级熔炉",
            color = warningColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

// 社交右侧信息内容
@Composable
private fun SocialRightContent(
    textColor: Color,
    sectionTitleColor: Color,
    attributeColor: Color,
    statusColor: Color,
    borderColor: Color
) {
    // 添加缺失的颜色变量
    val successColor = Color(0xFF4CAF50)
    val warningColor = Color(0xFFFFC107)
    
    Column {
        // 社交统计
        Text(
            text = "📊 社交统计",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 统计信息
        Text(
            text = "🔹 友好宗门：2个",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 敌对宗门：2个",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 消息通知：5条",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 社交快捷操作
        Text(
            text = "【社交快捷操作】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[📧消息中心] [👥好友管理] [🏛️宗门关系]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[🎉活动报名] [💬发起聊天] [🎁赠送礼物]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 最近消息
        Text(
            text = "【最近消息】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔔 玄水宗使者来访",
            color = statusColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔔 弟子张无忌突破",
            color = statusColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔔 青风谷赠送灵草",
            color = statusColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 好友列表
        Text(
            text = "【好友列表】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "✅ 玄水宗掌门 (在线)",
            color = successColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "✅ 青风谷长老 (离线)",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "✅ 弟子张无忌 (在线)",
            color = successColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 社交建议
        Text(
            text = "【社交建议】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "💡 建议提升玄水宗友好度",
            color = warningColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "💡 建议参与宗门大比",
            color = warningColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

// 设置右侧信息内容
@Composable
private fun SettingsRightContent(
    textColor: Color,
    sectionTitleColor: Color,
    attributeColor: Color,
    statusColor: Color,
    borderColor: Color
) {
    Column {
        // 快捷设置
        Text(
            text = "⚡ 快捷设置",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 快捷设置选项
        Text(
            text = "🔹 游戏速度：【正常×1】[快速×2] [极速×3]",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 音效：【开启】[关闭]",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 自动存档：【开启】[关闭]",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 系统信息
        Text(
            text = "💻 系统信息",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 游戏版本：v1.2.3",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 系统：Windows 10 64位",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 内存：8GB | CPU：i7-8700K",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 帮助与支持
        Text(
            text = "❓ 帮助与支持",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[📖游戏指南] [🎯常见问题] [📧联系客服]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[🔔更新日志] [🔍反馈bug] [📝建议提交]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 关于游戏
        Text(
            text = "ℹ️ 关于游戏",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 游戏名称：LibGDX Kotlin Odyssey",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 开发者：QingYunZong Studio",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 版权所有 © 2025",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

// 弟子管理右侧信息内容
@Composable
private fun DiscipleManagementRightContent(
    textColor: Color,
    sectionTitleColor: Color,
    attributeColor: Color,
    statusColor: Color,
    borderColor: Color
) {
    // 添加缺失的颜色变量
    val successColor = Color(0xFF4CAF50)
    val warningColor = Color(0xFFFFC107)
    
    Column {
        // 弟子统计
        Text(
            text = "📊 弟子统计",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 统计信息
        Text(
            text = "🔹 弟子总数：128/200",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 在线弟子：89/128",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 可招收：72人",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 弟子快捷操作
        Text(
            text = "【弟子快捷操作】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[👥招收弟子] [📋批量管理] [📚批量培养]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "[🔄刷新列表] [📈弟子统计] [💡培养建议]",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 在线弟子
        Text(
            text = "【在线弟子】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "✅ 张无忌 (筑基中期)",
            color = successColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "✅ 赵敏 (炼气后期)",
            color = successColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "✅ 杨逍 (金丹初期)",
            color = successColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 最新招收
        Text(
            text = "【最新招收】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 小昭 (炼气中期) - 2小时前",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 周芷若 (筑基初期) - 5小时前",
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        
        // 分隔线
        Text(text = "────────────────────────────", color = borderColor, fontSize = 12.sp)
        
        // 培养建议
        Text(
            text = "【培养建议】",
            color = sectionTitleColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "💡 建议培养张无忌至金丹期",
            color = warningColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "💡 建议招收更多高资质弟子",
            color = warningColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
    }
}