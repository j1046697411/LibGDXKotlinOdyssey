package cn.jzl.sect

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformApplication(context: @Composable () -> Unit) { context() }