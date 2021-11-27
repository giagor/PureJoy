object Build {
    const val applicationId = "com.topview.purejoy"

    const val compileSdkVersion = 31
    const val targetSdkVersion = 31
    const val minimumSdkVersion = 21

    const val versionCode = 1
    const val versionName = "1.0"
}

object Deps {
    object Compose {
        const val version = "1.0.5"

        const val composeUi = "androidx.compose.ui:ui:$version"
        const val composeMaterial = "androidx.compose.material:material:$version"
        const val composeUiToolingPreview =
            "androidx.compose.ui:ui-tooling-preview:$version"
        const val livedataRuntime =
            "androidx.compose.runtime:runtime-livedata:$version"
        const val composeNavigation = "androidx.navigation:navigation-compose:2.4.0-beta02"
        const val composeActivity = "androidx.activity:activity-compose:1.4.0"
        const val composeViewBinding = "androidx.compose.ui:ui-viewbinding:$version"

        const val composeRuntime = "androidx.compose.runtime:runtime:$version"
        const val uiDebugTool = "androidx.compose.ui:ui-tooling:$version"
        const val junitTest = "androidx.compose.ui:ui-test-junit4:$version"
    }

    object Navigation {
        const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:2.3.5"
        const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:2.3.5"
    }

    object AndroidX {
        const val appCompat = "androidx.appcompat:appcompat:1.3.1"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.1"
    }

    object Ktx {
        const val coreKtx = "androidx.core:core-ktx:1.7.0"
        const val fragment = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0"
    }

    object Coroutines {
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2"
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2"
    }

    const val accompanistInsets = "com.google.accompanist:accompanist-insets:0.16.0"
    const val material = "com.google.android.material:material:1.4.0"

    const val okhttp = "com.squareup.okhttp3:okhttp:4.9.2"

    const val retrofit = "com.squareup.retrofit2:retrofit:2.9.0"
    // Retrofit的gson转换器
    const val retrofitGsonConverter = "com.squareup.retrofit2:converter-gson:2.9.0"

    const val gilde = "com.github.bumptech.glide:glide:4.12.0"

    // RecyclerView - BaseRecyclerViewAdapterHelper
    const val baseRecyclerViewAdapterHelper = "com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.7"
    // RecyclerView - GravitySnapHelper
    const val gravitySnapHelper = "com.github.rubensousa:gravitysnaphelper:2.2.2"
}
object TestDeps {

    object Local {

        const val junit = "junit:junit:4.13.2"
    }

    object Instrumentation {

        const val junitExtension = "androidx.test.ext:junit:1.1.3"

        const val espresso = "androidx.test.espresso:espresso-core:3.4.0"
    }
}