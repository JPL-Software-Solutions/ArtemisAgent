plugins { id("konsist-tests") }

dependencies {
    testImplementation(projects.ian.enums)
    testImplementation(projects.ian.world)
    testCompileOnly(projects.ian.annotations)

    testImplementation(libs.bundles.konsist.ian)
}
