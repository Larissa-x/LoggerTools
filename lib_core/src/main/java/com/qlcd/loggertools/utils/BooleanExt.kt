package com.qlcd.loggertools.utils


sealed class BooleanExt<out T> {
    class TransferData<out T>(val data: T) : BooleanExt<T>()
    object Otherwise : BooleanExt<Nothing>()
}

inline fun <T> Boolean.yes(block: () -> T): BooleanExt<T> =
    when {
        this -> BooleanExt.TransferData(block.invoke())
        else -> BooleanExt.Otherwise
    }

inline fun <T> BooleanExt<T>.otherwise(block: () -> T): T =
    when (this) {
        is BooleanExt.Otherwise -> block()
        is BooleanExt.TransferData -> data
    }