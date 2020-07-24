package io.rsbox.mapper.mapper

import io.rsbox.mapper.mapper.asm.*
import io.rsbox.mapper.mapper.classifier.ClassClassifier
import io.rsbox.mapper.mapper.classifier.RankResult
import io.rsbox.mapper.mapper.decompile.CfrDecompiler
import org.tinylog.kotlin.Logger
import java.io.File

/**
 * Represents the primary Mapper object
 */
class Mapper {

    /**
     * The mapped reference JAR [ClassGroup] object
     */
    lateinit var mappedGroup: ClassGroup
        private set

    /**
     * The new / latest obfuscated JAR [ClassGroup] object.
     */
    lateinit var targetGroup: ClassGroup
        private set

    /**
     * Classifier singletons.
     */
    private val classClassifier = ClassClassifier()

    init {
        /**
         * Initialize all classifiers
         */
        classClassifier.init()
    }

    /**
     * Loads the Mapped JAR file.
     */
    fun loadMappedJar(file: File) {
        Logger.info("Loading mapped JAR file classes.")

        mappedGroup = ClassGroup.fromJar(file)

        /**
         * Extract class group features.
         */
        Logger.info("Extracting mapped classes features.")

        val extractor = FeatureExtractor(mappedGroup)
        extractor.processGroup()

        Logger.info("Completed loading mapped JAR classes. Found ${mappedGroup.classes.size} classes.")
    }

    /**
     * Loads the Target JAR file.
     */
    fun loadTargetJar(file: File) {
        Logger.info("Loading target JAR file classes.")

        targetGroup = ClassGroup.fromJar(file)

        /**
         * Extract group features
         */
        Logger.info("Extracting target classes features.")

        val extractor = FeatureExtractor(targetGroup)
        extractor.processGroup()

        Logger.info("Completed loading target JAR classes. Found ${targetGroup.classes.size} classes.")
    }

    fun classifyClasses(srcGroup: ClassGroup, targetGroup: ClassGroup): HashMap<Class, List<RankResult<Class>>> {
        Logger.info("Classifying classes between class groups.")

        val results = hashMapOf<Class, List<RankResult<Class>>>()

        srcGroup.classes.forEach { srcClass ->
            val ranking = classClassifier.rank(srcClass, getPotentialClassMatches(targetGroup).toTypedArray())
            results[srcClass] = ranking
        }

        return results
    }

    /**
     * Gets all potential matchable classes from the [other] [ClassGroup]
     */
    private fun getPotentialClassMatches(other: ClassGroup): MutableSet<Class> {
        return other.classes.toMutableSet()
    }

    /**
     * Get all potential matchable methods from the [other] [ClassGroup]
     */
    fun getPotentialMethodMatches(method: Method, other: ClassGroup): MutableSet<Method> {
        val methods = mutableSetOf<Method>()

        methods.addAll(method.owner.match!!.methods)
        methods.addAll(other.classes.flatMap { it.staticMethods })

        return methods
    }

    /**
     * Get all potential matchable fields from the [other] [ClassGroup]
     */
    fun getPotentialFieldMatches(field: Field, other: ClassGroup): MutableSet<Field> {
        val fields = mutableSetOf<Field>()

        fields.addAll(field.owner.match!!.fields)
        fields.addAll(other.classes.flatMap { it.staticFields })

        return fields
    }
}