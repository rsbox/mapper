package io.rsbox.mapper.mapper.classifier

import io.rsbox.mapper.mapper.asm.ClassGroup

interface Ranker<T> {

    fun rank(src: T, targets: Array<T>): List<RankResult<T>>

}