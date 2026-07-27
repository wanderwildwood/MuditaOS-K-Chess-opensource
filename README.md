# Chess+

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE.md)

**A chess app for the Mudita Kompakt (e-ink Android phone) with a local two-player mode.**

This is a personal fork of Mudita's own open-source [Kompakt Chess app](https://github.com/mudita/MuditaOS-K-Chess-opensource). It keeps everything the stock app does — play against the Stockfish engine, adjustable difficulty, move suggestions, game statistics — and adds one feature Mudita's version doesn't have:

- **Local 2-player (pass-and-play) mode**: a toggle at the top of New Game options. Selecting it hides Move Suggestions, Difficulty, and Player Color (both in the New Game screen and the in-game pause menu), since none of those apply when two people are playing on the same board.

## Why this exists as a separate app

The stock app depends on `com.mudita:kompakt-ui`, a private Mudita library that isn't publicly buildable. This fork migrates the whole UI layer to Mudita's public [MMD](https://github.com/mudita/MMD) design system instead, so anyone can actually build it from source. It also ships under its own application ID (`com.wanderwildwood.chessplus`, app name "Chess+") rather than the stock app's, so it installs **alongside** the factory Chess app instead of conflicting with it — installing over the stock app with a different signing key fails outright (`INSTALL_FAILED_UPDATE_INCOMPATIBLE`), so a separate app was the only way to make this usable without wiping the original.

## Download

Grab the latest APK from [Releases](https://github.com/wanderwildwood/MuditaOS-K-Chess-opensource/releases) and sideload it with `adb install`. Every release is signed with the same fixed keystore (checked into the repo — see [release workflow](.github/workflows/release.yml)), so installing a newer release over an older one works like a normal app update instead of requiring an uninstall first.

Or build it yourself: `./gradlew :app-android:assembleRelease`.

## License

Licensed under [GNU GPLv3](LICENSE.md). This app bundles the [Stockfish](https://stockfishchess.org/) chess engine, which is itself GPLv3 — using it obligates the whole app to be GPLv3 as well. See [mudita/MuditaOS-K-Chess-opensource](https://github.com/mudita/MuditaOS-K-Chess-opensource) and [mudita/MMD](https://github.com/mudita/MMD) for the original projects this is built on.
