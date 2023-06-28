plugins {
    val mewcraft = "1.0.0"
    id("cc.mewcraft.java-conventions") version mewcraft
    id("cc.mewcraft.repository-conventions") version mewcraft
    id("cc.mewcraft.publishing-conventions") version mewcraft
    id("cc.mewcraft.deploy-conventions") version mewcraft
    id("cc.mewcraft.paper-plugins") version mewcraft
}

project.ext.set("name", "ExcellentCrates")

group = "su.nightexpress.excellentcrates"
version = "4.2.0"

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
