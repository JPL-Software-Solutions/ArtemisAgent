plugins { alias(conventions.plugins.konsist.tests) }

dependencies {
    testImplementation(projects.ian.util)
    testImplementation(libs.bundles.konsist.ian)
}
