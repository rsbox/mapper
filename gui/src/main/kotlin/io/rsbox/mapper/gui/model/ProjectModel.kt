package io.rsbox.mapper.gui.model

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import java.io.File

data class Project(
    val name: String,
    val mappedRevision: String,
    val targetRevision: String,
    val mappedJar: File,
    val targetJar: File
)

class ProjectModel : ItemViewModel<Project>() {
    val name = bind { SimpleStringProperty(item.name) }
    val mappedRevision = bind { SimpleStringProperty(item.mappedRevision) }
    val targetRevision = bind { SimpleStringProperty(item.targetRevision) }
    val mappedJar = bind { SimpleObjectProperty<File>(item.mappedJar) }
    val targetJar = bind { SimpleObjectProperty<File>(item.targetJar) }
}