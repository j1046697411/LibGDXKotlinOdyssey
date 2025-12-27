package cn.jzl.sect.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 地图探索内容组件
 * 
 * 显示地图探索的详细信息和操作界面，包括：
 * 1. 地图区域列表
 * 2. 已探索区域详情
 * 3. 探索队伍管理
 * 4. 探索任务列表
 * 5. 资源分布信息
 */
@Composable
fun MapExplorationContent() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 标题
        Text(
            text = "【🗺️ 地图探索】",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // 区域统计
        TerminalCard(title = "区域统计") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🔹 总区域数：20 | 已探索：5 | 已占领：3 | 可探索：8 | 未探索：7")
                Text("🔹 大区域：5 | 小区域：15")
            }
        }

        // 区域分类与筛选
        TerminalCard(title = "区域分类与筛选") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🔹 分类：【全部】【大区域】【小区域】【灵脉】【古战场】【秘境】【妖兽森林】【凡人国度】")
                Text("🔹 筛选：【难度】【资源】【状态】【类型】【距离】")
                Text("🔹 操作：[探索区域] [占领区域] [管理队伍] [刷新列表] [导出数据]")
            }
        }

        // 区域列表
        val regionHeaders = listOf("区域名称", "类型", "等级", "状态", "资源", "难度", "距离", "占领度", "探索度")
        val regionRows = listOf(
            listOf("千绝谷", "小区域", "2", "已占领", "灵草+15%", "低", "0km", "100%", "100%"),
            listOf("黑风林", "小区域", "1", "已探索", "木材+20%", "低", "5km", "0%", "100%"),
            listOf("青灵山脉", "大区域", "3", "未探索", "未知资源", "高", "20km", "0%", "0%"),
            listOf("古战场", "大区域", "4", "未探索", "古物+功法", "极高", "50km", "0%", "0%"),
            listOf("玄水湖", "小区域", "2", "已探索", "泉水+25%", "中", "10km", "0%", "100%"),
            listOf("妖兽森林", "大区域", "2", "已探索", "妖兽材料", "中", "15km", "0%", "80%"),
            listOf("凡人国度", "大区域", "1", "已占领", "基础资源", "低", "30km", "100%", "100%"),
            listOf("秘境入口", "大区域", "5", "未探索", "秘境特产", "极高", "100km", "0%", "0%")
        )
        TerminalTable(headers = regionHeaders, rows = regionRows, modifier = Modifier.fillMaxWidth())

        // 区域详情
        TerminalCard(title = "区域详情") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🔹 名称：千绝谷 | 类型：小区域 | 等级：2 | 状态：已占领")
                Text("🔹 资源产出：灵草+15% | 木材+5% | 每年产出：灵草1500、木材800")
                Text("🔹 难度：低 | 距离：0km | 占领度：100% | 探索度：100%")
                Text("🔹 特殊事件：灵草成熟 (10天后)")
            }
        }

        // 功能标签页
        TerminalCard(title = "功能标签页") {
            Text(
                text = "▶ 区域列表     ▶ 区域详情     ▶ 探索队伍     ▶ 占领管理     ▶ 事件记录",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
