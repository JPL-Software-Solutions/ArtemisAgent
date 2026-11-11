plugins {
    id("info.solidsoft.pitest")
}

dependencies {
    pitest(libs.bundles.arcmutate)

    constraints {
        pitest(libs.asm) {
            because("Version 9.11.1 patches four medium-level security vulnerabilities")
        }
        pitest(libs.commons.codec) {
            because("Version 1.23.0 patches a high-level security vulnerability")
        }
    }
}
