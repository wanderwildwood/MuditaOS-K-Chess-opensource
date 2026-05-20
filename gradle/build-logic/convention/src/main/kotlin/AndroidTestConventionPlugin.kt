import com.android.build.gradle.TestExtension
import com.mudita.configureAndroid
import com.mudita.configureKotlinAndroid
import com.mudita.kotlinAndroidPluginId
import com.mudita.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.test")
            apply(libs.kotlinAndroidPluginId)
        }

        extensions.configure<TestExtension> {
            configureAndroid(this)
            configureKotlinAndroid()
        }
    }
}
