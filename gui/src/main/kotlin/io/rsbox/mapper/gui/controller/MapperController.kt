package io.rsbox.mapper.gui.controller

import io.rsbox.mapper.gui.SelectedType
import io.rsbox.mapper.gui.event.MapperLoadEvent
import io.rsbox.mapper.gui.model.ProjectModel
import io.rsbox.mapper.gui.view.MapperView
import io.rsbox.mapper.mapper.Mapper
import javafx.beans.property.SimpleObjectProperty
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

    var selectedType = SimpleObjectProperty<SelectedType>()

    private val mapperView: MapperView by inject()

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

        mapperView.title = "Mapper - ${project.name}"

        /**
         * Fire the mapper loaded event
         */
        fire(MapperLoadEvent(mapper))
    }
}