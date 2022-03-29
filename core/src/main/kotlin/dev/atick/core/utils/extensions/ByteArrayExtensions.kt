package dev.atick.core.utils.extensions

fun ByteArray.toHexString() {
    joinToString(
        separator = " ",
        prefix = "0x"
    ) { String.format("%02X", it) }
}