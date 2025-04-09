// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false

    id("org.jetbrains.dokka") version "1.9.0"
    // Firebase.
    id("com.google.gms.google-services") version "4.4.2" apply false

}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
}