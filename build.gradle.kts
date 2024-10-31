plugins {
    kotlin("jvm") version "2.0.20"
}

group = "com.dsw"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.16")

    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    testImplementation("com.squareup.okhttp3:okhttp:4.12.0")
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    testImplementation("com.google.code.gson:gson:2.11.0")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}