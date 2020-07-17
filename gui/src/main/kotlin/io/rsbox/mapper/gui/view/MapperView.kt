package io.rsbox.mapper.gui.view

import io.rsbox.mapper.gui.controller.MapperController
import io.rsbox.mapper.gui.event.MapperLoadEvent
import io.rsbox.mapper.gui.event.ProjectCreationEvent
import io.rsbox.mapper.mapper.asm.Class
import io.rsbox.mapper.mapper.asm.Field
import io.rsbox.mapper.mapper.asm.Method
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.scene.control.SelectionMode
import org.tinylog.kotlin.Logger
import tornadofx.*
import kotlin.system.exitProcess

/**
 * The primary GUI FX view.
 */
class MapperView : View("RSBox Mapper") {

    /**
     * The injected mapper controller instance
     */
    private val mapperController: MapperController by inject()

    /**
     * The selected mapped class
     */
    private var selectedMappedClass = SimpleObjectProperty<Class>()

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
            prefWidth = 150.0

            /**
             * Class List
             */
            listview<Class> {
                cellFormat {
                    text = it.name
                }

                selectionModel.selectionMode = SelectionMode.SINGLE

                bindSelected(selectedMappedClass)

                subscribe<MapperLoadEvent> { e ->
                    items.setAll(e.mapper.mappedGroup.classes)
                }
            }

            /**
             * Method List
             */
            listview<Method> {
                cellFormat {
                    text = it.name
                }

                selectionModel.selectionMode = SelectionMode.SINGLE

                selectedMappedClass.onChange {
                    if(it != null) {
                        items.setAll(it.methods)
                    }
                }

            }

            /**
             * Field List
             */
            listview<Field> {
                cellFormat {
                    text = it.name
                }

                selectionModel.selectionMode = SelectionMode.SINGLE

                selectedMappedClass.onChange {
                    if(it != null) {
                        items.setAll(it.fields)
                    }
                }
            }

            setDividerPositions(0.6, 0.8)
        }
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