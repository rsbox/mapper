package io.rsbox.mapper.mapper.asm

import io.rsbox.mapper.mapper.asm.ext.isCallToInterface
import io.rsbox.mapper.mapper.classifier.ClassifierUtil
import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*
import org.objectweb.asm.tree.AbstractInsnNode.*
import org.tinylog.kotlin.Logger
import java.util.*

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

        /**
         * Extract class strings.
         */

        clazz.methods.forEach { m ->
            ClassifierUtil.extractStrings(m.node.instructions, clazz.strings)
        }

        clazz.fields.forEach { f ->
            if(f.node.value is String) {
                clazz.strings.add(f.node.value as String)
            }
        }

        /**
         * Extract out class refs.
         */

        if(clazz.outerClass == null) detectOuterClass(clazz, clazz.node)

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
     * Extracts the hierarchy tree of a given ASM class node.
     */
    private fun extractClassHierarchyMethods(clazz: Class) {
        if(clazz.children.isNotEmpty() || clazz.interfaces.isNotEmpty()) return

        val methods = hashMapOf<String, Method>()
        val checkQueue = ArrayDeque<Class>()

        checkQueue.add(clazz)

        var cls: Class?
        while(checkQueue.poll().also { cls = it } != null) {

            /**
             * Loop through the methods in the hierarchy memnbers
             */
            cls!!.methods.forEach { m ->
                if(!m.isHierarchyBarrier()) return@forEach

                var prev: Method? = methods[m.desc]
                if(prev != null) {
                    if(m.hierarchyMembers.size == 0) {
                        m.hierarchyMembers = prev.hierarchyMembers
                        m.hierarchyMembers.add(m)
                    } else if(m.hierarchyMembers != prev.hierarchyMembers) {
                        m.hierarchyMembers.addAll(prev.hierarchyMembers)

                        prev.hierarchyMembers.forEach { pm ->
                            m.hierarchyMembers = prev!!.hierarchyMembers
                        }
                    }

                    prev = methods[m.desc]
                } else {
                    methods[m.desc] = m
                    m.hierarchyMembers.add(m)
                }
            }

            if(group[cls!!.superName] != null) checkQueue.add(group[cls!!.superName]!!)
            checkQueue.addAll(cls!!.interfaces)
        }

        println("name: ${clazz.name} - ${methods.size}")
    }

    private fun Method.isHierarchyBarrier(): Boolean {
        return (this.access and (ACC_PRIVATE or ACC_STATIC)) != 0
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

    private fun detectOuterClass(clazz: Class, node: ClassNode) {
        if(node.outerClass != null) {
            addOuterClass(clazz, node.outerClass)
        } else if(node.outerMethod != null) {
            throw UnsupportedOperationException()
        } else {
            node.innerClasses.forEach { ic ->
                if(ic.name == node.name) {
                    addOuterClass(clazz, ic.outerName)
                    return
                }
            }

            val pos = node.name.lastIndexOf("$")
            if(pos > 0 && pos < node.name.length - 1) {
                addOuterClass(clazz, node.name.substring(0, pos))
            }
        }
    }

    private fun addOuterClass(clazz: Class, name: String) {
        val outerClass = group[name] ?: return

        clazz.outerClass = outerClass
        outerClass.innerClasses.add(clazz)
    }
}