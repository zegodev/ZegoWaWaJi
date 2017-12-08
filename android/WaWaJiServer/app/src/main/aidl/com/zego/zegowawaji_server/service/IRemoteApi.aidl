// IRemoteApi.aidl
package com.zego.zegowawaji_server.service;

// Declare any non-default types here with import statements

interface IRemoteApi {
    void join(IBinder token);
    void leave(IBinder token);
    void sendHeartbeat();
}
