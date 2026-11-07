import artemis.agent.gradle.configure
import artemis.agent.gradle.configureTests
import artemis.agent.gradle.dependsOnKonsist

plugins {
    alias(conventions.plugins.ian.library)
    alias(conventions.plugins.fixtures)
    alias(libs.plugins.javaagent.test)
    id("info.solidsoft.pitest")
}

configureTests()

pitest.configure(rootPackage = "com.walkertribe.ian.iface", threads = 2)

dependsOnKonsist()

dependencies {
    api(libs.kotlin.stdlib)
    api(libs.kotlinx.coroutines)

    implementation(libs.kotlin.reflect)

    testImplementation(platform(libs.kotest.bom))
    testImplementation(libs.bundles.ian.listener.test)
    testFixturesImplementation(libs.kotlin.reflect)
    testRuntimeOnly(libs.bundles.ian.test.runtime)
    testJavaagent(libs.byte.buddy.agent)

    pitest(libs.bundles.arcmutate)

    constraints {
        pitest(libs.asm) {
            because("Version 9.11.1 patches four medium-level security vulnerabilities")
        }
    }
}
