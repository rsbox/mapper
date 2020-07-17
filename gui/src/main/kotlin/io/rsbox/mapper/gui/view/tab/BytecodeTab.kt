package io.rsbox.mapper.gui.view.tab

import io.rsbox.mapper.mapper.asm.Class
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TabPane
import org.objectweb.asm.util.TraceClassVisitor
import tornadofx.onChange
import tornadofx.select
import tornadofx.tab
import tornadofx.textarea
import java.io.PrintWriter
import java.io.StringWriter

class BytecodeTab(private val pane: TabPane, private val selectedClass: SimpleObjectProperty<Class>) {

    val root = pane.tab("Bytecode") {
        isClosable = false
        select()

        textarea {
            setPrefSize(10000.0, 10000.0)

            selectedClass.onChange {
                if(it == null) {
                    text = ""
                } else {
                    text = it.bytecode
                }
            }
        }
    }

    private val Class.bytecode: String get() {
        val writer = StringWriter()
        val pw = PrintWriter(writer)

        this.node.accept(TraceClassVisitor(pw))

        return writer.toString()
    }
}