import com.android.build.api.dsl.ApplicationExtension
import com.mudita.androidApplicationPluginId
import com.mudita.configureAndroidCompose
import com.mudita.kotlinComposePluginId
import com.mudita.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(libs.androidApplicationPluginId)
            apply(libs.kotlinComposePluginId)
        }

        extensions.configure<ApplicationExtension> {
            configureAndroidCompose(this)
        }
    }
}
