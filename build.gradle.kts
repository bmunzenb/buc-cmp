plugins {
    kotlin("jvm") version "1.8.10"
}

group = "com.munzenberger.buc"
version = "SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}
