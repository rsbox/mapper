package io.rsbox.mapper.gui.view

import io.rsbox.mapper.gui.NodeSelectionModel
import io.rsbox.mapper.gui.controller.MapperController
import io.rsbox.mapper.gui.event.ProjectCreationEvent
import javafx.geometry.Orientation
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

            menu("Rank") {
                item("Rank All")
                item("Rank Classes").action { mapperController.mapper.classifyClasses(mapperController.mapper.mappedGroup, mapperController.mapper.targetGroup) }
                item("Rank Methods")
                item("Rank Fields")
            }
        }

        /**
         * Source matching lists
         */
        left = find<SourceListView>(SourceListView::nodeSelectionModel to mappedSelection).root

        /**
         * Target match list
         */
        right = find<TargetListView>(mapOf(TargetListView::sourceSelectionModel to mappedSelection, TargetListView::targetSelectionModel to mappedSelection)).root

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