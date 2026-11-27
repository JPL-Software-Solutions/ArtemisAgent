import artemis.agent.gradle.configure
import artemis.agent.gradle.configureTests

plugins {
    alias(conventions.plugins.ian.library)
    alias(conventions.plugins.pitest)
    alias(libs.plugins.kover)
}

configureTests()

kover.useJacoco()

pitest.configure(rootPackage = "com.walkertribe.ian.protocol", threads = 2)

dependencies {
    implementation(libs.kotlinx.io)
    api(libs.bundles.ian.udp.api)

    testImplementation(platform(libs.kotest.bom))
    testImplementation(libs.bundles.ian.udp.test)
    testRuntimeOnly(libs.bundles.ian.test.runtime)
}
