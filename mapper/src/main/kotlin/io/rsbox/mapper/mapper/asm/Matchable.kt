package io.rsbox.mapper.mapper.asm

/**
 * Represents a matchable ASM node.
 */
abstract class Matchable<T> {

    abstract val self: T

    /**
     * The matched ASM object.
     */
    var match: T? = null

    val hasMatch: Boolean get() = (match != null)
}