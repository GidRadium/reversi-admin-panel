import org.gradle.jvm.application.tasks.CreateStartScripts
import org.gradle.jvm.tasks.Jar

plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.sqlite.jdbc)

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

val cliStartScripts by tasks.registering(CreateStartScripts::class) {
    applicationName = "cli"
    mainClass.set("org.gidradium.reversi.presentation.cli.CliMainKt")

    classpath = files(
        tasks.named<Jar>("jar").flatMap { it.archiveFile },
        configurations.runtimeClasspath
    )

    outputDir = layout.buildDirectory
        .dir("generated/cli-start-scripts")
        .get()
        .asFile

    dependsOn("jar")
}

distributions {
    main {
        contents {
            from(cliStartScripts) {
                into("bin")
            }
        }
    }
}

tasks.register<JavaExec>("runCli") {
    group = "application"
    description = "Runs the CLI application"

    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.gidradium.reversi.presentation.cli.CliMainKt")
}
