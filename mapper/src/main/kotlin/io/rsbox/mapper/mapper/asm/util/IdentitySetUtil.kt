package io.rsbox.mapper.mapper.asm.util

import java.util.*

fun <T> newIdentityHashSet(): HashSet<T> {
    val ret: Set<T> = Collections.newSetFromMap(IdentityHashMap())
    return ret.toHashSet()
}