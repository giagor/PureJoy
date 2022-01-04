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
    api(Deps.AndroidX.appCompat)
    api(Deps.AndroidX.constraintLayout)
    api(Deps.Ktx.coreKtx)
    api(Deps.Ktx.viewModelKtx)
    api(Deps.Ktx.fragmentKtx)
    api(Deps.material)

    api(Deps.okhttp)
    api(Deps.retrofit)
    api(Deps.retrofitGsonConverter)

    // Kotlin Coroutines
    api(Deps.Coroutines.coroutinesCore)
    api(Deps.Coroutines.coroutinesAndroid)

    // glide
    api(Deps.glide)
    // compose UI
    api(Deps.Compose.composeUi)
    api(Deps.Compose.composeMaterial)

    // RecyclerView - BaseRecyclerViewAdapterHelper
    api(Deps.baseRecyclerViewAdapterHelper)
    // RecyclerView - GravitySnapHelper
    api(Deps.gravitySnapHelper)

    // Room
    api(Deps.Room.roomRuntime)
    api(Deps.Room.roomKtx)

    // ARouter
    api(Deps.ARouter.arouterApi)

    // LeakCanary
    debugApi(Deps.Debug.leakCanary)
}