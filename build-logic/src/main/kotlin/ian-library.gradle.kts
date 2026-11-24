import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-library")
    id("kotlin")
    id("io.gitlab.arturbosch.detekt")
    id("com.autonomousapps.dependency-analysis")
}

val javaVersion = rootProject.extra["javaVersion"] as JavaVersion

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(javaVersion.toString())
        javaParameters = true
    }
}

findProject(relativeProjectPath("konsist"))?.also { konsist ->
    tasks
        .named { it.startsWith("assemble") }
        .configureEach { dependsOn("${konsist.path}:test") }
}

dependencies {
    constraints {
        detekt(libs.kotlin.stdlib) {
            because("Detekt CLI uses outdated, deprecated Kotlin version")
        }
    }
}
