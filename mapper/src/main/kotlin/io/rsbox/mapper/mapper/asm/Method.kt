package io.rsbox.mapper.mapper.asm

import io.rsbox.mapper.mapper.asm.util.newIdentityHashSet
import org.objectweb.asm.Type
import org.objectweb.asm.tree.MethodNode

/**
 * Represents a Method ASM node.
 */
class Method(val group: ClassGroup, val owner: Class, val node: MethodNode) : Matchable<Method>(), Node {

    override val self = this

    val name get() = node.name

    val desc get() = node.desc

    val type get() = Type.getMethodType(desc)

    val returnType get() = Type.getReturnType(desc)

    val argumentTypes get() = Type.getArgumentTypes(desc)

    val access get() = node.access

    val isInitializer: Boolean get() {
        return this.name == INITIALIZER_NAME
    }

    val isConstructor: Boolean get() {
        return this.name == CONSTRUCTOR_NAME
    }

    /**
     * Utility methods / fields
     */

    val isNameObfuscated: Boolean get() =
        (name.length <= 2 || (name.startsWith("aa") && name.length == 3))

    /**
     * Reference sets
     */

    val refsIn = newIdentityHashSet<Method>()
    val refsOut = newIdentityHashSet<Method>()

    val fieldReadRefs = newIdentityHashSet<Field>()
    val fieldWriteRefs = newIdentityHashSet<Field>()

    val classRefs = newIdentityHashSet<Class>()

    var hierarchyMembers = hashSetOf<Method>()

    override fun toString(): String = "${owner.name}.$name$desc"

    companion object {
        private const val CONSTRUCTOR_NAME = "<init>"
        private const val INITIALIZER_NAME = "<clinit>"
    }
}