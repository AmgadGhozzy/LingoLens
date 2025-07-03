plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
}

android {
    namespace = "com.venom.wordcraftai"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
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

    // Core Modules
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
    implementation(project(":core:resources"))
    implementation(project(":core:di"))

    // Hilt
    api(libs.hilt.android)
    implementation(libs.androidx.media3.exoplayer)
    kapt(libs.hilt.android.compiler)
    api(libs.hilt.navigation.compose)

    implementation(libs.logging.interceptor)

    // Room
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Coil for image loading
    implementation(libs.coil.compose)
    
    // OkHttp logging interceptor for debugging
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
}