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


// 功法堂内容
@Composable
fun TechniqueHallContent() {
    val sectionTitleColor = Color(0xFF2196F3)
    val textColor = Color(0xFF212121)
    val highlightColor = Color(0xFFFF9800)
    val borderColor = Color(0xFFBDBDBD)
    val warningColor = Color(0xFFFFC107)
    val successColor = Color(0xFF4CAF50)

    Column {
        Text(
            text = "【📚 功法堂】",
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

        // 功法筛选
        Text(
            text = "【功法筛选】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 属性：【全部】【金】【木】【水】【火】【土】【风】【雷】", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 类型：【修炼】【攻伐】【防御】【辅助】【特殊】", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 等级：【凡级】【灵级】【地级】【天级】【神级】", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 状态：【可学习】【已学习】【修炼中】【已掌握】", color = textColor, fontSize = 13.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 功法列表
        Text(
            text = "【功法列表】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // 天级功法
        Text(
            text = "⭐ 【金·天级】青冥剑诀",
            color = Color(0xFFFFD700),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "   类型：攻伐 | 要求：金丹期以上 | 消耗：贡献5000, 灵石10000", color = textColor, fontSize = 12.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "   效果：攻击力+300, 金系伤害+50% | 状态：可学习 | [学习] [查看详情] [放弃]", color = textColor, fontSize = 12.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 灵级功法 - 已学习
        Text(
            text = "🔵 【木·灵级】青灵心法",
            color = Color(0xFF2196F3),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "   类型：修炼 | 要求：筑基期以上 | 消耗：贡献1500, 灵石2000", color = textColor, fontSize = 12.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "   效果：修炼速度+20%, 木系亲和力+30% | 状态：已学习 | [修炼] [查看详情] [传授]", color = textColor, fontSize = 12.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 灵级功法 - 修炼中
        Text(
            text = "🟡 【水·灵级】玄水诀",
            color = Color(0xFFFFC107),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "   类型：防御 | 要求：筑基期以上 | 消耗：贡献1800, 灵石2500", color = textColor, fontSize = 12.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "   效果：防御力+250, 水系抗性+40% | 状态：修炼中 | [继续] [加速] [放弃]", color = textColor, fontSize = 12.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 凡级功法 - 已掌握
        Text(
            text = "🔸 【火·凡级】烈火掌",
            color = Color(0xFFF44336),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "   类型：攻伐 | 要求：炼气期以上 | 消耗：贡献500, 灵石500", color = textColor, fontSize = 12.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "   效果：攻击力+100, 火系伤害+20% | 状态：已掌握 | [查看详情] [传授]", color = textColor, fontSize = 12.sp)
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
                text = "▶ 功法列表     ▶ 功法详情     ▶ 学习记录     ▶ 修炼进度     ▶ 传承管理",
                color = highlightColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
