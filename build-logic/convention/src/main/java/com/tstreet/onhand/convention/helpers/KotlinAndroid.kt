import com.android.build.api.dsl.CommonExtension
import com.tstreet.onhand.convention.COMPILE_ANDROID_SDK
import com.tstreet.onhand.convention.MIN_ANDROID_SDK
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

/**
 * Configures base Kotlin with Android options
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = COMPILE_ANDROID_SDK

        defaultConfig {
            minSdk = MIN_ANDROID_SDK
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
            // TODO: look into later...
            //  allows developers to use more APIs without requiring a minimum API level for app
            // isCoreLibraryDesugaringEnabled = true
        }

        kotlinOptions {
            // Treat all Kotlin warnings as errors (disabled by default)
            // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
            val warningsAsErrors: String? by project
            allWarningsAsErrors = warningsAsErrors.toBoolean()

            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                // Enable experimental coroutines APIs, including Flow
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
                "-opt-in=kotlin.Experimental",
//                // Enables compose compiler metrics/reports for all modules
//                "-P",
//                "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=/Users/tstreet/Desktop",
//                "-P",
//                "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=/Users/tstreet/Desktop"
            )

            // Set JVM target to 11
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }
}

fun CommonExtension<*, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}
