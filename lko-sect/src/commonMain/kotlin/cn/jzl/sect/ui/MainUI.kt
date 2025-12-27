package cn.jzl.sect.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.jzl.sect.ui.components.BottomStatusBar
import cn.jzl.sect.ui.components.CentralContentArea
import cn.jzl.sect.ui.components.LeftMenuBar
import cn.jzl.sect.ui.components.RightInformationArea
import cn.jzl.sect.ui.components.TopNavigationBar

/**
 * 主界面组件，应用的根UI组件
 * 
 * 布局结构：
 * - 顶部导航栏（TopNavigationBar）
 * - 中间内容区域，包含：
 *   - 左侧菜单栏（LeftMenuBar）：占比2
 *   - 中央内容区（CentralContentArea）：占比5
 *   - 右侧信息区（RightInformationArea）：占比2
 * - 底部状态栏（BottomStatusBar）
 * 
 * 优化适配1920×1080分辨率，采用160字符×60行的布局设计
 */
@Preview
@Composable
fun MainUI() {
    // 当前选中菜单状态
    var currentMenu by remember { mutableStateOf(MenuOption.ZONGMEN_OVERVIEW) }

    TerminalTheme(darkTheme = false) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 顶部导航栏
                TopNavigationBar(modifier = Modifier.fillMaxWidth())
                // 中间内容区域
                Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                    // 左侧菜单栏 - 传递当前菜单和菜单切换回调
                    LeftMenuBar(
                        modifier = Modifier.weight(2f).fillMaxHeight(),
                        currentMenu = currentMenu,
                        onMenuChange = { newMenu ->
                            currentMenu = newMenu
                        }
                    )
                    // 中央内容区 - 根据当前菜单显示不同内容
                    CentralContentArea(
                        modifier = Modifier.weight(5f).fillMaxHeight(),
                        currentMenu = currentMenu
                    )
                    // 右侧信息区
                    RightInformationArea(modifier = Modifier.weight(2f).fillMaxHeight(), currentMenu = currentMenu)
                }
                // 底部状态栏
                BottomStatusBar(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}