package io.rsbox.mapper.mapper.classifier

data class RankResult<T>(val subject: T, val score: Double, val results: List<ClassifierResult<T>>)