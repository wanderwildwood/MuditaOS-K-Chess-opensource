import com.android.build.api.dsl.ApplicationExtension
import com.mudita.androidApplicationPluginId
import com.mudita.configureAndroid
import com.mudita.configureKotlinAndroid
import com.mudita.kotlinAndroidPluginId
import com.mudita.libs
import com.mudita.projectTargetSdk
import com.mudita.setupKover
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(libs.androidApplicationPluginId)
            apply(libs.kotlinAndroidPluginId)
            apply("lint.convention")
            apply("ktlint.convention")
            apply("detekt.convention")
            apply("org.jetbrains.kotlinx.kover")
        }

        extensions.configure<ApplicationExtension> {
            defaultConfig.targetSdk = projectTargetSdk
            configureAndroid(this)
            configureKotlinAndroid()
        }
        extensions.configure<KoverProjectExtension> {
            setupKover(this)
        }
    }
}
