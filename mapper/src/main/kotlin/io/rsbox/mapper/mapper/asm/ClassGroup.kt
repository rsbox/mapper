package io.rsbox.mapper.mapper.asm

import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.util.jar.JarFile

class ClassGroup private constructor(nodes: Collection<ClassNode>) {

    /**
     * A mutable list of class objects in the class group.
     */
    val classes = nodes.map { Class(this, it) }.toMutableList()

    operator fun get(name: String): Class? = classes.firstOrNull { it.name == name }

    companion object {
        /**
         * Create a class group from a JAR file.
         */
        fun fromJar(file: File): ClassGroup {
            val nodes = mutableListOf<ClassNode>()

            JarFile(file).use { jar ->
                jar.entries().asSequence()
                    .filter { it.name.endsWith(".class") }
                    .forEach {
                        val reader = ClassReader(jar.getInputStream(it))
                        val node = ClassNode()
                        reader.accept(node, ClassReader.SKIP_FRAMES or ClassReader.SKIP_DEBUG)
                        nodes.add(node)
                    }
            }

            return ClassGroup(nodes)
        }
    }
}