plugins {
    id("fabric-loom") version "1.2-SNAPSHOT"
    id("io.github.juuxel.loom-quiltflower") version "1.+"
    id("io.github.p03w.machete") version "1.+"
    id("org.cadixdev.licenser") version "0.6.1"
}

apply(from = "https://raw.githubusercontent.com/JamCoreModding/gronk/main/publishing.gradle.kts")
apply(from = "https://raw.githubusercontent.com/JamCoreModding/gronk/main/misc.gradle.kts")

val mod_version: String by project

group = "io.github.jamalam360"
version = mod_version

repositories {
    val mavenUrls = mapOf(
        Pair("https://maven.terraformersmc.com/releases", listOf("com.terraformersmc", "dev.emi")),
        Pair("https://api.modrinth.com/maven", listOf("maven.modrinth")),
        Pair("https://maven.jamalam.tech/releases", listOf("io.github.jamalam360")),
        Pair("https://jitpack.io/", listOf("com.github.LlamaLad7")),
        Pair("https://ladysnake.jfrog.io/artifactory/mods", listOf("dev.onyxstudios.cardinal-components-api")),
        Pair("https://maven.quiltmc.org/repository/release", listOf("org.quiltmc")),
        Pair("https://maven.quiltmc.org/repository/snapshot", listOf("org.quiltmc", "org.quiltmc.unpick")),
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
    mappings(variantOf(libs.quilt.mappings) { classifier("intermediary-v2") })

    modImplementation(libs.bundles.fabric)
    modImplementation(libs.bundles.required)
    modImplementation(libs.bundles.optional)
    modImplementation(libs.bundles.runtime)

    include(libs.tutorial.lib)
    include(libs.mixin.extras)
    annotationProcessor(libs.mixin.extras)
}

tasks {
    getByName("modrinth") {
        dependsOn("optimizeOutputsOfRemapJar")
    }

    getByName("curseforge") {
        dependsOn("optimizeOutputsOfRemapJar")
    }
}
