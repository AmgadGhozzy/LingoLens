import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("dagger.hilt.android.plugin")
}

val versionFile = rootProject.file("version.properties")

val localProperties = Properties()
val versionProperties = Properties()
localProperties.load(FileInputStream(rootProject.file("local.properties")))
versionProperties.load(FileInputStream(versionFile))


val localVersionName = versionProperties.getProperty("APP_VERSION_NAME")
val localVersionCode = versionProperties.getProperty("APP_VERSION_CODE")

android {
    namespace = "com.venom.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        buildConfigField("String", "APP_VERSION_NAME", "\"${localVersionName}${localVersionCode}\"")
        buildConfigField("int", "APP_VERSION_CODE", localVersionCode)
        buildConfigField("String", "OCR_API_KEY", localProperties.getProperty("OCR_API_KEY"))
        buildConfigField("String", "OPENAI_API_KEY", localProperties.getProperty("OPENAI_API_KEY"))
        buildConfigField("String", "GEMINI_API_KEY", localProperties.getProperty("GEMINI_API_KEY"))
        buildConfigField("String", "GROQ_API_KEY", localProperties.getProperty("GROQ_API_KEY"))
        buildConfigField("String", "DEEPSEEK_API_KEY", localProperties.getProperty("DEEPSEEK_API_KEY"))
        buildConfigField("String", "HUGGINGFACE_API_KEY", localProperties.getProperty("HUGGINGFACE_API_KEY"))
        buildConfigField("String", "SUPABASE_API_KEY", localProperties.getProperty("SUPABASE_API_KEY"))
        buildConfigField("String", "SUPABASE_URL", localProperties.getProperty("SUPABASE_URL"))
        buildConfigField("String", "SUPABASE_ANON_KEY", localProperties.getProperty("SUPABASE_ANON_KEY"))

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

    buildFeatures {
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {

    //api(libs.play.services.ads)

    // Core Modules
    implementation(project(":core:domain"))
    implementation(project(":core:utils"))
    implementation(project(":core:resources"))
    implementation(project(":core:analytics"))

    // Hilt
    api(libs.hilt.android)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.firebase.crashlytics.buildtools)
    api(platform(libs.firebase.bom))
    api(libs.firebase.config.ktx)


    ksp(libs.hilt.android.compiler)
    api(libs.hilt.navigation.compose)

    // Room
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    api(libs.converter.moshi)
    api(libs.moshi)
    api(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)
    // ML Kit
    implementation(libs.translate)
    implementation(libs.kotlinx.coroutines.play.services) // Added for ML Kit Task.await()
    implementation("com.google.mlkit:text-recognition:16.0.1")

    // OkHttp — used by AIRepositoryImpl for Gemini REST calls
    implementation(libs.okhttp)

    api(libs.androidx.credentials)
    api(libs.androidx.credentials.play.services.auth)
    api(libs.googleid)

    // Supabase
    implementation(libs.supabase.client)
    implementation(libs.supabase.postgrest)
    implementation("io.ktor:ktor-client-okhttp:3.1.3")

    // Testing
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
}