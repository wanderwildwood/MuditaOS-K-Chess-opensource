#!/bin/bash

player_side=${2:-white}
difficulty=${3:-1}
app_package=com.mudita.chess

pgn=$(cat scripts/game.pgn)
pgn="${pgn//$'\r\n'/\\;}"
pgn="${pgn//$'\n'/\\;}"
pgn="${pgn//\"/\\\"}"
pgn="${pgn// /\\ }"

echo "Escaped : $pgn"

adb shell "am force-stop $app_package"
adb shell am start -n "$app_package"/com.mudita.chess.gameloader.GameLoaderActivity --es PGN_EXTRA "$pgn" --es PLAYER_SIDE "$player_side" --ei DIFFICULTY "$difficulty"
