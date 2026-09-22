plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass = "org.gidradium.reversi.MainKt"
}

tasks.named<Test>("test") {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
