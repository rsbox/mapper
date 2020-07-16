package io.rsbox.mapper.mapper.asm

import io.rsbox.mapper.mapper.asm.util.newIdentityHashSet
import org.objectweb.asm.Opcodes.ACC_ENUM
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode

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

    override fun toString(): String = name
}