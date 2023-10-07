plugins {
    val mewcraft = "1.0.0"
    id("cc.mewcraft.repo-conventions") version mewcraft
    id("cc.mewcraft.java-conventions") version mewcraft
    id("cc.mewcraft.publishing-conventions") version mewcraft
    id("cc.mewcraft.deploy-conventions") version mewcraft
    id("cc.mewcraft.paper-plugins") version mewcraft
}

project.ext.set("name", "ExcellentCrates")

group = "su.nightexpress.excellentcrates"
version = "4.3.3"

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
    compileOnly("su.nexmedia", "NexEngine", "2.2.10")

    // server api
    compileOnly(libs.server.paper)

    // libs that present as other plugins
    compileOnly(libs.papi)
    compileOnly(libs.vault) {
        exclude("org.bukkit")
    }

    // libs that provides hologram feature
    compileOnly("com.github.decentsoftware-eu", "decentholograms", "2.2.5")
    compileOnly("com.gmail.filoghost.holographicdisplays", "holographicdisplays-api", "2.4.0")

    // adds support for custom items from various plugins
    compileOnly(libs.spatula.item)
}
