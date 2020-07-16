package io.rsbox.mapper.mapper.asm

import io.rsbox.mapper.mapper.asm.util.newIdentityHashSet
import org.objectweb.asm.Opcodes.ACC_ENUM
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import java.util.ArrayDeque

/**
 * Represents an ASM class node.
 */
class Class(val group: ClassGroup, val node: ClassNode) {

    val name get() = node.name

    val superName get() = node.superName

    val type get() = Type.getObjectType(name)

    val superType get() = Type.getObjectType(superName)

    val access get() = node.access

    var parent: Class? = null
        set(value) {
            field = value
            value!!.children
        }

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

    val isEnum get() = (node.access and ACC_ENUM) != 0

    /**
     * Resolves a field which may be defined in a hierarchy.
     */
    fun resolveField(name: String, desc: String): Field? {
        var ret = fields.firstOrNull { it.name == name && it.desc == desc }
        if(ret != null) return ret

        /**
         * Iterate through interfaces
         */
        if(interfaces.isNotEmpty()) {
            val queue = ArrayDeque<Class>()
            queue.addAll(interfaces)

            var cur: Class? = queue.pollFirst()
            while(cur != null) {
                ret = cur.fields.firstOrNull { it.name == name && it.desc == desc }
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
            ret = cur.fields.firstOrNull { it.name == name && it.desc == desc }
            if(ret != null) return ret

            cur = cur.parent
        }

        return null
    }

    override fun toString(): String = name
}