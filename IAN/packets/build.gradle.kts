import artemis.agent.gradle.configure
import artemis.agent.gradle.configureTests
import artemis.agent.gradle.dependsOnKonsist

plugins {
    alias(conventions.plugins.ian.library)
    alias(conventions.plugins.fixtures)
    alias(conventions.plugins.pitest)
    alias(libs.plugins.ksp)
    alias(libs.plugins.javaagent)
}

configureTests(maxMemoryGb = 4)

pitest {
    configure(rootPackage = "com.walkertribe.ian", threads = 8)
    jvmArgs = listOf("-Xmx8g", "-Xms1g", "-XX:+HeapDumpOnOutOfMemoryError", "-XX:+UseParallelGC")
}

kover.currentProject.instrumentation.excludedClasses.addAll("io.ktor.utils.io.*", "kotlinx.io.*")

dependsOnKonsist()

dependencies {
    compileOnly(projects.ian.annotations)

    api(projects.ian.enums)
    api(projects.ian.grid)
    api(projects.ian.listener)
    api(projects.ian.util)
    api(projects.ian.vesseldata)
    api(projects.ian.world)

    api(libs.bundles.ian.packets.api)

    ksp(projects.ian.processor)

    implementation(libs.kotlin.reflect)

    testImplementation(testFixtures(projects.ian.listener))
    testImplementation(testFixtures(projects.ian.vesseldata))
    testImplementation(testFixtures(projects.ian.world))

    testFixturesApi(projects.ian.enums)
    testFixturesApi(projects.ian.listener)
    testFixturesApi(projects.ian.util)
    testFixturesApi(projects.ian.vesseldata)
    testFixturesApi(projects.ian.world)

    testFixturesApi(libs.kotlinx.io)

    testFixturesImplementation(testFixtures(projects.ian.enums))
    testFixturesImplementation(testFixtures(projects.ian.listener))
    testFixturesImplementation(testFixtures(projects.ian.util))
    testFixturesImplementation(testFixtures(projects.ian.vesseldata))
    testFixturesImplementation(testFixtures(projects.ian.world))

    testFixturesImplementation(platform(libs.kotest.bom))
    testFixturesImplementation(libs.bundles.ian.packets.test.fixtures)

    testImplementation(platform(libs.kotest.bom))
    testImplementation(libs.bundles.ian.packets.test)
    testRuntimeOnly(libs.bundles.ian.test.runtime)
    testJavaagent(libs.byte.buddy.agent)

    pitest(testFixtures(projects.ian.enums))
    pitest(testFixtures(projects.ian.listener))
    pitest(testFixtures(projects.ian.util))
    pitest(testFixtures(projects.ian.vesseldata))
    pitest(testFixtures(projects.ian.world))
}
