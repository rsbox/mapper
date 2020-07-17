package io.rsbox.mapper.gui

import io.rsbox.mapper.mapper.asm.Class
import javafx.beans.property.SimpleObjectProperty

/**
 * Represents a node list selection observable
 * model.
 */
class NodeSelectionModel {

    var selectedClass = SimpleObjectProperty<Class>()

}