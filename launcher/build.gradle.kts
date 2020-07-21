plugins {
    id("com.github.johnrengelman.shadow") version Plugin.shadowjar
    application
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveName = "mapper.jar"
}

application {
    mainClassName = "io.rsbox.mapper.launcher.Launcher"
}

dependencies {
    implementation(project(":gui"))
    implementation(project(":mapper"))
    implementation(project(":common"))
}