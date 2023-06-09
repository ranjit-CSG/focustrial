package dependencies

import org.gradle.api.JavaVersion

object AppConfig {
    const val applicationId = "com.cobantch.focusx1"
    const val compileSdkVersion = 33
    const val minSdkVersion = 24
    const val targetSdkVersion = 33
    //buildVersion will be different for release
    const val buildVersion = 3
    private const val major = 3
    private const val minor = 8
    private const val patch = 0
    //versionName will be different for release
    const val versionName = "develop-$major.$minor.$patch"
    val jvmTarget = JavaVersion.VERSION_1_8
}
