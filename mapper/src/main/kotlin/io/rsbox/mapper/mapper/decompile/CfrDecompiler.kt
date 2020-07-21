package io.rsbox.mapper.mapper.decompile

import io.rsbox.mapper.common.coroutine.await
import io.rsbox.mapper.common.coroutine.future
import io.rsbox.mapper.mapper.asm.Class
import io.rsbox.mapper.mapper.asm.ClassGroup
import org.benf.cfr.reader.api.CfrDriver
import org.benf.cfr.reader.api.ClassFileSource
import org.benf.cfr.reader.api.OutputSinkFactory
import org.benf.cfr.reader.api.OutputSinkFactory.SinkClass
import org.benf.cfr.reader.api.OutputSinkFactory.SinkType
import org.benf.cfr.reader.bytecode.analysis.parse.utils.Pair
import java.nio.file.NoSuchFileException
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * The CFR Decompiler implementation.
 */
object CfrDecompiler : Decompiler {

    /**
     * Decompiles a [Class] object.
     */
    override fun decompile(clazz: Class): String = this.decompileTask(clazz)

    override fun decompileAsync(clazz: Class): CompletableFuture<String> {
        return future {
            return@future CompletableFuture.supplyAsync {
                this.decompileTask(clazz)
            }.await()
        }
    }

    /**
     * The decompile Future coroutine task
     */
    private fun decompileTask(clazz: Class): String {
        val options = hashMapOf<String, String>()

        val sink = Sink()

        val driver = CfrDriver.Builder()
            .withOptions(options)
            .withClassFileSource(Source(clazz.group))
            .withOutputSink(sink)
            .build()

        driver.analyse(Collections.singletonList(clazz.name.replace(".class", "")))

        return sink.toString()
    }

    private class Sink : OutputSinkFactory {

        private val stringBuilder = StringBuilder()

        override fun getSupportedSinks(
            sinkType: SinkType,
            available: MutableCollection<SinkClass>
        ): MutableList<SinkClass> {
            return Collections.singletonList(SinkClass.STRING)
        }

        override fun <T> getSink(sinkType: SinkType, sinkClass: SinkClass): OutputSinkFactory.Sink<T> {
            return when (sinkType) {
                SinkType.EXCEPTION -> OutputSinkFactory.Sink { str: T -> println("e $str") }
                SinkType.JAVA -> OutputSinkFactory.Sink { obj: T -> stringBuilder.append(obj) }
                SinkType.PROGRESS -> OutputSinkFactory.Sink { str: T -> println("p $str") }
                SinkType.SUMMARY -> OutputSinkFactory.Sink { str: T -> println("s $str") }
                else -> {
                    println("unknown sink type: $sinkType")
                    OutputSinkFactory.Sink { str: T -> println("* $str") }
                }
            }
        }

        override fun toString(): String {
            return stringBuilder.toString()
        }
    }

    private class Source(private val group: ClassGroup) : ClassFileSource {

        override fun informAnalysisRelativePathDetail(usePath: String?, classFilePath: String?) {

        }

        override fun addJar(jarPath: String): MutableCollection<String> {
            throw UnsupportedOperationException()
        }

        override fun getPossiblyRenamedPath(path: String): String {
            return path
        }

        override fun getClassFileContent(path: String): Pair<ByteArray, String> {
            if(!path.endsWith(".class")) {
                throw NoSuchFileException(path)
            }

            val className = path.substring(0, path.length - ".class".length)
            val clazz = group[className] ?: throw NoSuchFileException(path)

            val data = clazz.serialize()

            return Pair.make(data, path)
        }
    }
}