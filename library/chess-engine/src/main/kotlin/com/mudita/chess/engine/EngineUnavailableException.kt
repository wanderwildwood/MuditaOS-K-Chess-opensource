package com.mudita.chess.engine

/**
 * The engine cannot be run, or stopped answering.
 *
 * The usual cause is a phone whose processor the APK carries no engine for: the app ships
 * `libstockfish.so` for arm64 only, so an x86_64 emulator has nothing to start. Two-player games
 * never touch the engine, so callers treat this as "no computer player here", not as a crash.
 */
class EngineUnavailableException(message: String, cause: Throwable? = null) : Exception(message, cause)
