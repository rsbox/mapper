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
        const val asm = "8.0.1"
        const val coroutines = "1.3.8"
        const val cfr = "0.150"
    }

    const val tinylogApi = "org.tinylog:tinylog-api-kotlin:${Version.tinylog}"
    const val tinylogImpl = "org.tinylog:tinylog-impl:${Version.tinylog}"
    const val tornadofx = "no.tornado:tornadofx:${Version.tornadofx}"
    const val asm = "org.ow2.asm:asm:${Version.asm}"
    const val asmCommons = "org.ow2.asm:asm-commons:${Version.asm}"
    const val asmUtil = "org.ow2.asm:asm-util:${Version.asm}"
    const val asmTree = "org.ow2.asm:asm-tree:${Version.asm}"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.coroutines}"
    const val cfr = "org.benf:cfr:${Version.cfr}"
}