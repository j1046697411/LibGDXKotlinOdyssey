package cn.jzl.sect.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cn.jzl.sect.ui.components.BottomStatusBar
import cn.jzl.sect.ui.components.CentralContentArea
import cn.jzl.sect.ui.components.LeftMenuBar
import cn.jzl.sect.ui.components.RightInformationArea
import cn.jzl.sect.ui.components.TopNavigationBar

@Preview
@Composable
fun MainUI() {
    // 1920×1080 分辨率优化 - 160字符×60行，左:中:右 = 2:5:2
    
    // 当前选中菜单状态
    var currentMenu by remember { mutableStateOf(MenuOption.ZONGMEN_OVERVIEW) }
    
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 顶部导航栏
                TopNavigationBar(modifier = Modifier.fillMaxWidth())
                // 中间内容区域
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
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