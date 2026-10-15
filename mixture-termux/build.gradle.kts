plugins {
    kotlin("jvm") version "2.0.21"
}

group = "io.github.mixture"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}

tasks.test {
    useJUnit()
}
