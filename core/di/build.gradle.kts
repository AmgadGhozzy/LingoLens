plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
}

android {
    namespace = "com.venom.di"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    // Core Modules
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:utils"))

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)

    // Hilt
    api(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    api(libs.hilt.navigation.compose)


    implementation(libs.androidx.core.ktx)
}