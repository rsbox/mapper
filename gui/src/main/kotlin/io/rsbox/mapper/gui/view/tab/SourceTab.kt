package io.rsbox.mapper.gui.view.tab

import io.rsbox.mapper.gui.util.HtmlUtil
import io.rsbox.mapper.gui.NodeSelectionModel
import io.rsbox.mapper.mapper.asm.Class
import io.rsbox.mapper.mapper.decompile.CfrDecompiler
import javafx.concurrent.Worker
import javafx.scene.control.TabPane
import javafx.scene.web.WebView
import tornadofx.add
import tornadofx.onChange
import tornadofx.select
import tornadofx.tab
import java.util.ArrayDeque

class SourceTab(pane: TabPane, private val selectionModel: NodeSelectionModel) {

    private val webview = WebView()
    private val taskQueue = ArrayDeque<() -> Unit>()

    /**
     * Template HTML string.
     */
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private val template = javaClass.getResource("/template/SourceCodeTemplate.htm").readText(Charsets.UTF_8)

    val root = pane.tab("Source") {
        isClosable = false
        select()

        add(webview)
    }

    init {
        webview.engine.loadWorker.stateProperty().addListener { observable, oldValue, newValue ->
            if(newValue == Worker.State.SUCCEEDED) {
                var r = taskQueue.poll()

                while(r != null) {
                    r()
                    r = taskQueue.poll()
                }
            }
        }

        displayText("No Class Selected")

        selectionModel.selectedClass.onChange {
            if(it != null) update(it)
        }

        selectionModel.selectedMethod.onChange {
            if(it != null) jumpTo(it.name)
        }

        selectionModel.selectedField.onChange {
            if(it != null) jumpTo(it.name)
        }
    }

    private fun jumpTo(anchor: String) {
        queueTask { webview.engine.executeScript("$(window).scrollTop($(\"span:contains('$anchor'):first\").offset().top)") }
    }

    private fun update(clazz: Class) {
        taskQueue.clear()

        displayText("Decompiling...")

        val asyncTask = CfrDecompiler.decompileAsync(clazz)
        val text = asyncTask.join()

        asyncTask.whenComplete { _, _ ->
            displayHtml(text)
            queueTask { webview.engine.executeScript("document.body.scrollTop = 0") }
        }
    }

    private fun displayText(text: String) {
        displayHtml(HtmlUtil.escape(text))
    }

    private fun displayHtml(html: String) {
        webview.engine.loadContent(template.replace("%text%", html))
    }

    private fun queueTask(task: () -> Unit) {
        if(webview.engine.loadWorker.state == Worker.State.SUCCEEDED) {
            task()
        } else {
            taskQueue.add(task)
        }
    }
}