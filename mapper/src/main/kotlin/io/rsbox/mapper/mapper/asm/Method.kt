package io.rsbox.mapper.mapper.asm

import org.objectweb.asm.Type
import org.objectweb.asm.tree.MethodNode

/**
 * Represents a Method ASM node.
 */
class Method(val group: ClassGroup, val owner: Class, val node: MethodNode) {

    val name get() = node.name

    val desc get() = node.desc

    val type get() = Type.getMethodType(desc)

    val returnType get() = Type.getReturnType(desc)

    val argumentTypes get() = Type.getArgumentTypes(desc)

    val access get() = node.access

    /**
     * Utility methods / fields
     */

    val isNameObfuscated: Boolean get() =
        (name.length <= 2 || (name.startsWith("aa") && name.length == 3))

    /**
     * Reference sets
     */

    val refsIn = hashSetOf<Method>()

    val refsOut = hashSetOf<Method>()

    val fieldReadRefs = hashSetOf<Field>()

    val fieldWriteRefs = hashSetOf<Field>()

    val classRefs = hashSetOf<Class>()

    override fun toString(): String = "${owner.name}.$name$desc"
}