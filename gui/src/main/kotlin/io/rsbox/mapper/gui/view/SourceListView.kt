package io.rsbox.mapper.gui.view

import io.rsbox.mapper.gui.NodeSelectionModel
import io.rsbox.mapper.gui.SelectedType
import io.rsbox.mapper.gui.Styles
import io.rsbox.mapper.gui.controller.MapperController
import io.rsbox.mapper.gui.event.MapperLoadEvent
import io.rsbox.mapper.mapper.asm.Class
import io.rsbox.mapper.mapper.asm.Field
import io.rsbox.mapper.mapper.asm.Method
import javafx.geometry.Orientation
import javafx.scene.control.SelectionMode
import javafx.scene.paint.Color
import tornadofx.*

class SourceListView() : Fragment() {

    val nodeSelectionModel: NodeSelectionModel by param()

    private val controller: MapperController by inject()

    override val root = splitpane(Orientation.VERTICAL) {
        prefWidth = 200.0

        /**
         * Class List
         */
        listview<Class> {
            addClass(Styles.whiteDarkText)
            cellFormat {
                text = it.name

                textFill = when(it.hasMatch) {
                    false -> Color.rgb(236, 82, 82)
                    true -> Color.rgb(93, 167, 19)
                }
            }

            selectionModel.selectionMode = SelectionMode.SINGLE

            bindSelected(nodeSelectionModel.selectedClass)

            subscribe<MapperLoadEvent> { e ->
                items.setAll(e.mapper.mappedGroup.classes)
            }
        }

        /**
         * Method List
         */
        listview<Method> {
            addClass(Styles.whiteDarkText)
            cellFormat {
                text = it.name

                textFill = when(it.hasMatch) {
                    false -> Color.rgb(236, 82, 82)
                    true -> Color.rgb(93, 167, 19)
                }
            }

            selectionModel.selectionMode = SelectionMode.SINGLE

            bindSelected(nodeSelectionModel.selectedMethod)

            nodeSelectionModel.selectedClass.onChange {
                if(it != null) {
                    items.setAll(it.methods)
                }
            }

        }

        /**
         * Field List
         */
        listview<Field> {
            addClass(Styles.whiteDarkText)
            cellFormat {
                text = it.name

                textFill = when(it.hasMatch) {
                    false -> Color.rgb(236, 82, 82)
                    true -> Color.rgb(93, 167, 19)
                }
            }

            selectionModel.selectionMode = SelectionMode.SINGLE

            bindSelected(nodeSelectionModel.selectedField)

            nodeSelectionModel.selectedClass.onChange {
                if(it != null) {
                    items.setAll(it.fields)
                }
            }
        }

        setDividerPositions(0.6, 0.8)
    }

    init {
        /**
         * Update selection model selected type.
         */
        nodeSelectionModel.selectedClass.onChange {
            if(it != null) {
                controller.selectedType.value = SelectedType.CLASS
            }
        }

        nodeSelectionModel.selectedMethod.onChange {
            if(it != null) {
                controller.selectedType.value = SelectedType.METHOD
            }
        }

        nodeSelectionModel.selectedField.onChange {
            if(it != null) {
                controller.selectedType.value = SelectedType.FIELD
            }
        }
    }
}