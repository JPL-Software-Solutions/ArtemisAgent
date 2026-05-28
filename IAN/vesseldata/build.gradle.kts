import artemis.agent.gradle.configure
import artemis.agent.gradle.configureTests
import artemis.agent.gradle.dependsOnKonsist

plugins {
    alias(conventions.plugins.ian.library)
    alias(conventions.plugins.fixtures)
    id("info.solidsoft.pitest")
}

configureTests()

pitest.configure(rootPackage = "com.walkertribe.ian.vesseldata", threads = 2)

dependsOnKonsist()

dependencies {
    api(projects.ian.enums)
    api(projects.ian.grid)
    api(projects.ian.util)

    api(libs.kotlin.stdlib)
    api(libs.korlibs.serialization)

    implementation(libs.okio)

    testFixturesApi(projects.ian.grid)
    testFixturesImplementation(projects.ian.enums)
    testFixturesImplementation(testFixtures(projects.ian.grid))

    testFixturesImplementation(platform(libs.kotest.bom))
    testFixturesImplementation(libs.bundles.ian.vesseldata.test.fixtures)

    testImplementation(testFixtures(projects.ian.grid))
    testImplementation(platform(libs.kotest.bom))
    testImplementation(libs.bundles.ian.vesseldata.test)
    testRuntimeOnly(libs.bundles.ian.test.runtime)

    pitest(libs.bundles.arcmutate)

    pitest(projects.ian.enums)
    pitest(projects.ian.util)
}
