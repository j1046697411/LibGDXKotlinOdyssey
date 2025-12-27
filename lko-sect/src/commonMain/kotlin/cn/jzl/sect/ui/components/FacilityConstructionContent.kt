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

/**
 * 设施建设内容组件
 * 
 * 显示设施建设的详细信息和操作界面，包括：
 * 1. 设施统计（总数、建设状态等）
 * 2. 设施列表与筛选
 * 3. 设施详情
 * 4. 建设队列管理
 * 5. 升级建议
 */
@Composable
fun FacilityConstructionContent() {
    TerminalCard(
        title = "【🏗️ 设施建设】",
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // 设施统计
            Text(
                text = "【设施统计】",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 设施总数：12/20 | 核心建筑：1 | 功能性：5 | 生产性：4 | 防御性：2", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 已建设：12座 | 正在建设：2座 | 待建设：6座 | 可升级：4座", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            TerminalDivider()

            // 设施分类与筛选
            Text(
                text = "【设施分类与筛选】",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 分类：【全部】【核心建筑】【功能性】【生产性】【防御性】【装饰性】", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 筛选：【等级】【类型】【状态】【效率】【维护成本】", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 操作：[新建设施] [批量升级] [批量维护] [刷新列表] [导出数据]", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            TerminalDivider()

            // 设施列表
            Text(
                text = "【设施列表】",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // 设施列表表格
            val facilityHeaders = listOf("设施名称", "类型", "等级", "状态", "效率", "维护成本", "产出", "升级条件", "操作")
            val facilityRows = listOf(
                listOf("青灵殿", "核心建筑", "3", "正常", "100%", "灵石-500/天", "声望+15%", "可升级", "[升级] [维护] [详情]"),
                listOf("灵田", "生产性", "2", "正常", "120%", "灵石-200/天", "灵草+150/天", "可升级", "[升级] [维护] [详情]"),
                listOf("弟子房", "功能性", "2", "正常", "90%", "灵石-150/天", "弟子上限+20", "可升级", "[升级] [维护] [详情]"),
                listOf("伐木场", "生产性", "1", "正常", "85%", "灵石-100/天", "木材+80/天", "可升级", "[升级] [维护] [详情]"),
                listOf("聚灵阵", "功能性", "1", "正常", "100%", "灵石-300/天", "修炼速度+10%", "可升级", "[升级] [维护] [详情]")
            )
            TerminalTable(headers = facilityHeaders, rows = facilityRows, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(8.dp))
            TerminalDivider()

            // 建设队列
            Text(
                text = "【建设队列】",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 建设队列：2/3 | 剩余时间：2小时15分钟", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(8.dp)) {
                Text(text = "1. 炼丹房（2级） - 建设中 75% | 剩余时间：45分钟 | [加速] [取消]", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(8.dp)) {
                Text(text = "2. 炼器阁（1级） - 建设中 35% | 剩余时间：1小时30分钟 | [加速] [取消]", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
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
                    text = "▶ 设施列表     ▶ 建设队列     ▶ 升级管理     ▶ 维护记录     ▶ 建设建议",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}