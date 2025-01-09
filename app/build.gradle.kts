plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.venom.lingolens"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.venom.lingolens"
        minSdk = 24
        targetSdk = 34
        versionCode = 3
        versionName = "3.0"

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
    }
}

dependencies {


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
    implementation(project(":features:ocr"))


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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

}
