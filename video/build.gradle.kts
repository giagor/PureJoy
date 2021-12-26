plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = Build.compileSdkVersion

    defaultConfig {
        minSdk = Build.minimumSdkVersion
        targetSdk = Build.targetSdkVersion

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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Deps.Compose.version
    }
    kapt {
        arguments {
            arg("AROUTER_MODULE_NAME", project.getName())
        }
    }
}

dependencies {
    implementation(project(":dependencies"))
    implementation(project(":common"))

    implementation(Deps.Compose.composeUiToolingPreview)
    implementation(Deps.Compose.composeActivity)
    implementation(Deps.Compose.constraintLayoutDSL)
    implementation(Deps.Coil.coilCore)
    implementation(Deps.Coil.composeExtension)
    implementation(Deps.ExoPlayer.core)
    implementation(Deps.ExoPlayer.dash)
    implementation(Deps.ExoPlayer.ui)
    implementation(Deps.Accompanist.pager)
    implementation(Deps.Accompanist.accompanistInsets)

    implementation("androidx.paging:paging-runtime-ktx:3.1.0")
    implementation("androidx.paging:paging-compose:1.0.0-alpha14")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0")

    debugImplementation(Deps.Compose.uiDebugTool)
    androidTestImplementation(Deps.Compose.junitTest)

    testImplementation(TestDeps.Local.junit)
    androidTestImplementation(TestDeps.Instrumentation.espresso)
    androidTestImplementation(TestDeps.Instrumentation.junitExtension)

    // ARouter-kapt
    kapt(Deps.ARouter.arouterCompile)
}