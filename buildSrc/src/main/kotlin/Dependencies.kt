import org.gradle.api.JavaVersion

object Project {
    const val version = "0.0.1"
    const val kotlinVersion = "1.3.72"
    const val gradleVersion = "6.3"
    val jvmVersion = JavaVersion.VERSION_11
}

object Plugin {
    const val openjfx = "0.0.9"
}

object Library {
    private object Version {
        const val tinylog = "2.1.2"
        const val tornadofx = "1.7.20"
    }

    const val tinylogApi = "org.tinylog:tinylog-api-kotlin:${Version.tinylog}"
    const val tinylogImpl = "org.tinylog:tinylog-impl:${Version.tinylog}"
    const val tornadofx = "no.tornado:tornadofx:${Version.tornadofx}"
}