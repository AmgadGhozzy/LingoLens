pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven("https://jitpack.io")
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "LingoLens"

include(":app")

include(":features:translation")
include(":features:phrase")
include(":features:dialog")
include(":features:stackcard")
include(":features:quiz")
include(":features:lingospell")
include(":features:quote")
include(":features:wordcraftai")
include(":features:settings")
include(":features:ocr")


include(":core:ui")
include(":core:data")
include(":core:di")
include(":core:resources")
include(":core:utils")
include(":core:domain")

