function print_error_then_exit {
	echo "***** $1 *****"
	exit 1
}

echo "Install app-$1-release.apk to device"
adb install -r app/build/outputs/apk/app-$1-release.apk || print_error_then_exit "install error"

echo "Start com.zego.zegowawaji_server"
adb shell am start -a android.intent.action.MAIN -c android.intent.category.DEFAULT -n com.zego.zegowawaji_server/.MainActivity || print_error_then_exit "start error"

echo "Start finish."
