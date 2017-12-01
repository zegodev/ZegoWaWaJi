function print_error_then_exit {
	echo "***** $1 *****"
	exit 1
}

echo "Force stop com.zego.zegowawaji_server"
adb shell am force-stop com.zego.zegowawaji_server || print_error_then_exit "force stop failed"

echo "Restart com.zego.zegowawaji_server"
adb shell am start -a android.intent.action.MAIN -c android.intent.category.DEFAULT -n com.zego.zegowawaji_server/.MainActivity || print_error_then_exit "start activity failed"

echo "Restart finish."
