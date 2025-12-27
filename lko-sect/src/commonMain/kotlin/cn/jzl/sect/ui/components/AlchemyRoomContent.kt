package cn.jzl.sect.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 炼丹房内容
@Composable
fun AlchemyRoomContent() {
    TerminalCard(
        title = "【🧪 炼丹房】",
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // 丹炉与炼丹师信息
            Text(
                text = "【丹炉与炼丹师信息】",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 当前丹炉：二阶青灵炉 | 品质：精良 | 成功率+15% | 效率+20%", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 炼丹师：林玄风 (金丹中期) | 炼丹等级：4级 | 熟练度：85% | 擅长：金系丹药", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 丹炉数量：3座 | 正在使用：1座 | 空闲：2座 | 可升级：1座", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            TerminalDivider()

            // 当前炼制
            Text(
                text = "【当前炼制】",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(modifier = Modifier.padding(8.dp)) {
                Text(text = "🔥 聚气丹 (二阶) | 炼制进度：65% | 剩余时间：45分钟 | 预计产量：5枚", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(8.dp)) {
                Text(text = "📋 材料消耗：灵草×10, 泉水×5, 聚气草×3 | 成功率：85% | [加速] [取消] [查看详情]", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            TerminalDivider()

            // 丹药列表与筛选
            Text(
                text = "【丹药列表与筛选】",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 分类：【全部】【一阶】【二阶】【三阶】【四阶】【五阶】", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 筛选：【类型】【效果】【材料】【成功率】【炼制时间】", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 操作：[开始炼制] [批量炼制] [刷新列表] [导出数据]", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            TerminalDivider()

            // 丹药列表表格
            Text(
                text = "【丹药列表】",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // 使用新的TerminalTable组件
            val headers = listOf("丹药名称", "等级", "类型", "效果", "成功率", "炼制时间", "材料消耗", "产量", "操作")
            val rows = listOf(
                listOf("聚气丹", "二阶", "修炼", "修炼速度+20%", "85%", "2小时", "灵草×10,泉水×5", "5-8枚", "[开始炼制]"),
                listOf("回气丹", "一阶", "恢复", "恢复气血+150", "95%", "1小时", "灵草×5,泉水×3", "8-12枚", "[开始炼制]"),
                listOf("疗伤丹", "一阶", "恢复", "恢复气血+250", "90%", "1.5小时", "灵草×8,泉水×4", "6-10枚", "[开始炼制]"),
                listOf("筑基丹", "三阶", "突破", "突破筑基成功率+30%", "65%", "4小时", "灵草×20,聚气草×10", "3-5枚", "[开始炼制]")
            )
            TerminalTable(
                headers = headers,
                rows = rows,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            TerminalDivider()

            // 炼制历史
            Text(
                text = "【炼制历史】",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(modifier = Modifier.padding(8.dp)) {
                Text(text = "✅ 聚气丹 (二阶) | 炼制完成 | 产量：6枚 | 耗时：1小时55分钟 | 2小时前", color = MaterialTheme.colorScheme.secondary, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(8.dp)) {
                Text(text = "✅ 回气丹 (一阶) | 炼制完成 | 产量：10枚 | 耗时：58分钟 | 5小时前", color = MaterialTheme.colorScheme.secondary, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(8.dp)) {
                Text(text = "❌ 筑基丹 (三阶) | 炼制失败 | 材料损失：100% | 1天前", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            TerminalDivider()

            // 功能标签页
            Text(
                text = "【功能标签页】",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(modifier = Modifier.padding(4.dp)) {
                Text(
                    text = "▶ 丹炉管理     ▶ 丹药列表     ▶ 炼制历史     ▶ 炼丹师管理     ▶ 炼制建议",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}