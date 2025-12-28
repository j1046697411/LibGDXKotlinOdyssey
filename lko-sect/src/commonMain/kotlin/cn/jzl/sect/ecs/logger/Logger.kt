package cn.jzl.sect.ecs.logger

import cn.jzl.di.argPrototype
import cn.jzl.ecs.addon.createAddon

data class LogConfig(var level: LogLevel)

val logAddon = createAddon("log", { LogConfig(LogLevel.INFO) }) {
    injects { this bind argPrototype<Any, String, Logger> { ConsoleLogger(configuration.level, it) } }
}

enum class LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARN,
    ERROR
}

interface Logger {
    fun verbose(block: () -> String)
    fun debug(block: () -> String)
    fun info(block: () -> String)
    fun warn(block: () -> String)
    fun error(error: Throwable? = null, block: () -> String)
}

class ConsoleLogger(private val level: LogLevel, private val tag: String) : Logger {
    private fun log(messageLevel: LogLevel, error: Throwable? = null, message: () -> String) {
        if (messageLevel.ordinal >= level.ordinal) {
            println("[$tag] [$messageLevel] ${message.invoke()}")
            error?.printStackTrace()
        }
    }

    override fun verbose(block: () -> String) = log(LogLevel.VERBOSE, null, block)
    override fun debug(block: () -> String) = log(LogLevel.DEBUG, null, block)
    override fun info(block: () -> String) = log(LogLevel.INFO, null, block)
    override fun warn(block: () -> String) = log(LogLevel.WARN, null, block)
    override fun error(error: Throwable?, block: () -> String) = log(LogLevel.ERROR, error, block)
}
