*** 非常重要 ***：
请先解压 zego_wwjs_ex.zip ，里面有一个 zego_wwjs_ex.apk 文件和一个 aidl 文件夹。

在使用源码编译的娃娃机控制程序前，请先安装此 APK。

目前此 APK 仅提供更新系统时间服务，后续会根据需要增加其它能力。

如果您是自己开发控制程序，请参照下面的使用说明（我们的源码中已经有实现）：

1. 使用 adb 命令或者其它方式将 zego_wwjs_ex.apk 安装到娃娃机 Android 板上；
2. 将 aidl 目录下的内容复制到您自己开发的娃娃机控制程序相应源码目录（具体可参照 Android aidl 开发协议）；
3. 在需要更新系统时间的位置，参照如下方式调用此 APK 提供的 API：

    Intent intent = new Intent("im.zego.wwjs.action.SERVICE_EX");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        boolean success = bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IWwjsApiEx api = IWwjsApiEx.Stub.asInterface(service);
                try {
                    api.requestUpdateSysTime(new UpdateTimeCallbackImpl());
                } catch (RemoteException e) {
                    Log.e("ZEGO_WWJ", "call requestUpdateSysTime failed.", e);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);

        if (!success) {
            Log.e("ZEGO_WWJ", "*** package im.zego.wawajiservice not install in this device");
        }
    }


    static private class UpdateTimeCallbackImpl extends CBUpdateTime.Stub {
        @Override
        public void onSysTimeUpdated(boolean success) throws RemoteException {
            Log.w("ZEGO_WWJ", "update time success? " + success);
        }
    }