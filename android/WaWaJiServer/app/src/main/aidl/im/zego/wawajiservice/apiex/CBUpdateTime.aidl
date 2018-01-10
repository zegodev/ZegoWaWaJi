// CBUpdateTime.aidl
package im.zego.wawajiservice.apiex;

/**
 * 使用网络时间更新系统时间回调
 */
interface CBUpdateTime {
    /**
     * 更新系统时间后，通知调用方更新结果
     * @param success true : 更新成功; false: 更新失败
     */
    void onSysTimeUpdated(boolean success);
}
