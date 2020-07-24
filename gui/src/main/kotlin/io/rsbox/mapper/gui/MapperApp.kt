package io.rsbox.mapper.gui

import io.rsbox.fxframe.FXFrameApp
import io.rsbox.fxframe.FXFrameSkin
import io.rsbox.mapper.gui.view.MapperView
import javafx.scene.image.Image
import tornadofx.*
class MapperApp : FXFrameApp() {

    override val skin = FXFrameSkin.ARCDARK
    override val view = find<MapperView>()

    init {
        setFXFrameIcon(Image("/icon.png"))
        importStylesheet("/style.css")
    }

    override fun preload() {
        enableMoving()
        enableResizing()
        enableSnapping()
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