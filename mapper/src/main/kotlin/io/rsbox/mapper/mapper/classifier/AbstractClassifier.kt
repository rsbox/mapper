package io.rsbox.mapper.mapper.classifier

/**
 * Represents a base type classifier
 */
abstract class AbstractClassifier<T> {

    /**
     * Private store of classifier check types.
     */
    internal val classifiers = mutableListOf<Classifier<T>>()

    internal fun addClassifier(classifier: Classifier<T>, weight: Int) {
        classifier.weight = weight.toDouble()
        classifiers.add(classifier)
    }

    abstract fun init()
}