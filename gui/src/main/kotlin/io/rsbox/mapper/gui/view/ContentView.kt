package io.rsbox.mapper.gui.view

import io.rsbox.mapper.gui.NodeSelectionModel
import io.rsbox.mapper.gui.view.tab.BytecodeTab
import io.rsbox.mapper.gui.view.tab.ClassInfoTab
import tornadofx.*

/**
 * Represents the center tabbed content pane.
 */
class ContentView(val selection: NodeSelectionModel) : Fragment() {

    override val root = tabpane {
        BytecodeTab(this, selection.selectedClass).root
        ClassInfoTab(this, selection.selectedClass).root
    }
}