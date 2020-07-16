package io.rsbox.mapper.gui.controller

import io.rsbox.mapper.gui.model.ProjectModel
import io.rsbox.mapper.mapper.Mapper
import org.tinylog.kotlin.Logger
import tornadofx.Controller
import java.io.File

/**
 * The primary mapper application controller
 */
class MapperController : Controller() {

    /**
     * The mapper singleton instance.
     */
    val mapper = Mapper()

    /**
     * Create an load the mapper instance.
     */
    internal fun createProject(project: ProjectModel) {
        Logger.info("Creating new project with name: '${project.name}'.")

        /**
         * Loads the JAR files into the mapper class groups.
         */
        mapper.loadMappedJar(File(project.mappedJarPath))
        mapper.loadTargetJar(File(project.targetJarPath))
    }
}