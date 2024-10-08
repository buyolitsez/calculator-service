plugins {
    kotlin("jvm") version "2.0.10"
    kotlin("plugin.serialization") version "2.0.20"
    id("io.ktor.plugin") version "3.0.0-rc-1"
    id("com.diffplug.spotless") version "7.0.0.BETA2"
}

group = "com.github.heheteam"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.github.heheteam.MainKt")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("com.michael-bull.kotlin-result:kotlin-result:2.0.0")

    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-cors:2.3.12")

    implementation("ch.qos.logback:logback-classic:1.5.8")

    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")

    testImplementation("io.ktor:ktor-server-tests:2.3.12")
    testImplementation("io.ktor:ktor-client-content-negotiation:2.3.12")
    testImplementation("io.ktor:ktor-client-mock:2.3.12")
    testImplementation("io.ktor:ktor-server-test-host:2.3.12")
    implementation("io.ktor:ktor-server-call-logging")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(18)
}

spotless {
    kotlin {
        ktlint("1.0.0")
            .setEditorConfigPath("$projectDir/.editorconfig")
            .editorConfigOverride(
                mapOf(
                    "indent_size" to 4,
                    "ktlint_code_style" to "intellij_idea"
                )
            )
            .customRuleSets(
                listOf(
                    "io.nlopez.compose.rules:ktlint:0.3.3"
                )
            )
        target("**/*.kt")
        val spotlessFiles: String? = findProperty("spotlessFiles") as String?
        if (spotlessFiles != null) {
            val filesList = spotlessFiles.split("\n").map { it.trim() }
            println("Spotless files: $filesList")
            target(filesList)
        }
    }
}
