plugins {
    kotlin("jvm") version Project.kotlinVersion
}

tasks.withType<Wrapper> {
    gradleVersion = Project.gradleVersion
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "io.rsbox"
    version = Project.version

    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()

        maven(url = "https://dl.bintray.com/rsbox/fxframe")
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))

        implementation(Library.tinylogApi)
        implementation(Library.tinylogImpl)
    }

    tasks {
        compileKotlin {
            kotlinOptions.jvmTarget = Project.jvmVersion.toString()
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = Project.jvmVersion.toString()
        }
    }
}