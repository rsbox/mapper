package io.rsbox.mapper.gui.view.tab

import io.rsbox.mapper.mapper.asm.Class
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.control.TabPane
import javafx.scene.control.TableView
import tornadofx.column
import tornadofx.onChange
import tornadofx.tab
import tornadofx.tableview

class ClassInfoTab(private val pane: TabPane, private val selectedClass: SimpleObjectProperty<Class>) {

    private val infoEntryList = FXCollections.observableArrayList<InfoEntry>()

    val root = pane.tab("Class Info") {
        isClosable = false

        tableview(infoEntryList) {
            column("Name", InfoEntry::nameProperty)
            column("Value", InfoEntry::valueProperty)

            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }

        selectedClass.onChange {
            if(it == null) {
                infoEntryList.clear()
            } else {
                infoEntryList.clear()
                populateInfoEntryList(it)
            }
        }
    }

    private fun populateInfoEntryList(clazz: Class) {
        infoEntryList.add(InfoEntry("Class Name", clazz.name))
        infoEntryList.add(InfoEntry("Parent Name", clazz.superName))
        infoEntryList.add(InfoEntry("Interfaces", clazz.interfaces.joinToString { it.name }))
        infoEntryList.add(InfoEntry("Children", clazz.children.joinToString { it.name }))
        infoEntryList.add(InfoEntry("Implementers", clazz.implementers.joinToString { it.name }))
        infoEntryList.add(InfoEntry("Access Opcode", clazz.access.toString()))
        infoEntryList.add(InfoEntry("Type", clazz.type.toString()))
        infoEntryList.add(InfoEntry("Ref Methods", clazz.methodTypeRefs.joinToString { "\n" + it.owner.name + "." + it.name}))
        infoEntryList.add(InfoEntry("Ref Fields", clazz.fieldTypeRefs.joinToString { "\n" + it.owner.name + "." + it.name }))
    }

    internal class InfoEntry(name: String, value: String) {
        val nameProperty = SimpleStringProperty(name)
        val valueProperty = SimpleStringProperty(value)
    }
}