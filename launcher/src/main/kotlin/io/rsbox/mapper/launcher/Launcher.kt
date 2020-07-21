package io.rsbox.mapper.launcher

import io.rsbox.mapper.gui.MapperApp
import org.tinylog.kotlin.Logger

object Launcher {

    @JvmStatic
    fun main(args: Array<String>) {
        Logger.info("Launching RSBox matcher application.")

        /**
         * Launch the Gui.
         */
        MapperApp.launch()
    }
}