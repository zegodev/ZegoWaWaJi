开发步骤：

1. 在开发调试此 App 前，请先联系我们的客服人员，申请专属于您自己的 AppId & App SignKey;

2. 将申请的 AppId & App SignKey 填入相应位置：
    a、打开 app/src/main/java/com/zego/zegowawaji_server/ZegoApplication.java;
    b、修改 loadActivateConfig 方法，将申请的 AppId 赋值给 mAppId，App SignKey 赋值给 mSignKey，例：
        mAppId = 1234567899L;
        mSignKey = new byte[] { (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07, (byte)0x08,
                                (byte)0x09, (byte)0x0a, (byte)0x0b, (byte)0x0c, (byte)0x0d, (byte)0x0e, (byte)0x0f, (byte)0x10,
                                (byte)0x11, (byte)0x12, (byte)0x13, (byte)0x14, (byte)0x15, (byte)0x16, (byte)0x17, (byte)0x18,
                                (byte)0x19, (byte)0x1a, (byte)0x1b, (byte)0x1c, (byte)0x1d, (byte)0x1e, (byte)0x1f, (byte)0x20 };
    c、保存修改；

3. 使用 mini5P 转 USB 线将电脑与娃娃机 OTG 口连接；

4. 检测连接状态：
    a、adb devices -l
    b、查看输出内容中是否有如下内容：
        12345678900            device usb:336592896X product:rk7130s model:rk7130s device:rk7130s
    c、如果有，表明连接正常，可以执行接下来的步骤；否则，请重启娃娃机再次检测连接状态

5. 打开终端，进入工程根目录（该文件所在目录）；

6. 编译、安装并运行 apk （如果失败，请根据错误提示修改相应的源码）：
    ./build_and_install.sh x -r

7. 请使用我们开源的 Client 端源码(选择您需要的平台)，设置相应的 AppId 与 App SignKey，编译可执行文件安装至手机；

8. 恭喜，至此，您已经大功告成，可以联调娃娃机了

