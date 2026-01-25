plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.25.0"
}

group = "org.graph"
version = "1.0.0"

repositories {
    mavenCentral()
}

val junitVersion = "5.10.2"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("org.graph.graphproject")
    mainClass.set("org.graph.graphproject.GraphApplication")
}

javafx {
    version = "17.0.14"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web")
}

dependencies {
    implementation("org.controlsfx:controlsfx:11.2.1")
    implementation("org.kordamp.bootstrapfx:bootstrapfx-core:0.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {
    imageZip.set(layout.buildDirectory.file("distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "app"
    }
    jpackage {
        imageName = "GraphApp"
        installerName = "GraphApp"
        if (System.getProperty("os.name").contains("Windows", ignoreCase = true)) {
            installerType = "exe"
            installerOptions = listOf("--win-dir-chooser", "--win-shortcut", "--win-menu")
        }
    }
}

val createPortableApp by tasks.registering(Sync::class) {
    group = "distribution"
    description = "folder z aplikacją gotową do uruchomienia bez instalacji."

    dependsOn("jlink")

    from(layout.buildDirectory.dir("image"))

    into(layout.buildDirectory.dir("distributions/GraphApp-Portable"))
}


tasks.jpackage {
    dependsOn("jlink")
    mustRunAfter(createPortableApp)
}

tasks.jlinkZip {
    mustRunAfter("jpackage")
}