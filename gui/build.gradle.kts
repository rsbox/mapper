plugins {
    id("org.openjfx.javafxplugin") version Plugin.openjfx
}

javafx {
    version = "11"
    modules = listOf("javafx.controls", "javafx.graphics", "javafx.base", "javafx.swing")
}

dependencies {
    implementation(Library.tornadofx)
    api(project(":mapper"))
    api(Library.asm)
    api(Library.asmCommons)
    api(Library.asmUtil)
    api(Library.asmTree)
}