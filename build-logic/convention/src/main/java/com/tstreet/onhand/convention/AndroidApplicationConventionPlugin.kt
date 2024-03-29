import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.tstreet.onhand.convention.TARGET_ANDROID_SDK
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.kapt")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = TARGET_ANDROID_SDK
                // TODO: flavor config here..
                //configureFlavors(this)
            }
            extensions.configure<ApplicationAndroidComponentsExtension> {
                // TODO: apk task config here..
                //configurePrintApksTask(this)
            }
        }
    }
}
