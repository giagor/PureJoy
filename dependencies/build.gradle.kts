plugins {
    id("com.android.library")
    id("kotlin-android")
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
}

dependencies {
    api(Deps.AndroidX.appCompat)
    api(Deps.AndroidX.constraintLayout)
    api(Deps.Ktx.coreKtx)
    api(Deps.Ktx.fragment)
    api(Deps.material)

    api(Deps.okhttp)
    api(Deps.retrofit)
    api(Deps.retrofitGsonConverter)

    // Kotlin Coroutines
    api(Deps.Coroutines.coroutinesCore)
    api(Deps.Coroutines.coroutinesAndroid)

    // glide
    api(Deps.gilde)

    // RecyclerView - BaseRecyclerViewAdapterHelper
    api(Deps.baseRecyclerViewAdapterHelper)
    // RecyclerView - GravitySnapHelper
    api(Deps.gravitySnapHelper)
}