package cn.jzl.sect.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 宗门总览内容
@Composable
fun SectOverviewContent() {
    val sectionTitleColor = Color(0xFF2196F3)
    val textColor = Color(0xFF212121)
    val highlightColor = Color(0xFFFF9800)
    val successColor = Color(0xFF4CAF50)
    val warningColor = Color(0xFFFFC107)
    val borderColor = Color(0xFFBDBDBD)

    Column {
        // 标题
        Text(
            text = "【🏠 宗门总览】",
            color = sectionTitleColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Divider(color = borderColor, modifier = Modifier.fillMaxWidth())

        // 宗门基本信息
        Text(
            text = "【宗门基本信息】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔸 宗门名称：青云宗", color = textColor, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Text(text = "🔸 宗门等级：三阶", color = textColor, fontSize = 13.sp, modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔸 宗门类型：灵草专精", color = textColor, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Text(text = "🔸 创建时间：修真纪元120年·5月", color = textColor, fontSize = 13.sp, modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔸 宗门声望：8500 (地区知名)", color = textColor, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Text(text = "🔸 稳定度：92% (非常稳定)", color = textColor, fontSize = 13.sp, modifier = Modifier.weight(1f))
        }
        Text(
            text = "🔸 发展趋势：快速增长",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Divider(color = borderColor, modifier = Modifier.fillMaxWidth())
        // 核心数据统计
        Text(
            text = "【核心数据统计】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Column(modifier = Modifier.padding(8.dp)) {
            // 表格第一行
            Row(modifier = Modifier.padding(2.dp)) {
                Text(text = "┌────────────┬────────────┬────────────┬────────────┐", color = textColor, fontSize = 13.sp)
            }
            // 表格标题行
            Row(modifier = Modifier.padding(2.dp)) {
                Text(text = "│ 弟子总数   │ 灵石储备   │ 设施数量   │ 占领区域   │", color = textColor, fontSize = 13.sp)
            }
            // 表格数据行1
            Row(modifier = Modifier.padding(2.dp)) {
                Text(text = "│ 128/200    │ 25000/100000│ 12/20      │ 5/10       │", color = textColor, fontSize = 13.sp)
            }
            // 表格分隔线
            Row(modifier = Modifier.padding(2.dp)) {
                Text(text = "├────────────┼────────────┼────────────┼────────────┤", color = textColor, fontSize = 13.sp)
            }
            // 表格标题行2
            Row(modifier = Modifier.padding(2.dp)) {
                Text(text = "│ 贡献总量   │ 炼气期     │ 筑基期     │ 金丹期     │", color = textColor, fontSize = 13.sp)
            }
            // 表格数据行2
            Row(modifier = Modifier.padding(2.dp)) {
                Text(text = "│ 15600/30000│ 85         │ 38         │ 4          │", color = textColor, fontSize = 13.sp)
            }
            // 表格结束行
            Row(modifier = Modifier.padding(2.dp)) {
                Text(text = "└────────────┴────────────┴────────────┴────────────┘", color = textColor, fontSize = 13.sp)
            }
        }
        Divider(color = borderColor, modifier = Modifier.fillMaxWidth())
        // 宗门影响力
        Text(
            text = "【宗门影响力】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = "🔹 地区影响力：8500 (地区知名宗门)",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 友好宗门：玄水阁、清风派",
            color = successColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔹 敌对宗门：血魔宗、鬼阴门",
            color = warningColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 核心设施状态
        Text(
            text = "【核心设施状态】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = "🏛️ 青灵殿 (三阶)：宗门核心，声望+15%",
            color = highlightColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🌾 灵田 (二阶)：灵草产量+20%，当前产出：150/小时",
            color = successColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🏭 炼丹房 (二阶)：丹药炼制成功率+15%，当前正在炼制：聚气丹×5",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🏭 炼器阁 (一阶)：装备打造成功率+10%，当前空闲",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )

        Divider(color = borderColor, modifier = Modifier.fillMaxWidth())
        // 近期动态
        Text(
            text = "【近期动态】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = "🔔 弟子张无忌突破至筑基中期",
            color = highlightColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔔 千绝谷灵草产量增加15%",
            color = successColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔔 新弟子报名：12人",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "🔔 玄水阁使者来访",
            color = textColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 发展建议
        Text(
            text = "【发展建议】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = "💡 建议升级青灵殿至三阶，提升宗门声望上限至15000",
            color = warningColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "💡 建议扩建灵田至三阶，增加灵草产量",
            color = warningColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = "💡 建议招募更多金丹期弟子，提升宗门战斗力",
            color = warningColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(4.dp)
        )
        Divider(color = borderColor, modifier = Modifier.fillMaxWidth())
        // 功能标签页
        Text(
            text = "【功能标签页】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(
                text = "▶ 宗门概览     ▶ 发展趋势     ▶ 影响力分析     ▶ 事件记录     ▶ 设施管理",
                color = highlightColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
