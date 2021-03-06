package io.rsbox.mapper.mapper.classifier

import io.rsbox.mapper.mapper.asm.Class
import org.objectweb.asm.Opcodes.*
import kotlin.math.pow

class ClassClassifier : AbstractClassifier<Class>(), Ranker<Class> {

    override fun init() {
        addClassifier(classTypeCheck, 20)
        addClassifier(hierarchyDepth, 1)
        addClassifier(hierarchyChildren, 2)
        addClassifier(parentClass, 4)
        addClassifier(childClasses, 3)
        addClassifier(interfaces, 3)
    }

    override fun rank(src: Class, targets: Array<Class>): List<RankResult<Class>> {
        return ClassifierUtil.rank(src, targets, classifiers, ClassifierUtil::checkPotentialEquality)
    }

    companion object {
        /**
         * Class type check classifier
         */
        private val classTypeCheck = classifier("class type check") { a: Class, b: Class ->
            val mask = ACC_ENUM or ACC_INTERFACE or ACC_ANNOTATION or ACC_ABSTRACT
            val resultA = a.access and mask
            val resultB = b.access and mask

            return@classifier (1 - Integer.bitCount(resultA.toDouble().pow(resultB.toDouble()).toInt()) / 4).toDouble()
        }

        /**
         * Hierarchy depth check
         */
        private val hierarchyDepth = classifier("hierarchy depth") { a: Class, b: Class ->
            var countA = 0
            var countB = 0

            var clsA = a
            while(clsA.parent != null) {
                clsA = clsA.parent!!
                countA++

            }

            var clsB = b
            while(clsB.parent != null) {
                clsB = clsB.parent!!
                countB++
            }

            return@classifier ClassifierUtil.compareCounts(countA, countB)
        }

        /**
         * Hierarchy children check
         */
        private val hierarchyChildren = classifier("hierarchy children") { a: Class, b: Class ->
            return@classifier ClassifierUtil.compareCounts(a.parent?.children?.size ?: 0, b.parent?.children?.size ?: 0)
        }

        /**
         * Parent class check
         */
        private val parentClass = classifier("parent class") { a: Class, b: Class ->
            if(a.parent == null && b.parent == null) return@classifier 1.0
            if(a.parent == null || b.parent == null) return@classifier 0.0

            return@classifier if(ClassifierUtil.checkPotentialEquality(a.parent!!, b.parent!!)) 1.0 else 0.0
        }

        /**
         * Child classes check
         */
        private val childClasses = classifier("child classes") { a: Class, b: Class ->
            return@classifier ClassifierUtil.compareClassSets(a.children, b.children)
        }

        /**
         * Interface classes check
         */
        private val interfaces = classifier("interfaces") { a: Class, b: Class ->
            return@classifier ClassifierUtil.compareClassSets(a.interfaces, b.interfaces)
        }

        /**
         * Implementers check
         */

    }
}