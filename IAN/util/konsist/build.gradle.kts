plugins { id("konsist-tests") }

dependencies {
    testImplementation(projects.ian.util)
    testImplementation(libs.bundles.konsist.ian)
}
