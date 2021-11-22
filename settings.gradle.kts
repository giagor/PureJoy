dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() // Warning: this repository is going to shut down soon
        maven {
            isAllowInsecureProtocol = true
            setUrl("https://jitpack.io")
        }
    }
}

rootProject.name = "PureJoy"
include(":app")
include(":dependencies")
include(":common")

include(":musiclibrary")

include(":home")

