package io.rsbox.mapper.gui

import io.rsbox.mapper.gui.view.MapperView
import tornadofx.App
import tornadofx.launch

class MapperApp : App(MapperView::class) {

    companion object {
        /**
         * Launch the mapper FX application
         */
        fun launch() {
            launch<MapperApp>()
        }
    }
}