package io.rsbox.mapper.mapper

import io.rsbox.mapper.mapper.asm.ClassGroup
import io.rsbox.mapper.mapper.asm.FeatureExtractor
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
}