package io.rsbox.mapper.mapper.asm

import io.rsbox.mapper.mapper.asm.util.newIdentityHashSet
import org.objectweb.asm.Type
import org.objectweb.asm.tree.FieldNode

/**
 * Represents an ASM field node.
 */
class Field(val group: ClassGroup, val owner: Class, val node: FieldNode) : Matchable<Field>(), Node {

    override val self = this

    val name get() = node.name

    val desc get() = node.desc

    val type get() = Type.getType(desc)

    val access get() = node.access

    /**
     * Reference sets
     */

    val readRefs = newIdentityHashSet<Method>()
    val writeRefs = newIdentityHashSet<Method>()

    /**
     * Utility methods / fields
     */

    val isNameObfuscated: Boolean get() =
        (name.length <= 2 || (name.startsWith("aa") && name.length == 3))

    override fun toString(): String = "${owner.name}.$name"
}