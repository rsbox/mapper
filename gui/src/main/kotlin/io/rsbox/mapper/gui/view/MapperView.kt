package io.rsbox.mapper.gui.view

import org.tinylog.kotlin.Logger
import tornadofx.*
import kotlin.system.exitProcess

/**
 * The primary GUI FX view.
 */
class MapperView : View("RSBox Mapper") {

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

    private fun newProject() {
        Logger.info("Opening new project window.")
        NewProjectWizard().openModal()
    }
}