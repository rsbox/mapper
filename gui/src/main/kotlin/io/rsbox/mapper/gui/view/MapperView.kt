package io.rsbox.mapper.gui.view

import io.rsbox.mapper.gui.controller.MapperController
import io.rsbox.mapper.gui.event.ProjectCreationEvent
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