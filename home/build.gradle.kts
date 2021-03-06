plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.mikepenz.aboutlibraries.plugin")
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
        dataBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Deps.Compose.compilerVersion
    }
    kapt {
        arguments {
            arg("AROUTER_MODULE_NAME", project.name)
        }
    }
}

dependencies {
    implementation(project(":dependencies"))
    implementation(project(":common"))
    implementation(project(":musiclibrary"))
    implementation(project(":video"))

    implementation(Deps.Compose.composeUiToolingPreview)
    implementation(Deps.Compose.livedataRuntime)
    implementation(Deps.Compose.composeActivity)
    implementation(Deps.Compose.composeViewBinding)
    implementation(Deps.Compose.constraintLayoutDSL)
    implementation(Deps.Coil.coilCore)
    implementation(Deps.Coil.composeExtension)

    implementation(Deps.Navigation.fragmentKtx)
    implementation(Deps.Navigation.navigationUiKtx)
    implementation(Deps.Navigation.compose)

    implementation(Deps.Paging.runtime)
    implementation(Deps.Paging.compose)

    implementation(Deps.Accompanist.accompanistInsets)
    implementation(Deps.Accompanist.swipeRefresh)
    implementation(Deps.Accompanist.pager)

    debugImplementation(Deps.Compose.uiDebugTool)
    androidTestImplementation(Deps.Compose.junitTest)

    // About Library Core
    implementation(Deps.AboutPlugin.core)

    testImplementation(TestDeps.Local.junit)
    androidTestImplementation(TestDeps.Instrumentation.espresso)
    androidTestImplementation(TestDeps.Instrumentation.junitExtension)

    // ARouter-kapt
    kapt(Deps.ARouter.arouterCompile)
}