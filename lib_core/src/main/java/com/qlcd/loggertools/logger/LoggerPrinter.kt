package com.qlcd.loggertools.logger


/** * Created by Tony Shen on 2017/1/2. */

object LoggerPrinter {
    private const val MIN_STACK_OFFSET = 3
    /** * Drawing toolbox */
    private const val TOP_LEFT_CORNER = '┌'
    private const val BOTTOM_LEFT_CORNER = '└'
    private const val MIDDLE_CORNER = '├'
    private const val HORIZONTAL_DOUBLE_LINE = '│'
    private const val DOUBLE_DIVIDER = "────────────────────────────────────────────────────────"
    private const val SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄"
    val TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    val BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    val MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER
    /** * It is used for json pretty print */
    const val JSON_INDENT = 2

    fun getStackOffset(trace: Array<StackTraceElement>): Int {
        var i = MIN_STACK_OFFSET
        while (i < trace.size) {
            val e = trace[i]
            val name = e.className
            if (name != LoggerPrinter::class.java.name && name != LogKit::class.java.name) {
                return --i
            }
            i ++
        }
        return -1
    }
}