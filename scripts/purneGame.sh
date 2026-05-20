#!/bin/bash

app_package=com.mudita.chess

adb shell "run-as $app_package rm /data/data/$app_package/databases/chess.db"
adb shell "run-as $app_package rm /data/data/$app_package/databases/chess.db-journal"
adb shell "am force-stop $app_package"
