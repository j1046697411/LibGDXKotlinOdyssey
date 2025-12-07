package cn.jzl.sect

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import java.awt.Dimension

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KMP Template",
        state = WindowState(
//            placement = WindowPlacement.Maximized,  // 默认最大化
            size = DpSize(800.dp, 600.dp)  // 初始尺寸（最大化时不生效）
        ),
        resizable = true,  // 允许调整窗口大小
        undecorated = false  // 保留窗口装饰（标题栏等）
    ) {
        // 设置窗口最小尺寸
        window.minimumSize = Dimension(360, 680)
        App()
    }
}
