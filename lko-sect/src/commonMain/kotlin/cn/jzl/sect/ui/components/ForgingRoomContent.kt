package cn.jzl.sect.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 炼器房内容组件
 * 
 * 显示炼器房的详细信息和操作界面，包括：
 * 1. 熔炉与炼器师信息
 * 2. 当前锻造进度
 * 3. 装备列表与筛选
 * 4. 装备列表表格
 * 5. 锻造历史记录
 * 6. 功能标签页
 */
@Composable
fun ForgingRoomContent() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 标题
        Text(
            text = "【⚒️ 炼器房】",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // 熔炉与炼器师信息
        TerminalCard(title = "熔炉与炼器师信息") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🔹 当前熔炉：一阶玄铁炉 | 品质：普通 | 成功率+10% | 效率+15%")
                Text("🔹 炼器师：张无忌 (筑基中期) | 炼器等级：3级 | 熟练度：72% | 擅长：金系武器")
                Text("🔹 熔炉数量：2座 | 正在使用：1座 | 空闲：1座 | 可升级：1座")
            }
        }

        // 当前锻造
        TerminalCard(title = "当前锻造") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🔥 玄铁剑 (一阶) | 锻造进度：42% | 剩余时间：1小时15分钟 | 预计品质：精良")
                Text("📋 材料消耗：玄铁×15, 铁矿石×10, 木炭×5 | 成功率：75% | [加速] [取消] [查看详情]")
            }
        }

        // 装备列表与筛选
        TerminalCard(title = "装备列表与筛选") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🔹 分类：【全部】【武器】【防具】【饰品】【一阶】【二阶】【三阶】")
                Text("🔹 筛选：【类型】【品质】【材料】【成功率】【锻造时间】")
                Text("🔹 操作：[开始锻造] [批量锻造] [刷新列表] [导出数据]")
            }
        }

        // 装备列表表格
        val equipmentHeaders = listOf("装备名称", "类型", "等级", "品质", "效果", "成功率", "锻造时间", "材料消耗", "操作")
        val equipmentRows = listOf(
            listOf("玄铁剑", "武器", "一阶", "精良", "攻击力+120", "75%", "2小时", "玄铁×15,铁矿石×10", "[开始锻造]"),
            listOf("玄铁护甲", "防具", "一阶", "普通", "防御力+80", "85%", "1.5小时", "玄铁×12,铁矿石×8", "[开始锻造]"),
            listOf("聚气戒指", "饰品", "二阶", "稀有", "修炼速度+15%", "60%", "3小时", "玄铁×20,灵石×50", "[开始锻造]"),
            listOf("青灵剑", "武器", "二阶", "稀有", "攻击力+200,木系伤害+15%", "55%", "4小时", "青灵木×15,玄铁×10", "[开始锻造]")
        )
        TerminalTable(headers = equipmentHeaders, rows = equipmentRows, modifier = Modifier.fillMaxWidth())

        // 锻造历史
        TerminalCard(title = "锻造历史") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "✅ 玄铁护甲 (一阶) | 锻造完成 | 品质：普通 | 耗时：1小时45分钟 | 3小时前",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "✅ 聚气戒指 (二阶) | 锻造完成 | 品质：稀有 | 耗时：2小时58分钟 | 8小时前",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "❌ 青灵剑 (二阶) | 锻造失败 | 材料损失：100% | 1天前",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        // 功能标签页
        TerminalCard(title = "功能标签页") {
            Text(
                text = "▶ 熔炉管理     ▶ 装备列表     ▶ 锻造历史     ▶ 炼器师管理     ▶ 锻造建议",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
