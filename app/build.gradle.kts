import com.android.build.api.dsl.ApplicationExtension
import java.io.FileInputStream
import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("plugin.serialization")
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.firebase.perf)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
    alias(libs.plugins.dependency.analysis)
}

val appName = "Artemis Agent"
val appId = "artemis.agent"
val sdkVersion: Int by rootProject.extra
val minimumSdkVersion: Int by rootProject.extra
val javaVersion: JavaVersion by rootProject.extra
val stringRes = "string"

val release = "release"
val keystoreProperties =
    Properties().apply { load(FileInputStream(rootProject.file("keystore.properties"))) }

val changelog =
    rootProject.file("changelog/whatsnew-en-US").readLines().joinToString(" \\u0020\\n") {
        it.replaceFirst('*', '\u2022').replace("'", "\\'").replace("\"", "\\\"")
    }

val versionProperties =
    Properties().apply { rootProject.file("version.properties").inputStream().use { load(it) } }

extensions.configure<ApplicationExtension> {
    namespace = appId
    compileSdk = sdkVersion

    defaultConfig {
        applicationId = appId
        minSdk = minimumSdkVersion
        targetSdk = sdkVersion
        versionCode = versionProperties.getProperty("versionCode").toInt()
        versionName = versionProperties.getProperty("versionName")

        testInstrumentationRunner = "com.kaspersky.kaspresso.runner.KaspressoRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        isCoreLibraryDesugaringEnabled = true
    }

    lint {
        lintConfig = file("lint.xml")
        sarifReport = true
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.add("-Xannotation-target-all")
            jvmTarget = JvmTarget.fromTarget(javaVersion.toString())
            javaParameters = true
        }
    }

    testOptions.execution = "ANDROIDX_TEST_ORCHESTRATOR"
    testOptions.unitTests.all { it.useJUnitPlatform() }

    signingConfigs {
        create(release) {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storePassword = keystoreProperties["storePassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
        }
    }

    buildTypes {
        configureEach {
            resValue(stringRes, "app_name", appName)
            resValue(stringRes, "app_version", "$appName ${defaultConfig.versionName}")
            resValue(stringRes, "changelog", changelog)
        }
        release {
            signingConfig = signingConfigs.getByName(release)
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            ndk.debugSymbolLevel = "FULL"
        }
    }

    packaging.jniLibs.excludes.add("lib/*/libdatastore_shared_counter.so")

    buildFeatures {
        viewBinding = true
        buildConfig = true
        resValues = true
    }

    tasks.preBuild.configure { dependsOn(":IAN:konsistCollect") }
}

androidComponents {
    onVariants { variant ->
        val variantName = variant.name.replaceFirstChar { it.uppercaseChar() }

        // As of AGP 9, release test tasks are no longer generated, so we depend on the
        // debug task instead
        tasks
            .named { it == "assemble${variantName}" }
            .configureEach { dependsOn(":app:konsist:testDebugUnitTest") }
        tasks
            .named { it.startsWith("ksp$variantName") && it.endsWith("Kotlin") }
            .configureEach { mustRunAfter("generate${variantName}Proto") }
    }
}

dependencies {
    implementation(fileTree(baseDir = "libs") { include("*.jar") })
    implementation(projects.ian)
    implementation(projects.ian.enums)
    implementation(projects.ian.grid)
    implementation(projects.ian.listener)
    implementation(projects.ian.packets)
    implementation(projects.ian.udp)
    implementation(projects.ian.util)
    implementation(projects.ian.vesseldata)
    implementation(projects.ian.world)

    ksp(projects.ian.processor)

    implementation(libs.bundles.app)
    debugImplementation(libs.bundles.app.debug)
    debugRuntimeOnly(libs.bundles.app.debug.runtime)

    testImplementation(testFixtures(projects.ian.packets))
    testImplementation(testFixtures(projects.ian.util))
    testImplementation(testFixtures(projects.ian.vesseldata))

    testImplementation(platform(libs.kotest.bom))
    testImplementation(libs.bundles.app.test)
    testDebugImplementation(libs.bundles.app.test.debug)
    testRuntimeOnly(libs.bundles.app.test.runtime)

    androidTestImplementation(libs.bundles.app.androidTest) {
        exclude(group = "org.hamcrest", module = "hamcrest-core")
        exclude(group = "org.hamcrest", module = "hamcrest-library")
    }
    androidTestUtil(libs.test.orchestrator)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase) {
        exclude(group = "com.google.firebase", module = "protolite-well-known-types")
    }

    constraints {
        implementation(libs.guava) {
            because("Version 32.0.0-android patches a moderate security vulnerability")
        }
        androidTestImplementation(libs.jsoup) {
            because("Version 1.14.2 patches a high-level security vulnerability")
        }
        androidTestImplementation(libs.accessibility.test.framework) {
            because("Needed to resolve static method registerDefaultInstance")
        }
    }

    coreLibraryDesugaring(libs.desugaring)

    lintChecks(libs.lint.security)
}

detekt {
    source.from(files("src/androidTest/kotlin"))
    ignoredBuildTypes = listOf(release)
    ignoredVariants = listOf(release)
}

protobuf {
    protoc { artifact = libs.protoc.get().toString() }

    generateProtoTasks {
        all().configureEach {
            builtins {
                create("java") { option("lite") }
                create("kotlin") { option("lite") }
            }
        }
    }
}
