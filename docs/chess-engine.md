# Chess engine

## Selected engine

Application is using [Fairy-Stockfish](https://github.com/fairy-stockfish/Fairy-Stockfish) -
is a chess variant engine derived from Stockfish.
It's used to support wide range of playing strength, whereas Stockfish lowest supported playing strength ELO is
1320 [source](https://official-stockfish.github.io/docs/stockfish-wiki/UCI-&-Commands.html#setoption).
For more info look at Stockfish [issue](https://github.com/official-stockfish/Stockfish/issues/3635) where considered engine playing
strength.

## Engine build

Engine sources and build configuration is encapsulated in [chess-engine-bin](../library/chess-engine-bin) module.
To rebuild engine execute `./gradlew library:chess-engine-bin:copyBinaries`.
Task will build executable binary and copy it to `jniLibs` folder in [chess-engine](../library/chess-engine) module.

## Engine utilization

Application has configured `packaging.jniLibs.useLegacyPackaging = true` to include native libraries compressed in the APK.
Including binaries in application assets to copy into application data folder and then execute binaries is not supported after android
28 since violating rule of executing in folder with read/write only permission.

When engine binaries are compressed into APK, application is able to run engine using `java.lang.ProcessBuilder` and communicate with
it using UCI interface (sending commands to process output stream and reading result from process input stream).

UCI interface documentation can be found under [link](https://github.com/official-stockfish/Stockfish/wiki/UCI-&-Commands) 
