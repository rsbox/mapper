package io.rsbox.mapper.mapper.classifier

/**
 * Represents a type classifier which compares two nodes and scores
 * them based on their similarity and weight values.
 */
interface Classifier<T> {

    /**
     * The name of the classifier
     */
    val name: String

    /**
     * The score weight multiplier
     */
    var weight: Double

    /**
     * Gets the similarity score of the two nodes.
     */
    fun getScore(a: T, b: T): Double
}