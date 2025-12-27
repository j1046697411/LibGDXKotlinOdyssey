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
 * 资源管理内容组件
 * 
 * 显示资源管理的详细信息和操作界面，包括：
 * 1. 仓库基本信息（容量、物品总数等）
 * 2. 资源分类与筛选
 * 3. 资源列表表格
 * 4. 资源详情
 * 5. 资源流动记录
 */
@Composable
fun InventoryManagementContent() {
    TerminalCard(
        title = "【📦 资源管理】",
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {

            // 资源统计
        Text(
            text = "【资源统计】",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 灵石：25000/100000 · 贡献点：15600/30000 · 声望：8500/10000", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 灵草：1250/2000 · 矿石：1500/3000 · 木材：2800/5000 · 泉水：850/2000", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
        TerminalDivider()

        // 资源分类与筛选
        Text(
            text = "【资源分类与筛选】",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 分类：【全部】【基础资源】【高级资源】【丹药】【装备】【功法】", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 筛选：【数量】【类型】【品质】【用途】【获取方式】", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 操作：[分配资源] [资源交易] [资源转换] [刷新列表] [导出数据]", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
        TerminalDivider()

        // 资源列表
        Text(
            text = "【资源列表】",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // 资源列表表格
        val resourceHeaders = listOf("资源名称", "类型", "当前数量", "最大容量", "品质", "用途", "价值", "状态")
        val resourceRows = listOf(
            listOf("灵石", "货币", "25000", "100000", "普通", "交易", "1", "充足"),
            listOf("灵草", "材料", "1250", "2000", "普通", "炼丹", "10", "充足"),
            listOf("矿石", "材料", "1500", "3000", "普通", "炼器", "15", "充足"),
            listOf("木材", "材料", "2800", "5000", "普通", "建筑", "8", "充足"),
            listOf("泉水", "材料", "850", "2000", "普通", "生活", "5", "充足"),
            listOf("回气丹", "丹药", "12", "100", "普通", "恢复", "20", "充足"),
            listOf("疗伤丹", "丹药", "8", "100", "普通", "恢复", "25", "充足"),
            listOf("聚气丹", "丹药", "5", "50", "稀有", "修炼", "80", "有限"),
            listOf("筑基丹", "丹药", "2", "30", "稀有", "突破", "300", "稀少")
        )
        TerminalTable(headers = resourceHeaders, rows = resourceRows, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(8.dp))
        TerminalDivider()

        // 资源流动日志
        Text(
            text = "【资源流动日志】",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 [14:25] 灵草+15 (灵田产出) · [14:15] 矿石-300 (炼器房消耗) · [14:00] 灵石+1200 (区域税收)", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
        TerminalDivider()

        // 资源分配建议
        Text(
            text = "【资源分配建议】",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "💡 建议分配5000灵石用于升级青灵殿 · 💡 建议分配300灵草用于炼制聚气丹 · 💡 建议分配500矿石用于打造装备", color = MaterialTheme.colorScheme.secondary, fontSize = 13.sp)
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
                    text = "▶ 资源概览     ▶ 仓库管理     ▶ 资源流动     ▶ 交易记录     ▶ 分配记录",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
