function print_error_then_exit {
	echo "***** $1 *****"
	exit 1
}

echo "Begin at `date`"

if [ 'a'$1 = 'as' ]; then
    echo "Build swawaji server apk"
    ./gradlew clean aSR || print_error_then_exit "build error"
else
    echo "Build xwawaji server apk"
    ./gradlew clean aXR || print_error_then_exit "build error"
fi

if [ 'a'$1 = 'a-r' ] || [ 'a'$2 = 'a-r' ]; then
    echo "Uninstall package com.zego.zegowawaji_server"
    adb uninstall com.zego.zegowawaji_server || print_error_then_exit "uninstall package failed"
fi

cd 'app/build/outputs/apk'

apk_list=`ls ./`
for apk in $apk_list
do
    echo "Install $apk to device"
    adb install -r $apk || print_error_then_exit "install error"
    break
done

echo "Start com.zego.zegowawaji_server"
adb shell am start -a android.intent.action.MAIN -c android.intent.category.DEFAULT -n com.zego.zegowawaji_server/.MainActivity || print_error_then_exit "start error"

echo "Finish at `date`"
