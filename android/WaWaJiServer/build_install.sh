function print_error_then_exit {
	echo "***** $1 *****"
	exit 1
}

echo "Build xwawaji debug"
./gradlew clean assembleXwawajiDebug ||  print_error_then_exit "Build Error"

echo "Install the apk to device"
adb install -r app/build/outputs/apk/app-xwawaji-debug.apk || print_error_then_exit "Install Error"

echo "Start com.zego.zegowawaji_server/.MainActivity"
adb shell am start -a android.intent.action.MAIN -c android.intent.category.DEFAULT -n com.zego.zegowawaji_server/.MainActivity || print_error_then_exit "Start Error"

echo "Finish."
