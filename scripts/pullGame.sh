#!/bin/bash

app_package=com.wanderwildwood.chessplus

adb -d shell "run-as $app_package cat /data/data/$app_package/databases/chess.db" > ./scripts/chess.db
sqlite3 ./scripts/chess.db "select * from SavedGames;" ".exit"
rm ./scripts/chess.db
