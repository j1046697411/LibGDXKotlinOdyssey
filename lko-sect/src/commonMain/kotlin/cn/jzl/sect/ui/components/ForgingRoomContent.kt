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

// 炼器房内容
@Composable
fun ForgingRoomContent() {
    val sectionTitleColor = Color(0xFF2196F3)
    val textColor = Color(0xFF212121)
    val highlightColor = Color(0xFFFF9800)
    val borderColor = Color(0xFFBDBDBD)
    val warningColor = Color(0xFFFFC107)
    val successColor = Color(0xFF4CAF50)

    Column {
        Text(
            text = "【⚒️ 炼器房】",
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

        // 熔炉与炼器师信息
        Text(
            text = "【熔炉与炼器师信息】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 当前熔炉：一阶玄铁炉 | 品质：普通 | 成功率+10% | 效率+15%", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 炼器师：张无忌 (筑基中期) | 炼器等级：3级 | 熟练度：72% | 擅长：金系武器", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 熔炉数量：2座 | 正在使用：1座 | 空闲：1座 | 可升级：1座", color = textColor, fontSize = 13.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 当前锻造
        Text(
            text = "【当前锻造】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = "🔥 玄铁剑 (一阶) | 锻造进度：42% | 剩余时间：1小时15分钟 | 预计品质：精良", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = "📋 材料消耗：玄铁×15, 铁矿石×10, 木炭×5 | 成功率：75% | [加速] [取消] [查看详情]", color = textColor, fontSize = 13.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 装备列表与筛选
        Text(
            text = "【装备列表与筛选】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 分类：【全部】【武器】【防具】【饰品】【一阶】【二阶】【三阶】", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 筛选：【类型】【品质】【材料】【成功率】【锻造时间】", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 操作：[开始锻造] [批量锻造] [刷新列表] [导出数据]", color = textColor, fontSize = 13.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 装备列表表格
        Text(
            text = "【装备列表】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // 表格
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "┌─────────┬──────┬──────┬──────┬──────┬────────┬────────┬────────┬────────┐", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "│ 装备名称 │ 类型 │ 等级 │ 品质 │ 效果 │ 成功率 │ 锻造时间 │ 材料消耗 │ 操作   │", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "├─────────┼──────┼──────┼──────┼──────┼────────┼────────┼────────┼────────┤", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "│ 玄铁剑  │ 武器 │ 一阶 │ 精良 │ 攻击力+120 │ 75%  │ 2小时   │ 玄铁×15,铁矿石×10 │ [开始锻造] │", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "│ 玄铁护甲 │ 防具 │ 一阶 │ 普通 │ 防御力+80 │ 85%  │ 1.5小时 │ 玄铁×12,铁矿石×8 │ [开始锻造] │", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "│ 聚气戒指 │ 饰品 │ 二阶 │ 稀有 │ 修炼速度+15% │ 60%  │ 3小时   │ 玄铁×20,灵石×50 │ [开始锻造] │", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "│ 青灵剑  │ 武器 │ 二阶 │ 稀有 │ 攻击力+200,木系伤害+15% │ 55%  │ 4小时   │ 青灵木×15,玄铁×10 │ [开始锻造] │", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "└─────────┴──────┴──────┴──────┴──────┴────────┴────────┴────────┴────────┘", color = textColor, fontSize = 13.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 锻造历史
        Text(
            text = "【锻造历史】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = "✅ 玄铁护甲 (一阶) | 锻造完成 | 品质：普通 | 耗时：1小时45分钟 | 3小时前", color = successColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = "✅ 聚气戒指 (二阶) | 锻造完成 | 品质：稀有 | 耗时：2小时58分钟 | 8小时前", color = successColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = "❌ 青灵剑 (二阶) | 锻造失败 | 材料损失：100% | 1天前", color = warningColor, fontSize = 13.sp)
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
                text = "▶ 熔炉管理     ▶ 装备列表     ▶ 锻造历史     ▶ 炼器师管理     ▶ 锻造建议",
                color = highlightColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
