plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    // Google services para firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "es.masanz.pathfinder"
    compileSdk = 35

    defaultConfig {
        applicationId = "es.masanz.pathfinder"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    // Dependencia para trabajar con OpenStreetMap
    implementation("org.osmdroid:osmdroid-android:6.1.16")

    // Dependencia para compatibilidad con otras versiones android
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Extensiones para código
    implementation("androidx.core:core-ktx:1.12.0")

    // Extensión para la ubicación en tiempo real
    implementation("com.google.android.gms:play-services-location:18.0.0")

    // Firebase authentication y firebase firestore database.
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.3")

    // Dependencia para el navbar
    implementation ("com.google.android.material:material:1.7.0")


    /*Dependencias base*/

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}