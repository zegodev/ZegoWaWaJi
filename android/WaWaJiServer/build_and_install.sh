function print_error_then_exit {
	echo "***** $1 *****"
	exit 1
}

if [ 'a'$2 = 'a-r' ]; then
    echo "Uninstall package com.zego.zegowawaji_server"
    adb uninstall com.zego.zegowawaji_server || print_error_then_exit "uninstall package failed"
fi

if [ 'a'$1 = 'as' ]; then
    echo "Build swawaji server apk"
    ./gradlew clean assembleSwawajiRelease || print_error_then_exit "build error"

    echo "Install swawaji server apk to device"
    adb install -r app/build/outputs/apk/app-swawaji-release.apk || print_error_then_exit "install error"
else
    echo "Build xwawaji server apk"
    ./gradlew clean assembleXwawajiRelease || print_error_then_exit "build error"

    echo "Install xwawaji server apk to device"
    adb install -r app/build/outputs/apk/app-xwawaji-release.apk || print_error_then_exit "install error"
fi

echo "Start com.zego.zegowawaji_server"
adb shell am start -a android.intent.action.MAIN -c android.intent.category.DEFAULT -n com.zego.zegowawaji_server/.MainActivity || print_error_then_exit "start error"

echo "Finish."
