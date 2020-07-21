package io.rsbox.mapper.gui.view

import io.rsbox.mapper.gui.NodeSelectionModel
import io.rsbox.mapper.gui.Styles
import io.rsbox.mapper.gui.controller.MapperController
import io.rsbox.mapper.gui.event.MapperLoadEvent
import io.rsbox.mapper.gui.event.ProjectCreationEvent
import io.rsbox.mapper.mapper.asm.Class
import io.rsbox.mapper.mapper.asm.Field
import io.rsbox.mapper.mapper.asm.Method
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Orientation
import javafx.scene.control.SelectionMode
import javafx.scene.paint.Color
import org.tinylog.kotlin.Logger
import tornadofx.*
import kotlin.system.exitProcess

/**
 * The primary GUI FX view.
 */
class MapperView : View("Mapper") {

    /**
     * The injected mapper controller instance
     */
    private val mapperController: MapperController by inject()

    internal val mappedSelection = NodeSelectionModel()
    internal val targetSelection = NodeSelectionModel()

    override val root = borderpane {
        setPrefSize(1280.0, 900.0)

        /**
         * Menu bar
         */
        top = menubar {
            menu("File") {
                item("New Project").action { newProject() }
                item("Open Project")
                separator()
                item("Import Mappings")
                item("Export Mappings")
                separator()
                item("Save")
                item("Save As")
                separator()
                item("Exit").action { exitProcess(0) }
            }
        }

        /**
         * Source matching lists
         */
        left = splitpane(Orientation.VERTICAL) {
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

                bindSelected(mappedSelection.selectedClass)

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

                bindSelected(mappedSelection.selectedMethod)

                mappedSelection.selectedClass.onChange {
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

                bindSelected(mappedSelection.selectedField)

                mappedSelection.selectedClass.onChange {
                    if(it != null) {
                        items.setAll(it.fields)
                    }
                }
            }

            setDividerPositions(0.6, 0.8)
        }

        /**
         * Content Pane
         */
        center = splitpane(Orientation.HORIZONTAL) {
            add(ContentView(mappedSelection).root)
            add(ContentView(targetSelection).root)

            setDividerPositions(0.5)
        }

        /**
         * Matchable list
         */
    }

    init {

        /**
         * Project creation event subscription
         */
        subscribe<ProjectCreationEvent> {
            mapperController.createProject(it.project)
        }
    }

    /**
     * Opens the 'New Project' creation wizard modal.
     */
    private fun newProject() {
        Logger.info("Opening new project window.")
        NewProjectView().openModal()
    }
}