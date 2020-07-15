package io.rsbox.mapper.gui.view

import io.rsbox.mapper.gui.event.ProjectCreationEvent
import io.rsbox.mapper.gui.model.ProjectModel
import javafx.geometry.Pos
import javafx.stage.FileChooser
import org.tinylog.kotlin.Logger
import tornadofx.*
import java.io.File

/**
 * The new project wizard window.
 */
class NewProjectView : View("New Project") {

    /**
     * The project model data object.
     */
    private val projectModel = ProjectModel()

    override val root = form {
        setPrefSize(400.0, 50.0)

        fieldset("Create a new project") {

            /**
             * Project name
             */
            field("Project Name") {
                textfield(projectModel.nameProperty())
            }

            /**
             * Mapped JAR File
             */
            field("Mapped Jar") {
                textfield {
                    isEditable = false
                    bind(projectModel.mappedJarPathProperty())
                }

                button("Browse").action {
                    val jarFile = chooseJarFile("Select Mapped Jar")
                    projectModel.mappedJarPath = jarFile.absolutePath
                }
            }

            /**
             * Target JAR File
             */
            field("Target Jar") {
                textfield {
                    isEditable = false
                    bind(projectModel.targetJarPathProperty())
                }

                button("Browse").action {
                    val jarFile = chooseJarFile("Select Target Jar")
                    projectModel.targetJarPath = jarFile.absolutePath
                }
            }
        }

        hbox {
            alignment = Pos.CENTER_RIGHT
            spacing = 10.0

            button("Cancel").action {
                Logger.info("Closing new project window.")
                close()
            }

            button("Create") {
                setOnAction {
                    fire(ProjectCreationEvent(projectModel))
                    close()
                }

                disableProperty().bind(projectModel.nameProperty().isBlank())
                disableProperty().bind(projectModel.mappedJarPathProperty().isBlank())
                disableProperty().bind(projectModel.targetJarPathProperty().isBlank())
            }
        }
    }

    /**
     * Opens a jar file picker.
     */
    private fun chooseJarFile(title: String): File {
        return chooseFile(title, arrayOf(FileChooser.ExtensionFilter("Jar", "*.jar")), mode = FileChooserMode.Single).first()
    }
}