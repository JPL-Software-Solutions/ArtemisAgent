plugins { alias(conventions.plugins.konsist.tests) }

dependencies {
    testCompileOnly(projects.ian.annotations)
    testImplementation(libs.bundles.konsist.ian)
}
