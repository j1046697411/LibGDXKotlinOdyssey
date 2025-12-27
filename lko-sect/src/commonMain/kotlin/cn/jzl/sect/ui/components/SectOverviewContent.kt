package cn.jzl.sect.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// 宗门总览内容
@Composable
fun SectOverviewContent() {
    Column {
        // 标题
        Text(
            text = "【🏠 宗门总览】",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TerminalDivider(modifier = Modifier.fillMaxWidth())

        // 宗门基本信息
        TerminalCard(
            title = "宗门基本信息",
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column {
                Row(modifier = Modifier.padding(4.dp)) {
                    Text(
                        text = "🔸 宗门名称：青云宗",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "🔸 宗门等级：三阶",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.padding(4.dp)) {
                    Text(
                        text = "🔸 宗门类型：灵草专精",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "🔸 创建时间：修真纪元120年·5月",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.padding(4.dp)) {
                    Text(
                        text = "🔸 宗门声望：8500 (地区知名)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "🔸 稳定度：92% (非常稳定)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
                Text(
                    text = "🔸 发展趋势：快速增长",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        // 核心数据统计
        TerminalCard(
            title = "核心数据统计",
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            val statsHeaders = listOf("弟子总数", "灵石储备", "设施数量", "占领区域")
            val statsRows = listOf(
                listOf("128/200", "25000/100000", "12/20", "5/10"),
                listOf("15600/30000", "85", "38", "4")
            )
            TerminalTable(headers = statsHeaders, rows = statsRows, modifier = Modifier.fillMaxWidth())
        }

        // 宗门影响力
        TerminalCard(
            title = "宗门影响力",
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column {
                Text(
                    text = "🔹 地区影响力：8500 (地区知名宗门)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "🔹 友好宗门：玄水阁、清风派",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "🔹 敌对宗门：血魔宗、鬼阴门",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        // 核心设施状态
        TerminalCard(
            title = "核心设施状态",
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column {
                Text(
                    text = "🏛️ 青灵殿 (三阶)：宗门核心，声望+15%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "🌾 灵田 (二阶)：灵草产量+20%，当前产出：150/小时",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "🏭 炼丹房 (二阶)：丹药炼制成功率+15%，当前正在炼制：聚气丹×5",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "🏭 炼器阁 (一阶)：装备打造成功率+10%，当前空闲",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        // 近期动态
        TerminalCard(
            title = "近期动态",
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column {
                Text(
                    text = "🔔 弟子张无忌突破至筑基中期",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "🔔 千绝谷灵草产量增加15%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "🔔 新弟子报名：12人",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "🔔 玄水阁使者来访",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        // 发展建议
        TerminalCard(
            title = "发展建议",
            borderColor = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column {
                Text(
                    text = "💡 建议升级青灵殿至三阶，提升宗门声望上限至15000",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "💡 建议扩建灵田至三阶，增加灵草产量",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "💡 建议招募更多金丹期弟子，提升宗门战斗力",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        // 功能标签页
        TerminalCard(
            title = "功能标签页",
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                Text(
                    text = "▶ 宗门概览     ▶ 发展趋势     ▶ 影响力分析     ▶ 事件记录     ▶ 设施管理",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
