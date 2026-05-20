import com.android.build.gradle.LibraryExtension
import com.mudita.androidLibraryPluginId
import com.mudita.configureAndroidCompose
import com.mudita.kotlinComposePluginId
import com.mudita.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(libs.androidLibraryPluginId)
            apply(libs.kotlinComposePluginId)
        }

        extensions.configure<LibraryExtension> {
            configureAndroidCompose(this)
        }
    }
}
