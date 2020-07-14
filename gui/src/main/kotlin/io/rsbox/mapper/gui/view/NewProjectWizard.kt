package io.rsbox.mapper.gui.view

import io.rsbox.mapper.gui.model.ProjectModel
import tornadofx.*

/**
 * The new project wizard window.
 */
class NewProjectWizard : Wizard("New Project", "Create new mapper project") {

    init {

    }

    /**
     * Step view models
     */
    private class ProjectInformationStep : View("Project Information") {

        /**
         * Injected project model
         */
        private val project: ProjectModel by inject()

        /**
         * Project information fields.
         */
        override val root = form {
            fieldset(title) {
                field("Project Name") {
                    textfield(project.name) {
                        required()
                    }
                }
            }
        }
    }
}