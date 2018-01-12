// IServiceEx.aidl
package im.zego.wawajiservice.apiex;

import im.zego.wawajiservice.apiex.CBUpdateTime;

/**
 * 对外 API 定义
 */
interface IWwjsApiEx {
    /**
     * 请求更新系统时间。请确保此时 Android 板已经连接至公网
     * @param callback 时间更新后的回调，通知是否更新成功
     */
    void requestUpdateSysTime(CBUpdateTime callback);
}
