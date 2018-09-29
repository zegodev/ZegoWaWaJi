// IRemoteApi.aidl
package com.zego.zegowawaji_server.service;

// Declare any non-default types here with import statements
import com.zego.zegowawaji_server.service.OnSysTimeUpdateFinish;

interface IRemoteApi {
    /*
     * 启动监听
     */
    void join(IBinder token);

    /**
     * 断开监听
     */
    void leave(IBinder token);
    /**
     * 发送心跳
     */
    void sendHeartbeat();
    /**
     * 更新 Service 使用的 Bugly 信息。
     * 使用此方法设置 Bugly 信息的原因是避免在 :guard 进程也启动 Bugly 进程。
     * @param sdkVersion SDK 版本信息
     * @param veVersion VE 引擎版本信息
     * @param appId Zego 分配的 AppId 值
     */
    void updateBuglyInfo(String sdkVersion, String veVersion, long appId);
    /**
     * 请求更新系统时间。
     * 请确保此时 Android 板已经连接至公网（此方法并非对所有系统有效，仅在没有安装 im.zego.wawajiservice 包的情况下作为备用）。
     * @param callback 时间更新后的回调，通知是否更新成功
     */
    void requestUpdateSysTime(OnSysTimeUpdateFinish cb);
}
