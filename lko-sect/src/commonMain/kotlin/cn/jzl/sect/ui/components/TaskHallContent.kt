package cn.jzl.sect.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 任务大厅内容
@Composable
fun TaskHallContent() {
    val sectionTitleColor = Color(0xFF2196F3)
    val textColor = Color(0xFF212121)
    val highlightColor = Color(0xFFFF9800)
    val borderColor = Color(0xFFBDBDBD)
    val warningColor = Color(0xFFFFC107)
    val successColor = Color(0xFF4CAF50)

    val taskTypeColors = mapOf(
        "主线" to Color(0xFFFFEB3B),
        "日常" to Color(0xFF4CAF50),
        "委托" to Color(0xFF9C27B0),
        "试炼" to Color(0xFFFF9800)
    )

    Column {
        Text(
            text = "【📋 任务大厅】",
            color = sectionTitleColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 任务筛选
        Text(
            text = "【任务筛选】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 类型：【主线】【日常】【委托】【试炼】【宗门】【个人】", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 难度：【无要求】【炼气】【筑基】【金丹】【元婴】【化神】", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 状态：【可接受】【进行中】【已完成】【已放弃】【已过期】", color = textColor, fontSize = 13.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 任务列表
        Text(
            text = "【任务列表】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // 主线任务
        Text(
            text = "⭐ 【主线】前往千绝谷采集灵草 (筑基)",
            color = taskTypeColors["主线"]!!,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "   要求：修为≥筑基中期 | 奖励：贡献500, 筑基丹×1", color = textColor, fontSize = 12.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "   状态：可接受 | [接受] [放弃] [查看详情]", color = textColor, fontSize = 12.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 日常任务
        Text(
            text = "◆ 【日常】宗门巡逻 (无要求)",
            color = taskTypeColors["日常"]!!,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "   要求：无 | 奖励：贡献100, 灵石×50", color = textColor, fontSize = 12.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "   状态：可接受 | [接受] [放弃] [查看详情]", color = textColor, fontSize = 12.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 委托任务
        Text(
            text = "▲ 【委托】帮李长老寻找丢失的玉简 (筑基)",
            color = taskTypeColors["委托"]!!,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "   要求：神识≥200 | 奖励：贡献300, 低级功法×1", color = textColor, fontSize = 12.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "   状态：进行中 | [放弃] [查看详情] [加速]", color = textColor, fontSize = 12.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 试炼任务
        Text(
            text = "▶ 【试炼】黑风林妖兽猎杀 (炼气)",
            color = taskTypeColors["试炼"]!!,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "   要求：炼气期以上 | 奖励：贡献200, 妖兽材料×5", color = textColor, fontSize = 12.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "   状态：已完成 | [领取奖励]", color = textColor, fontSize = 12.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

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
                text = "▶ 任务列表     ▶ 任务详情     ▶ 任务日志     ▶ 任务统计     ▶ 任务建议",
                color = highlightColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
