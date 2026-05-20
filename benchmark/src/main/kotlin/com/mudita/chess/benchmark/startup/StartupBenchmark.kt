package com.mudita.chess.benchmark.startup

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import org.junit.Rule
import org.junit.Test

class StartupBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun coldStartupMode() = startup(StartupMode.COLD)

    @Test
    fun coldStartupModePartial() = startup(StartupMode.COLD, CompilationMode.Partial())

    @Test
    fun warmStartupMode() = startup(StartupMode.WARM)

    @Test
    fun hotStartupMode() = startup(StartupMode.HOT)

    private fun startup(
        startupMode: StartupMode,
        compilationMode: CompilationMode = CompilationMode.None()
    ) = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(StartupTimingMetric()),
        iterations = 10,
        startupMode = startupMode,
        compilationMode = compilationMode,
        setupBlock = { pressHome() }
    ) {
        startActivityAndWait()
    }

    companion object {
        private const val PACKAGE_NAME = "com.mudita.chess"
    }
}
