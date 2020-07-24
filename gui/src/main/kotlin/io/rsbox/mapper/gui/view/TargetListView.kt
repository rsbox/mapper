package io.rsbox.mapper.gui.view

import io.rsbox.mapper.gui.NodeMatch
import io.rsbox.mapper.gui.NodeSelectionModel
import io.rsbox.mapper.gui.controller.MapperController
import javafx.geometry.Orientation
import javafx.scene.control.SelectionMode
import tornadofx.*

class TargetListView : Fragment() {

    val sourceSelectionModel: NodeSelectionModel by param()
    val targetSelectionModel: NodeSelectionModel by param()

    private val controller: MapperController by inject()

    @Suppress("UNCHECKED_CAST")
    override val root = splitpane(Orientation.VERTICAL) {
        prefWidth = 200.0

        /**
         * The matched entry listview
         */
        listview<NodeMatch> {
            cellFormat {
                text = "${it.score} - ${it.name}"
            }

            selectionModel.selectionMode = SelectionMode.SINGLE

            sourceSelectionModel.selectedClass.onChange {
                if(it == null) {
                    items.removeAll()
                }

                if(controller.classRankedResults.containsKey(it)) {
                    items.setAll(controller.classRankedResults[it]?.map { NodeMatch(it.subject, it.score, it.subject.name) })
                }
            }
        }
    }
}