package io.rsbox.mapper.gui.view

import io.rsbox.mapper.gui.NodeSelectionModel
import io.rsbox.mapper.gui.view.tab.BytecodeTab
import io.rsbox.mapper.gui.view.tab.ClassInfoTab
import io.rsbox.mapper.gui.view.tab.SourceTab
import tornadofx.*

/**
 * Represents the center tabbed content pane.
 */
class ContentView(private val selection: NodeSelectionModel) : Fragment() {

    override val root = tabpane {
        SourceTab(this, selection).root
        BytecodeTab(this, selection).root
        ClassInfoTab(this, selection).root
    }
}