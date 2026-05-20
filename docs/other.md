# Other

## Test coverage

It can be checked by running `./gradlew koverHtmlReportMain`.

## Benchmarks

Benchmarks should be run once in a while to check if the performance of the app is not degrading.
Benchmarks can be found in the [benchmark](../benchmark) module.

## Baseline profiles

Generate baseline profiles to improve app performance. If you change the code that is related to particular baseline profile
scope, run `./gradlew generateBaselineProfile`. Result files will be automatically saved
to [app-android/src/main/baselineProfiles](../app-android/src/main/baselineProfiles).

To check if baseline profile improved the app performance, run the corresponding benchmark test
(like [StartupBenchmark](../benchmark/src/main/kotlin/com/mudita/chess/benchmark/startup/StartupBenchmark.kt)) and compare the results.
`CompilationMode.None()` represents results without baseline profile,
`CompilationMode.Partial()` represents results with baseline profile.

Baseline profile generators can be found in the [baselineprofile](../baselineprofile) module.

# Licenses
Licenses list is collected by the [AboutLibraries](https://github.com/mikepenz/AboutLibraries) plugin.

The report is generated **automatically** during every build process and saved to:
`app/src/main/res/raw/aboutlibraries.json`

To generate it manually, run:
```bash
./gradlew exportLibraryDefinitions
```