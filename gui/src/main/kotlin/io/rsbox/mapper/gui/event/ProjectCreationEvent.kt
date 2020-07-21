package io.rsbox.mapper.gui.event

import io.rsbox.mapper.gui.model.ProjectModel
import tornadofx.FXEvent

class ProjectCreationEvent(val project: ProjectModel) : FXEvent()