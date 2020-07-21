plugins {
    id("org.openjfx.javafxplugin") version Plugin.openjfx
    maven
    `maven-publish`
}

javafx {
    version = "11"
    modules = listOf("javafx.controls", "javafx.graphics", "javafx.base", "javafx.swing", "javafx.web")
}

dependencies {
    implementation(Library.tornadofx)
    implementation(Library.fxframe)
    api(project(":mapper"))
    api(Library.asm)
    api(Library.asmCommons)
    api(Library.asmUtil)
    api(Library.asmTree)
}