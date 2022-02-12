// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://plugins.gradle.org/m2/") }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath(Deps.AboutPlugin.pluginClassPath)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}


tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}