import android.annotation.SuppressLint

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = 30

    defaultConfig {
        minSdk = 21
        targetSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    implementation("com.android.support:palette-v7:28.0.0")
    implementation(project(":common"))
    implementation(project(":dependencies"))
    testImplementation(TestDeps.Local.junit)
    androidTestImplementation(TestDeps.Instrumentation.espresso)
    androidTestImplementation(TestDeps.Instrumentation.junitExtension)
}