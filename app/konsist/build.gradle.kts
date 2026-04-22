import com.android.build.api.dsl.LibraryExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    alias(libs.plugins.detekt)
    alias(libs.plugins.dependency.analysis)
}

val sdkVersion: Int by rootProject.extra
val minimumSdkVersion: Int by rootProject.extra
val javaVersion: JavaVersion by rootProject.extra

extensions.configure<LibraryExtension> {
    namespace = "artemis.agent.konsist"
    compileSdk = sdkVersion

    defaultConfig { minSdk = minimumSdkVersion }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    lint.sarifReport = true

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(javaVersion.toString())
            javaParameters = true
        }
    }

    tasks.withType<Test>().configureEach { useJUnitPlatform() }
}

dependencies {
    testImplementation(projects.app)

    testImplementation(platform(libs.kotest.bom))
    testImplementation(libs.bundles.konsist.app)
    testImplementation(libs.bundles.konsist.common)
    testRuntimeOnly(libs.bundles.konsist.runtime)

    constraints {
        androidLintTool(libs.bouncycastle) {
            because("Version 1.84 patches five moderate security vulnerabilities")
        }
        androidLintTool(libs.commons.lang3) {
            because("Version 3.18 fixes an uncontrolled recursion error")
        }
        androidLintTool(libs.httpclient) { because("Version 4.5.13 patches an XSS vulnerability") }
    }
}

dependencyAnalysis { issues { ignoreSourceSet("androidTest") } }
