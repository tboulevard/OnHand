import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.kapt")
            }
            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner =
                        "androidx.test.runner.AndroidJUnitRunner"
                }
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                add("implementation", project(":core:model"))
                add("implementation", project(":core:data:impl"))
                add("implementation", project(":core:network"))
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:common"))
                add("implementation", project(":core:domain"))

                add("implementation", libs.findLibrary("androidx.core.ktx").get())
                add("implementation", libs.findLibrary("androidx.activity.compose").get())
                add("implementation", libs.findLibrary("androidx.compose.ui").get())
                add("implementation", libs.findLibrary("androidx.navigation.compose").get())
                add("implementation", libs.findLibrary("androidx.compose.material3").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.runtime.compose").get())
                add("implementation", libs.findLibrary("dagger").get())
                add("kapt", libs.findLibrary("dagger.compiler").get())

                add("testImplementation", libs.findLibrary("junit4").get())
                add("androidTestImplementation", libs.findLibrary("androidx.test.ext").get())
                add("androidTestImplementation", libs.findLibrary("androidx.test.espresso.core").get())
                add("androidTestImplementation", libs.findLibrary("androidx.compose.ui.test").get())

                // TODO: Tooling preview doesn't work under the `mockDebug` variant, look into later...
                add("debugImplementation", libs.findLibrary("androidx.compose.ui.tooling").get())
                // TODO: debugImplementation doesn't work for this dependency for some reason...look
                //  into later. We use implementation to circumvent the bug for now.
                add("implementation", libs.findLibrary("androidx.compose.ui.tooling.preview").get())
            }
        }
    }
}
