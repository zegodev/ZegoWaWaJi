package com.zego.zegowawaji_server;

import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.entity.ZegoUser;
import com.zego.zegowawaji_server.entity.GameUser;

import java.util.List;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 30/10/2017.
 */

public interface IRoomClient {
    List<ZegoUser> getTotalUser();

    List<GameUser> getQueueUser();

    void updateCurrentPlayerInfo(String userId, String userName);

    ZegoLiveRoom getZegoLiveRoom();

    void runOnWorkThread(Runnable task);

    void requireRestart(String desc);
}
