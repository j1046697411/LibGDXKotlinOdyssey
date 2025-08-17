package cn.jzl.ui

sealed interface AlignmentLine {
    fun merge(line1: Int, line2: Int): Int
}