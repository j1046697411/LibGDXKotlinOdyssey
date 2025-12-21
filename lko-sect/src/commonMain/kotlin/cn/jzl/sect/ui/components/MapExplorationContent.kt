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

// 地图探索内容
@Composable
fun MapExplorationContent() {
    val sectionTitleColor = Color(0xFF2196F3)
    val textColor = Color(0xFF212121)
    val highlightColor = Color(0xFFFF9800)
    val borderColor = Color(0xFFBDBDBD)
    val warningColor = Color(0xFFFFC107)
    val successColor = Color(0xFF4CAF50)

    Column {
        Text(
            text = "【🗺️ 地图探索】",
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

        // 区域统计
        Text(
            text = "【区域统计】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 总区域数：20 | 已探索：5 | 已占领：3 | 可探索：8 | 未探索：7", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 大区域：5 | 小区域：15", color = textColor, fontSize = 13.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 区域分类与筛选
        Text(
            text = "【区域分类与筛选】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 分类：【全部】【大区域】【小区域】【灵脉】【古战场】【秘境】【妖兽森林】【凡人国度】", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 筛选：【难度】【资源】【状态】【类型】【距离】", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 操作：[探索区域] [占领区域] [管理队伍] [刷新列表] [导出数据]", color = textColor, fontSize = 13.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 区域列表
        Text(
            text = "【区域列表】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // 区域列表表格
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "┌─────────┬──────┬──────┬──────┬──────┬──────┬────────┬────────┬────────┐", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "│ 区域名称 │ 类型 │ 等级 │ 状态 │ 资源 │ 难度 │ 距离   │ 占领度 │ 探索度 │", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "├─────────┼──────┼──────┼──────┼──────┼──────┼────────┼────────┼────────┤", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "│ 千绝谷  │ 小区域 │ 2    │ 已占领 │ 灵草+15% │ 低   │ 0km    │ 100%  │ 100%  │", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "│ 黑风林  │ 小区域 │ 1    │ 已探索 │ 木材+20% │ 低   │ 5km    │ 0%    │ 100%  │", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "│ 青灵山脉│ 大区域 │ 3    │ 未探索 │ 未知资源 │ 高   │ 20km   │ 0%    │ 0%    │", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "│ 古战场  │ 大区域 │ 4    │ 未探索 │ 古物+功法 │ 极高  │ 50km   │ 0%    │ 0%    │", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "│ 玄水湖  │ 小区域 │ 2    │ 已探索 │ 泉水+25% │ 中   │ 10km   │ 0%    │ 100%  │", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "│ 妖兽森林│ 大区域 │ 2    │ 已探索 │ 妖兽材料 │ 中   │ 15km   │ 0%    │ 80%   │", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "│ 凡人国度│ 大区域 │ 1    │ 已占领 │ 基础资源 │ 低   │ 30km   │ 100%  │ 100%  │", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "│ 秘境入口│ 大区域 │ 5    │ 未探索 │ 秘境特产 │ 极高  │ 100km  │ 0%    │ 0%    │", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = "└─────────┴──────┴──────┴──────┴──────┴──────┴────────┴────────┴────────┘", color = textColor, fontSize = 13.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 区域详情
        Text(
            text = "【区域详情】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 名称：千绝谷 | 类型：小区域 | 等级：2 | 状态：已占领", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 资源产出：灵草+15% | 木材+5% | 每年产出：灵草1500、木材800", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 难度：低 | 距离：0km | 占领度：100% | 探索度：100%", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 特殊事件：灵草成熟 (10天后)", color = textColor, fontSize = 13.sp)
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
                text = "▶ 区域列表     ▶ 区域详情     ▶ 探索队伍     ▶ 占领管理     ▶ 事件记录",
                color = highlightColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
