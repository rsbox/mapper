package io.rsbox.mapper.gui.model

import tornadofx.getProperty
import tornadofx.property

class ProjectModel {
    /**
     * Project Name
     */
    var name by property<String>()
    fun nameProperty() = getProperty(ProjectModel::name)

    /**
     * Mapped Jar
     */
    var mappedJarPath by property<String>()
    fun mappedJarPathProperty() = getProperty(ProjectModel::mappedJarPath)

    /**
     * Target Jar
     */
    var targetJarPath by property<String>()
    fun targetJarPathProperty() = getProperty(ProjectModel::targetJarPath)
}