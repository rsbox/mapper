plugins {
    application
}

application {
    mainClassName = "io.rsbox.mapper.launcher.Launcher"
}

dependencies {
    implementation(project(":gui"))
    implementation(project(":mapper"))
    implementation(project(":common"))
}