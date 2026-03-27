plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.venom.wordcraftai"
    compileSdk = 35

    lint {
        disable += "NullSafeMutableLiveData"
    }

    defaultConfig {
        minSdk = 24

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
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
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
    ksp(libs.hilt.android.compiler)
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