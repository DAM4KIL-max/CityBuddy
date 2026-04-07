plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler)    apply false
    id("org.jetbrains.kotlin.android") version "2.1.21" apply false  // ← ADD THIS
    id("com.google.gms.google-services")   version "4.4.4" apply false
}