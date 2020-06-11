pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://files.minecraftforge.net/maven")
    }
    resolutionStrategy {
        eachPlugin {
            "net.minecraftforge.gradle".let {
                if (it == requested.id.id) {
                    useModule("$it:ForgeGradle:${requested.version}")
                }
            }
        }
    }
}

rootProject.name = "Peckish"
