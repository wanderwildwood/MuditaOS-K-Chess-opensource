package com.mudita.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

open class GenerateChangelogTask @Inject constructor(
    private val execOperations: ExecOperations
) : DefaultTask() {
    @Input
    var appName: String = ""

    @Input
    var versionName: String = ""

    @TaskAction
    fun generate() {
        // Retrieve all tags sorted by date, most recent first
        val tagsCommand = listOf("git", "-c", "versionsort.suffix=-rc", "tag", "--sort=-v:refname")
        val tagsOutput = execAndGetOutput(tagsCommand)
        val tags = tagsOutput.split("\n")
        // Extracting the two most recent tags
        val currentVersion = tags.firstOrNull().orEmpty()
        val previousVersion = tags.getOrNull(1) ?: "origin/master"
        val changes = execAndGetOutput(listOf("git", "log", "--pretty=format:%s <%an>", "$previousVersion..$currentVersion"))
        val changelog = buildString {
            append("Changelog")
            if (previousVersion.isNotBlank() && currentVersion.isNotBlank()) {
                append(" from $previousVersion to $currentVersion")
            }
            append("\n\n")

            var features = ""
            var fixes = ""
            var improvements = ""

            changes.lineSequence().forEach { line ->
                val prefix = line.substringBefore(":", "")
                val rest = line.substringAfter(":", "").trim()

                when {
                    prefix.startsWith("ft") -> features += "$rest\n"
                    prefix.startsWith("fi") -> fixes += "$rest\n"
                    prefix.startsWith("im") -> improvements += "$rest\n"
                }
            }

            if (features.isNotBlank()) append("Features:\n$features\n")
            if (fixes.isNotBlank()) append("Fixes:\n$fixes\n")
            if (improvements.isNotBlank()) append("Improvements:\n$improvements\n")
        }

        val fileName = "${appName}-${versionName}"
        val changelogDir = project.file("./build/outputs/changelog")
        val changelogFilePath = "$changelogDir/CHANGELOG-$fileName.md"
        println("Changelog file path: $changelogFilePath")
        // Ensure the directory exists
        changelogDir.mkdirs()
        // Create and write to the changelog file
        val changelogFile = project.file(changelogFilePath)
        changelogFile.createNewFile()
        changelogFile.writeText(changelog)
        println(changelog)
    }

    private fun execAndGetOutput(command: List<String>): String {
        val outputStream = ByteArrayOutputStream()
        execOperations.exec {
            commandLine(command)
            standardOutput = outputStream
        }
        return String(outputStream.toByteArray(), Charsets.UTF_8).trim()
    }
}
