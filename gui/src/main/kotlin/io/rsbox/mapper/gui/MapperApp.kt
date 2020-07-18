package io.rsbox.mapper.gui

import io.rsbox.mapper.gui.view.MapperView
import javafx.scene.image.Image
import tornadofx.App
import tornadofx.importStylesheet
import tornadofx.launch
import tornadofx.setStageIcon

class MapperApp : App(MapperView::class, Styles::class) {

    init {
        setStageIcon(Image("/icon.png"))
        importStylesheet("/style.css")
    }

    companion object {
        /**
         * Launch the mapper FX application
         */
        fun launch() {
            launch<MapperApp>()
        }
    }
}