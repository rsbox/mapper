package io.rsbox.mapper.mapper.classifier

data class ClassifierResult<T>(val classifier: Classifier<T>, val score: Double)