import com.mudita.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.kotlin.dsl.register

class KtLintConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            val ktlintConfiguration = configurations.create("ktlint")

            dependencies.add(ktlintConfiguration.name, libs.findLibrary("ktlint").get())

            tasks.register("ktlint", JavaExec::class) {
                group = "verification"
                description = "Check Kotlin code style."
                classpath = ktlintConfiguration
                mainClass.set("com.pinterest.ktlint.Main")
                inputs.files(fileTree("src").matching {
                    include("**/*.kt")
                    exclude("test/**/*.kt", "androidTest/**/*.kt")
                })
                outputs.file("build/reports/ktlint-report.xml")
                outputs.cacheIf { true }

                // Monochrome.kt and EInkDialog.kt are kept byte-identical with the other apps
                // that carry them, so they keep those apps' style rather than this one's.
                args = listOf(
                    "src/**/*.kt", "!src/test/**/*.kt", "!src/androidTest/**/*.kt",
                    "!src/**/Monochrome.kt", "!src/**/EInkDialog.kt",
                    "--editorconfig=${rootDir}/.codeStyleConfig/.editorconfig",
                    "--reporter=checkstyle,output=build/reports/ktlint-report.xml",
                    "--reporter=plain"
                )

                maxHeapSize = "256m"
            }
        }
    }
}
