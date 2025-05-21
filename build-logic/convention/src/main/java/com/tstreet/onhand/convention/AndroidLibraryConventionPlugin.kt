import com.android.build.gradle.LibraryExtension
import com.tstreet.onhand.convention.TARGET_ANDROID_SDK
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.kapt")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = TARGET_ANDROID_SDK

                testOptions {
                    // Allows us to mock un-mocked classes, like Log
                    unitTests {
                        isReturnDefaultValues = true
                    }
                }
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                add("testImplementation", libs.findLibrary("junit4").get())
                add("testImplementation", libs.findLibrary("mockito-core").get())
                add("testImplementation", libs.findLibrary("mockito-kotlin").get())
            }
        }
    }
}
