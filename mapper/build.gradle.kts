dependencies {
    api(project(":common"))
    implementation(Library.asm)
    implementation(Library.asmCommons)
    implementation(Library.asmUtil)
    implementation(Library.asmTree)
    implementation(Library.cfr)
    api(Library.coroutines)
}