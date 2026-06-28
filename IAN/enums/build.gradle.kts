import artemis.agent.gradle.configure
import artemis.agent.gradle.configureTests
import artemis.agent.gradle.dependsOnKonsist

plugins {
    alias(conventions.plugins.ian.library)
    alias(conventions.plugins.fixtures)
    id("info.solidsoft.pitest")
}

configureTests()

kover.useJacoco()

pitest.configure(rootPackage = "com.walkertribe.ian.enums", threads = 2)

dependsOnKonsist()

dependencies {
    api(projects.ian.util)
    api(libs.kotlin.stdlib)

    testImplementation(platform(libs.kotest.bom))
    testImplementation(testFixtures(projects.ian.util))
    testImplementation(libs.bundles.ian.enums.test)
    testRuntimeOnly(libs.bundles.ian.test.runtime)

    pitest(libs.bundles.arcmutate)

    pitest(testFixtures(projects.ian.util))
}
