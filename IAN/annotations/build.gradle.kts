import artemis.agent.gradle.dependsOnKonsist

plugins { alias(conventions.plugins.ian.library) }

dependsOnKonsist()

dependencies { api(libs.kotlin.stdlib) }
