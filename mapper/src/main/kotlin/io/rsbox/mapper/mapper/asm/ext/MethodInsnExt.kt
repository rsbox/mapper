package io.rsbox.mapper.mapper.asm.ext

import org.objectweb.asm.Opcodes.INVOKEINTERFACE
import org.objectweb.asm.tree.MethodInsnNode

/**
 * Whether a METHOD_INSN instruction is invoking an interface.
 */
val MethodInsnNode.isCallToInterface: Boolean get() {
    assert(this.itf || this.opcode != INVOKEINTERFACE)
    return this.itf
}