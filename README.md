# Chess+

A chess app for the [Mudita Kompakt](https://mudita.com/products/kompakt/), with a two-player
mode for one phone.

Fork of Mudita's own open-source
[Kompakt Chess](https://github.com/mudita/MuditaOS-K-Chess-opensource). Everything the stock
app does is still here: play against Stockfish, adjustable difficulty, move suggestions, game
statistics.

## What is different

Two people can play on one board, passing it back and forth. The toggle is at the top of New
Game options; selecting it hides Move Suggestions, Difficulty and Player Color — in the New
Game screen and in the in-game pause menu — since none of them apply.

The result no longer covers the board. Checkmate and draw used to be announced over the
position that caused them, which is the one thing you want to look at. The result now sits in
the turn-indicator slot.

Undo works after the game is over, so a checkmate or a draw can be taken back and play
resumed, rather than the board freezing the moment it ends.

It builds from source. The stock app depends on `com.mudita:kompakt-ui`, a private Mudita
library that is not publicly buildable; the UI layer here is on Mudita's public
[MMD](https://github.com/mudita/MMD) design system instead.

It installs alongside the factory Chess app rather than replacing it, under its own
application ID (`com.wanderwildwood.chessplus`, app name "Chess+"). Installing over the stock
app with a different signing key fails outright with `INSTALL_FAILED_UPDATE_INCOMPATIBLE`, so
a separate app was the only way to have this without wiping the original.

## Installing

Download the APK from
[Releases](https://github.com/wanderwildwood/MuditaOS-K-Chess-opensource/releases), check it
against the published `.sha256`, and sideload it with `adb install`.

### Upgrading from an older copy needs an uninstall

Android will not install this over an older copy, and stops with
`INSTALL_FAILED_UPDATE_INCOMPATIBLE`. Uninstall first:

```sh
adb uninstall com.wanderwildwood.chessplus
adb install chessplus-<version>.apk
```

Uninstalling clears game statistics and settings. It does not touch the Chess app the phone
came with — Chess+ installs alongside it. Updates after this one install normally.

Or build it yourself: `./gradlew :app-android:assembleRelease`.

## Licence

GPLv3. See [LICENSE.md](LICENSE.md).

This app bundles the [Stockfish](https://stockfishchess.org/) chess engine, which is itself
GPLv3; using it obligates the whole app to be GPLv3 as well. See
[mudita/MuditaOS-K-Chess-opensource](https://github.com/mudita/MuditaOS-K-Chess-opensource)
and [mudita/MMD](https://github.com/mudita/MMD) for the original projects this is built on.
