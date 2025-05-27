import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
}

val versionFile = rootProject.file("version.properties")
val localPropertiesFile = rootProject.file("local.properties")

val localProperties = Properties()
val versionProperties = Properties()

localProperties.load(FileInputStream(localPropertiesFile))
versionProperties.load(FileInputStream(versionFile))


val localVersionName = versionProperties.getProperty("VERSION_NAME")
val localVersionCode = versionProperties.getProperty("VERSION_CODE")

android {
    namespace = "com.venom.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        buildConfigField("String", "APP_VERSION_NAME", "\"${localVersionName}\"")
        buildConfigField("String", "VERSION_CODE", "\"${localVersionCode}\"")
        buildConfigField("String", "OCR_API_KEY", localProperties.getProperty("OCR_API_KEY"))
        buildConfigField("String", "OPENAI_API_KEY", localProperties.getProperty("OPENAI_API_KEY"))
        buildConfigField("String", "GEMINI_API_KEY", localProperties.getProperty("GEMINI_API_KEY"))

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
        buildConfig = true
    }
}

dependencies {

    //api(libs.play.services.ads)

    // Core Modules
    implementation(project(":core:domain"))
    implementation(project(":core:utils"))
    implementation(project(":core:resources"))

    // Hilt
    api(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    api(libs.hilt.navigation.compose)

    implementation(libs.androidx.datastore)

    // Room
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    api(libs.converter.gson)
    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.serialization.json)


    // Testing
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
}