package io.rsbox.mapper.mapper.classifier

import io.rsbox.mapper.mapper.asm.Class
import io.rsbox.mapper.mapper.asm.Field
import io.rsbox.mapper.mapper.asm.Matchable
import io.rsbox.mapper.mapper.asm.Method
import io.rsbox.mapper.mapper.asm.util.newIdentityHashSet
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.IntInsnNode
import org.objectweb.asm.tree.LdcInsnNode
import java.util.*
import kotlin.math.abs
import kotlin.math.max

/**
 * Utility methods for comparing matchable types or nodes from ASM.
 */
object ClassifierUtil {

    fun checkPotentialEquality(a: Class, b: Class): Boolean {
        if(a == b) return true
        if(a.match != null) return a.match == b
        if(b.match != null) return b.match == a

        return true
    }

    fun checkPotentialEquality(a: Method, b: Method): Boolean {
        if(a == b) return true
        if(a.match != null) return a.match == b
        if(b.match != null) return b.match == a
        if(!checkPotentialEquality(a.owner, b.owner)) return false

        return true
    }

    fun checkPotentialEquality(a: Field, b: Field): Boolean {
        if(a == b) return true
        if(a.match != null) return a.match == b
        if(b.match != null) return b.match == a
        if(!checkPotentialEquality(a.owner, b.owner)) return false

        return true
    }

    fun compareCounts(a: Int, b: Int): Double {
        val delta = abs(a - b)
        if(delta == 0) return 1.0

        return (1 - (delta / max(a, b))).toDouble()
    }

    fun <T> compareSets(a: Set<T>, b: Set<T>): Double {
        val copySet = mutableSetOf<T>()
        copySet.addAll(b)

        val oldSize = b.size
        copySet.removeAll(a)

        val matched = oldSize - copySet.size
        val total = a.size - matched + oldSize

        return if(total == 0) 1.0 else (matched / total).toDouble()
    }

    fun compareClassSets(a: MutableSet<Class>, b: MutableSet<Class>): Double {
        return compareIdentitySets(a, b, ClassifierUtil::checkPotentialEquality)
    }

    fun compareMethodSets(a: MutableSet<Method>, b: MutableSet<Method>): Double {
        return compareIdentitySets(a, b, ClassifierUtil::checkPotentialEquality)
    }

    fun compareFieldSets(a: MutableSet<Field>, b: MutableSet<Field>): Double {
        return compareIdentitySets(a, b, ClassifierUtil::checkPotentialEquality)
    }

    private fun <T : Matchable<T>> compareIdentitySets(a: MutableSet<T>, b: MutableSet<T>, predicate: (T, T) -> Boolean): Double {
        if(a.isEmpty() || b.isEmpty()) {
            return if(a.isEmpty() && b.isEmpty()) 1.0 else 0.0
        }

        val setA = newIdentityHashSet<T>()
        setA.addAll(a)

        val setB = newIdentityHashSet<T>()
        setB.addAll(b)

        val total = setA.size + setB.size
        var unmatched = 0

        val it1 = setA.iterator()
        while(it1.hasNext()) {
            val node = it1.next()

            if(setB.remove(node)) {
                it1.remove()
            } else if(node.match != null) {
                if(!setB.remove(node.match!!)) {
                    unmatched++
                }

                it1.remove()
            }
        }

        val it2 = setA.iterator()
        while(it2.hasNext()) {
            val node = it2.next()

            var found = false

            setB.forEach {
                if(predicate(node, it)) {
                    found = true
                    return@forEach
                }
            }

            if(!found) {
                unmatched++
                it2.remove()
            }
        }

        setB.forEach bLoop@ { rb ->
            var found = false

            setA.forEach { ra ->
                if(predicate(ra, rb)) {
                    found = true
                    return@bLoop
                }
            }

            if(!found) {
                unmatched++
            }
        }

        return ((total - unmatched) / total).toDouble()
    }

    fun extractStrings(insnList: InsnList, out: MutableSet<String>) {
        extractStrings(insnList.iterator(), out)
    }

    fun extractStrings(insnList: Collection<AbstractInsnNode>, out: MutableSet<String>) {
        extractStrings(insnList.iterator(), out)
    }

    private fun extractStrings(it: Iterator<AbstractInsnNode>, out: MutableSet<String>) {
        while(it.hasNext()) {
            val insn = it.next()

            if(insn is LdcInsnNode) {
                if(insn.cst is String) {
                    out.add(insn.cst as String)
                }
            }
        }
    }

    fun extractNumbers(method: Method, ints: MutableSet<Int>, longs: MutableSet<Long>, floats: MutableSet<Float>, doubles: MutableSet<Double>) {
        val it = method.node.instructions.iterator()

        while(it.hasNext()) {
            val insn = it.next()

            if(insn is LdcInsnNode) {
                val number = insn.cst as Number ?: return
                when(number) {
                    is Int -> ints.add(number)
                    is Long -> longs.add(number)
                    is Float -> floats.add(number)
                    is Double -> doubles.add(number)
                }
            } else if(insn is IntInsnNode) {
                ints.add(insn.operand)
            }
        }
    }
}