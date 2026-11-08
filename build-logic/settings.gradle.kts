pluginManagement { repositories { gradlePluginPortal() } }

plugins { id("dev.panuszewski.typesafe-conventions") version "0.12.2" }

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        gradlePluginPortal()
    }
}

rootProject.name = "build-logic"
