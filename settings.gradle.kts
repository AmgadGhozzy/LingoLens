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
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "LingoLens"

include(":app")

include(":features:translation")
include(":features:phrase")
include(":features:dialog")
include(":features:wordcard")
include(":features:ocr")


include(":core:ui")
include(":core:data")
include(":core:di")
include(":core:resources")
include(":core:utils")
include(":core:domain")

