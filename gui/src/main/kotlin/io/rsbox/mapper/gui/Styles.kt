package io.rsbox.mapper.gui

import tornadofx.Stylesheet
import tornadofx.c
import tornadofx.cssclass

class Styles : Stylesheet() {

    companion object {
        val blackDark by cssclass()
        val whiteDarkText by cssclass()
    }

    init {
        blackDark {
            backgroundColor += c("#141A1F")
        }

        whiteDarkText {
            textFill = c("#6B859E")
        }
    }

}