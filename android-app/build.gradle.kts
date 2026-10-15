plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.github.mixture.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.mixture.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":mixture-termux"))
    implementation(project(":mixture-hands"))
    implementation(project(":mixture-shizuku"))
    implementation(project(":mixture-ui"))
    implementation(project(":mixture-model"))
}
