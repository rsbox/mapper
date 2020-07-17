package io.rsbox.mapper.mapper.asm

import io.rsbox.mapper.mapper.asm.ext.isCallToInterface
import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.AbstractInsnNode.*
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InvokeDynamicInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode
import org.tinylog.kotlin.Logger

/**
 * Responsible for building feature maps for class, methods, and fields
 * inside of a [ClassGroup] object.
 */
class FeatureExtractor(val group: ClassGroup) {

    /**
     * Processes the class group feature extractor.
     */
    fun processGroup() {
        /**
         * Extract the class features in each [Class] in the group.
         */
        group.classes.forEach { c ->
            this.extractClassFeatures(c)
        }

        /**
         * Process method instruction features in each [Class]
         * in the [group]
         */
        group.classes.flatMap { it.methods }
            .forEach { m ->
                this.extractMethodFeatures(m)
            }
    }

    /**
     * Extract class features for [clazz]
     */
    private fun extractClassFeatures(clazz: Class) {
        // TODO Extract Class signature

        // TODO Extract Method Strings

        // TODO Extract Field Strings

        // TODO Extract outer class

        /**
         * Set parent / super class hierarchy
         */
        if(group[clazz.superName] != null) {
            clazz.setParent(group[clazz.superName]!!)
        }

        /**
         * Set interfaces and implementers.
         */
        clazz.node.interfaces
            .mapNotNull { group[it] }
            .forEach { i ->
                if(clazz.interfaces.add(i)) i.implementers.add(clazz)
            }
    }

    /**
     * Extract method features from [method]
     */
    private fun extractMethodFeatures(method: Method) {
        /**
         * Iterate through each instruction in the method.
         */
        val it = method.node.instructions.iterator()

        while(it.hasNext()) {
            /**
             * The current instruction from the iterator.
             */
            val instruction = it.next()

            /**
             * Switch through each instruction node type
             */
            when(instruction.type) {

                /**
                 * METHOD INVOKE INSTRUCTION
                 */
                METHOD_INSN -> {
                    val insn = instruction as MethodInsnNode

                    this.processMethodInvocation(
                        method = method,
                        owner = insn.owner,
                        name = insn.name,
                        desc = insn.desc,
                        toInterface = insn.isCallToInterface,
                        isStatic = insn.opcode == INVOKESTATIC
                    )
                }

                /**
                 * FIELD INVOKE INSTRUCTION
                 */
                FIELD_INSN -> {
                    val insn = instruction as FieldInsnNode
                    val owner = group[insn.owner] ?: return
                    val dst = owner.resolveField(insn.name, insn.desc)

                    if(dst == null) {
                        Logger.info("Method $method invoked field ${insn.name} but was not found in class $owner.")
                        return
                    }

                    if(insn.opcode == GETSTATIC || insn.opcode == GETFIELD) {
                        dst.readRefs.add(method)
                        method.fieldReadRefs.add(dst)
                    } else {
                        dst.writeRefs.add(method)
                        method.fieldWriteRefs.add(dst)
                    }

                    dst.owner.methodTypeRefs.add(method)
                    method.classRefs.add(dst.owner)
                }

                /**
                 * TYPE INVOKE INSTRUCTION
                 */
                TYPE_INSN -> {
                    val insn = instruction as TypeInsnNode
                    val dst = group[insn.desc] ?: return

                    dst.methodTypeRefs.add(method)
                    method.classRefs.add(dst)
                }

                /**
                 * INVOKE DYNAMIC INSTRUCTION
                 */
                INVOKE_DYNAMIC_INSN -> {
                    val insn = instruction as InvokeDynamicInsnNode
                    val impl: Handle = insn.bsmArgs[1] as Handle? ?: return

                    when(impl.tag) {
                        H_INVOKEVIRTUAL, H_INVOKESTATIC, H_INVOKESPECIAL, H_INVOKEINTERFACE -> {
                            processMethodInvocation(method, impl.owner, impl.name, impl.desc,
                                impl.isInterface,  impl.tag == H_INVOKESTATIC)
                        }

                        else -> {
                            Logger.info("Unexpected impl tag: ${impl.tag}")
                        }
                    }
                }
            }
        }
    }

    /**
     * Process method invocation instructions
     */
    private fun processMethodInvocation(method: Method, owner: String, name: String, desc: String, toInterface: Boolean, isStatic: Boolean) {
        val ownerClass = group[owner] ?: return
        val dst = ownerClass.resolveMethod(name, desc, toInterface) ?: return

        dst.refsIn.add(method)
        method.refsOut.add(dst)

        dst.owner.methodTypeRefs.add(method)
        method.classRefs.add(dst.owner)
    }
}