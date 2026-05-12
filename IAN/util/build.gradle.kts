import artemis.agent.gradle.configure
import artemis.agent.gradle.configureTests
import artemis.agent.gradle.dependsOnKonsist

plugins {
    alias(conventions.plugins.ian.library)
    alias(conventions.plugins.fixtures)
    id("info.solidsoft.pitest")
}

configureTests()

pitest.configure(rootPackage = "com.walkertribe.ian.util", threads = 2)

dependsOnKonsist()

dependencies {
    api(libs.bundles.ian.util.api)
    implementation(libs.bundles.ian.util)

    testImplementation(platform(libs.kotest.bom))
    testImplementation(libs.bundles.ian.util.test)
    testRuntimeOnly(libs.bundles.ian.test.runtime)

    testFixturesImplementation(platform(libs.kotest.bom))
    testFixturesImplementation(libs.bundles.ian.util.test.fixtures)

    pitest(libs.bundles.arcmutate)
}
