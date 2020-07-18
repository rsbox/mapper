package io.rsbox.mapper.mapper.decompile

import io.rsbox.mapper.mapper.asm.Class
import java.util.concurrent.CompletableFuture

/**
 * Represents an interface decompiler which takes a [Class] and outputs
 * the Java representation source code.
 */
interface Decompiler {

    /**
     * Decompile a [Class] ASM object and output the
     * Java source code as a string.
     */
    fun decompile(clazz: Class): String

    fun decompileAsync(clazz: Class): CompletableFuture<String>
}