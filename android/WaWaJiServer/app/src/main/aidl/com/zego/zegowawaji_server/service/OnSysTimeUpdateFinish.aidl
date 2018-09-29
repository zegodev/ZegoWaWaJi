// OnSysTimeUpdateFinish.aidl
package com.zego.zegowawaji_server.service;

/**
 * 使用网络时间更新系统时间回调
 */
interface OnSysTimeUpdateFinish {
    /**
     * 更新系统时间后，通知调用方更新结果
     * @param success true : 更新成功; false: 更新失败
     */
    void onSysTimeUpdateFinish(boolean success);
}
