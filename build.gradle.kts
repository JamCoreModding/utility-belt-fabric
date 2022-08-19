plugins {
    id("fabric-loom") version "0.12-SNAPSHOT"
    id("org.quiltmc.quilt-mappings-on-loom") version "4.2.0"
    id("io.github.juuxel.loom-quiltflower") version "1.7.2"
    id("io.github.p03w.machete") version "1.+"
    id("org.cadixdev.licenser") version "0.6.1"
}

apply(from = "https://raw.githubusercontent.com/JamCoreModding/Gronk/4eb7f60e89c272e1f00090a68d4e90c2f3498aae/publishing.gradle.kts")
apply(from = "https://raw.githubusercontent.com/JamCoreModding/Gronk/main/misc.gradle.kts")

val mod_version: String by project

group = "io.github.jamalam360"
version = mod_version

loom {
    accessWidenerPath.set(project.file("src/main/resources/toolbelt.accesswidener"))
}

repositories {
    val mavenUrls = mapOf(
        Pair("https://maven.terraformersmc.com/releases", listOf("com.terraformersmc", "dev.emi")),
        Pair("https://api.modrinth.com/maven", listOf("maven.modrinth")),
        Pair("https://maven.jamalam.tech/releases", listOf("io.github.jamalam360")),
        Pair("https://ladysnake.jfrog.io/artifactory/mods", listOf("dev.onyxstudios.cardinal-components-api")),
    )

    for (mavenPair in mavenUrls) {
        maven {
            url = uri(mavenPair.key)
            content {
                for (group in mavenPair.value) {
                    includeGroup(group)
                }
            }
        }
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        addLayer(quiltMappings.mappings("org.quiltmc:quilt-mappings:${libs.versions.minecraft.get()}+build.${libs.versions.mappings.build.get()}:v2"))
    })

    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)

    modImplementation(libs.required.jamlib)
    modImplementation(libs.required.trinkets)

    //TODO: Remove on public release
    include(libs.required.jamlib)
    include(libs.required.trinkets)

    modImplementation(libs.optional.mod.menu)

    modLocalRuntime(libs.runtime.lazy.dfu)
}

sourceSets {
    val main = this.getByName("main")

    create("gametest") {
        this.compileClasspath += main.compileClasspath
        this.compileClasspath += main.output
        this.runtimeClasspath += main.runtimeClasspath
        this.runtimeClasspath += main.output
    }
}

loom {
    runs {
        create("gametest") {
            client()
            name("Game Test")
            source(sourceSets.getByName("gametest"))
            vmArg("-Dfabric-api.gametest")
            vmArg("-Dfabric-api.gametest.report-file=${project.buildDir}/junit.xml")
            runDir("build/gametest")
        }
    }
}
