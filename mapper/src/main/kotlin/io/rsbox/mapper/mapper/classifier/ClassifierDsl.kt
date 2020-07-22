package io.rsbox.mapper.mapper.classifier

import io.rsbox.mapper.mapper.asm.Matchable

fun <T : Matchable<T>> classifier(name: String, logic: (T, T) -> Double): Classifier<T> {
    return object : Classifier<T> {
        override val name = name
        override var weight = 0.0

        override fun getScore(a: T, b: T): Double {
            return logic(a, b)
        }
    }
}