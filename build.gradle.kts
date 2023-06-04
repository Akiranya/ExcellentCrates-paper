plugins {
    val version = "1.0.0"
    id("cc.mewcraft.java-conventions") version version
    id("cc.mewcraft.repository-conventions") version version
    id("cc.mewcraft.publishing-conventions") version version
    alias(libs.plugins.indra)
}

group = "su.nightexpress.excellentcrates"
version = "4.2.0"
description = "ExcellentCrates"

repositories {
    maven("https://jitpack.io") {
        content {
            includeGroup("com.github.decentsoftware-eu")
        }
    }
    maven("https://repo.codemc.io/repository/maven-public/") {
        content {
            includeGroup("com.gmail.filoghost.holographicdisplays")
        }
    }
}

dependencies {
    compileOnly("su.nexmedia:NexEngine:2.2.10")

    // server api
    compileOnly(libs.server.paper)

    // my own libs
    compileOnly(libs.mewcore)

    // libs that present as other plugins
    compileOnly(libs.papi)
    compileOnly(libs.vault) {
        exclude("org.bukkit")
    }

    // libs that provides hologram feature
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.2.5")
    compileOnly("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.0")
}

// TODO remove bukkit plugin.yml
/*bukkit {
    main = "su.nightexpress.excellentcrates.ExcellentCrates"
    name = "ExcellentCrates"
    version = "${project.version}"
    description = "Fully customizable crates with rewards. Enjoy."
    apiVersion = "1.17"
    authors = listOf("NightExpress")
    depend = listOf("NexEngine", "MewCore")
    softDepend = listOf(
        "Citizens",
        "HolographicDisplays",
        "DecentHolograms",
        "Vault",
        "PlaceholderAPI"
    )
}*/

tasks {
    jar {
        archiveFileName.set("ExcellentCrates-${project.version}.jar")
        archiveClassifier.set("")
        destinationDirectory.set(file("$rootDir"))
    }
    processResources {
        filesMatching("**/paper-plugin.yml") {
            expand(mapOf(
                "version" to "${project.version}",
                "description" to project.description
            ))
        }
    }
    register("deployJar") {
        doLast {
            exec {
                commandLine("rsync", jar.get().archiveFile.get().asFile.absoluteFile, "dev:data/dev/jar")
            }
        }
    }
    register("deployJarFresh") {
        dependsOn(build)
        finalizedBy(named("deployJar"))
    }
}

indra {
    javaVersions().target(17)
}