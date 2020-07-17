package io.rsbox.mapper.mapper.classifier

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.LdcInsnNode

object ClassifierUtil {

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
}