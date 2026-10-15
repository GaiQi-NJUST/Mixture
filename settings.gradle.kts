pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Mixture"
include(":mixture-termux")
include(":mixture-hands")
include(":mixture-shizuku")
include(":mixture-ui")
include(":mixture-model")
include(":android-app")
project(":android-app").projectDir = File("android-app")
