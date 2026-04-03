plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") // Added: This is the missing link!
    id("com.google.gms.google-services")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.adam.citybuddy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.adam.citybuddy"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        mlModelBinding = false // Keep false to use your TFLiteHelper manual loader
    }

    packaging {
        resources {
            pickFirsts += "**/AndroidManifest.xml"
            pickFirsts += "**/libtensorflowlite_jni.so"
            // These lines prevent the 20+ AAR Metadata errors
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE*"
            excludes += "META-INF/NOTICE*"
            excludes += "META-INF/*.kotlin_module"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // Core Android & UI
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // TensorFlow Lite - Core Engine Only
    // Removing 'support' and 'metadata' libraries stops the version conflicts.
    implementation("org.tensorflow:tensorflow-lite:2.16.1")
    implementation("org.tensorflow:tensorflow-lite-api:2.16.1")

    // Compose BOM & Material
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation(libs.androidx.compose.ui.tooling)
}