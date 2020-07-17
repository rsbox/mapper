package io.rsbox.mapper.gui

import io.rsbox.mapper.mapper.asm.Class
import io.rsbox.mapper.mapper.asm.Field
import io.rsbox.mapper.mapper.asm.Method
import javafx.beans.property.SimpleObjectProperty

/**
 * Represents a node list selection observable
 * model.
 */
class NodeSelectionModel {

    var selectedType = SimpleObjectProperty<SelectedType>()

    var selectedClass = SimpleObjectProperty<Class>()

    var selectedMethod = SimpleObjectProperty<Method>()

    var selectedField = SimpleObjectProperty<Field>()
}