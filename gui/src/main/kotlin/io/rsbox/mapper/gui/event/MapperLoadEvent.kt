package io.rsbox.mapper.gui.event

import io.rsbox.mapper.mapper.Mapper
import tornadofx.FXEvent

class MapperLoadEvent(val mapper: Mapper) : FXEvent()