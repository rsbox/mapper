package io.rsbox.mapper.mapper.asm

import io.rsbox.mapper.mapper.asm.util.newIdentityHashSet
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import java.util.ArrayDeque

/**
 * Represents an ASM class node.
 */
class Class(val group: ClassGroup, val node: ClassNode) : Matchable<Class>(), Node {

    override val self = this

    val name get() = node.name

    val superName get() = node.superName

    val type get() = Type.getObjectType(name)

    val superType get() = Type.getObjectType(superName)

    val access get() = node.access

    var parent: Class? = null
        private set

    val children = newIdentityHashSet<Class>()

    val interfaces = newIdentityHashSet<Class>()
    val implementers = newIdentityHashSet<Class>()

    /**
     * Entry nodes
     */

    val methods = node.methods.map { Method(group, this, it) }

    val fields = node.fields.map { Field(group, this, it) }

    /**
     * Reference sets
     */

    val methodTypeRefs = newIdentityHashSet<Method>()
    val fieldTypeRefs = newIdentityHashSet<Field>()

    /**
     * Utility fields
     */

    val isNameObfuscated: Boolean get() =
        (name.length <= 2 || (name.startsWith("aa") && name.length == 3))

    fun getMethod(name: String, desc: String): Method? {
        return methods.firstOrNull { it.name == name && it.desc == desc }
    }

    fun getField(name: String, desc: String): Field? {
        return fields.firstOrNull { it.name == name && it.desc == desc }
    }

    fun setParent(clazz: Class) {
        parent = clazz
        clazz.children.add(this)
    }

    /**
     * Resolves a method which may be defined in a hierarchy.
     */
    fun resolveMethod(name: String, desc: String, toInterface: Boolean): Method? {
        if(!toInterface) {
            var ret = getMethod(name, desc)
            if(ret != null) return ret

            var cls: Class? = this

            while(cls != null) {
                ret = cls.getMethod(name, desc)
                if(ret != null) return ret

                cls = group[cls.superName]
            }

            return resolveInterfaceMethod(name, desc)
        } else {
            var ret = getMethod(name, desc)
            if(ret != null) return ret

            if(group[superName] != null) {
                ret = group[superName]?.getMethod(name, desc)
                if(ret != null && (ret.access and (ACC_PUBLIC or ACC_STATIC)) == ACC_PUBLIC) return ret
            }

            return resolveInterfaceMethod(name, desc)
        }
    }

    /**
     * Resolves a field which may be defined in a hierarchy.
     */
    fun resolveField(name: String, desc: String): Field? {
        var ret = getField(name, desc)
        if(ret != null) return ret

        /**
         * Iterate through interfaces
         */
        if(interfaces.isNotEmpty()) {
            val queue = ArrayDeque<Class>()
            queue.addAll(interfaces)

            var cur: Class? = queue.pollFirst()
            while(cur != null) {
                ret = cur.getField(name, desc)
                if(ret != null) return ret

                cur.interfaces.forEach { queue.addFirst(it) }

                cur = queue.pollFirst()
            }
        }

        /**
         * Iterate through parents
         */
        var cur: Class? = parent
        while(cur != null) {
            ret = cur.getField(name, desc)
            if(ret != null) return ret

            cur = cur.parent
        }

        return null
    }

    private fun resolveInterfaceMethod(name: String, desc: String): Method? {
        val queue = ArrayDeque<Class>()
        val queued = newIdentityHashSet<Class>()

        var cls: Class? = this

        while(cls != null) {
            cls.interfaces.forEach {
                if(queued.add(it)) queue.add(it)
            }

            cls = group[cls.superName]
        }

        if(queue.isEmpty()) return null

        val matches = newIdentityHashSet<Method>()
        var foundNonAbstract = false

        cls = queue.poll()
        while(cls != null) {
            var ret = cls.getMethod(name, desc)
            if(ret != null &&
                (ret.access and (ACC_PRIVATE or ACC_STATIC)) == 0) {
                matches.add(ret)

                if((ret.access and ACC_ABSTRACT) == 0) {
                    foundNonAbstract = true
                }
            }

            cls.interfaces.forEach { i ->
                if(queued.add(i)) queue.add(i)
            }

            cls = queue.poll()
        }

        if(matches.isEmpty()) return null
        if(matches.size == 1) return matches.iterator().next()

        if(foundNonAbstract) {
            val it = matches.iterator()
            while(it.hasNext()) {
                val m = it.next()

                if((m.access and ACC_ABSTRACT) != 0) {
                    it.remove()
                }
            }

            assert(matches.isNotEmpty())
            if(matches.size == 1) return matches.iterator().next()
        }

        val it = matches.iterator()
        while(it.hasNext()) {
            val m = it.next()

            matches.forEach { match ->

                if(match.owner.interfaces.contains(m.owner)) {
                    it.remove()
                    return@forEach
                }

                queue.addAll(match.owner.interfaces)

                var cur: Class? = queue.poll()
                while(cur != null) {
                    if(cur.interfaces.contains(m.owner)) {
                        it.remove()
                        queue.clear()
                        return@forEach
                    }

                    queue.addAll(cur.interfaces)

                    cur = queue.poll()
                }
            }
        }

        return matches.iterator().next()
    }

    override fun toString(): String = name
}