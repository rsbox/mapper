package io.rsbox.mapper.gui.view.tab

import io.rsbox.mapper.gui.NodeSelectionModel
import io.rsbox.mapper.mapper.asm.Class
import javafx.scene.control.TabPane
import org.objectweb.asm.util.TraceClassVisitor
import tornadofx.onChange
import tornadofx.tab
import tornadofx.textarea
import java.io.PrintWriter
import java.io.StringWriter

class BytecodeTab(pane: TabPane, private val selectionModel: NodeSelectionModel) {

    val root = pane.tab("Bytecode") {
        isClosable = false

        textarea {
            setPrefSize(10000.0, 10000.0)

            selectionModel.selectedClass.onChange {
                text = it?.bytecode ?: ""
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