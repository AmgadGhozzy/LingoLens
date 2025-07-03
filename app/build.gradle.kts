import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.serialization)
    alias(libs.plugins.aboutlibraries)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

val versionFile = rootProject.file("version.properties")

val localProperties = Properties()
val versionProperties = Properties()
localProperties.load(FileInputStream(rootProject.file("local.properties")))
versionProperties.load(FileInputStream(versionFile))

val localVersionCode = versionProperties.getProperty("VERSION_CODE").toInt()

android {
    namespace = "com.venom.lingolens"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.venom.lingolens"
        minSdk = 24
        targetSdk = 34
        versionCode = localVersionCode
        versionName = "3.8.${localVersionCode}"

        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    // Auto increment app version
    gradle.startParameter.taskNames.forEach {
        if (it.contains(":app:assembleRelease")) {
            versionFile.bufferedWriter().use { file ->
                file.write("VERSION_CODE=${(localVersionCode + 1)}")
            }
        }
    }

    tasks.whenTaskAdded {
        if (name == "assembleDebug") {
            doLast {
                exec {
                    commandLine("cmd", "/c", "D:\\Amgad\\unlock_device.bat")
                }
            }
        }
    }
}


dependencies {

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))

    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")

    // Ads
    api(libs.play.services.ads)

    // Core
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
    implementation(project(":core:di"))
    implementation(project(":core:resources"))
    implementation(project(":core:utils"))
    implementation(project(":core:domain"))

    // Features
    implementation(project(":features:translation"))
    implementation(project(":features:phrase"))
    implementation(project(":features:dialog"))
    implementation(project(":features:stackcard"))
    implementation(project(":features:wordcraftai"))
    implementation(project(":features:ocr"))
    implementation(project(":features:settings"))


    // Android Jetpack
    api(libs.androidx.core.ktx)
    api(libs.androidx.lifecycle.runtime.ktx)
    api(platform(libs.compose.bom))
    api(libs.androidx.lifecycle.runtime.compose)
    api(libs.androidx.lifecycle.viewmodel.compose)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.fragment.ktx)

    // Hilt
    api(libs.hilt.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.splashscreen)
    kapt(libs.hilt.android.compiler)
    api(libs.hilt.navigation.compose)

    // Networking
    api(libs.okhttp)
    api(libs.retrofit)
    api(libs.converter.gson)

    // Room
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.serialization.json)


    // Compose
    api(libs.compose.ui)
    api(libs.compose.ui.graphics)
    api(libs.compose.ui.tooling.preview)
    api(libs.compose.material3)
    api(libs.compose.animation)
    api(libs.coil.compose)
    api(libs.compose.material.icons.extended) {
        exclude(group = "androidx.compose.material.icons", module = "filled")
        exclude(group = "androidx.compose.material.icons", module = "outlined")
        exclude(group = "androidx.compose.material.icons", module = "round")
        exclude(group = "androidx.compose.material.icons", module = "sharp")
        exclude(group = "androidx.compose.material.icons", module = "twotone")
    }


    // Kotlin
    api(libs.kotlin.stdlib)
    api(libs.androidx.exifinterface)


    // Testing
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

}
