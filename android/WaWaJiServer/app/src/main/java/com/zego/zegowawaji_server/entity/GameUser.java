package com.zego.zegowawaji_server.entity;

import com.zego.zegoliveroom.entity.ZegoUser;

/**
 * 预约用户信息。
 *
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 30/11/2017.
 */

public class GameUser extends ZegoUser {
    private String sessionId;

    private GameConfig config;
    private String customToken;

    private long beginTime;

    private int level;

    public GameUser() {
        level = -1;
        config = new GameConfig();
    }

    public GameUser(ZegoUser user) {
        this();
        userID = user.userID;
        userName = user.userName;
    }

    public GameUser(String userId, String userName) {
        this();
        this.userID = userId;
        this.userName = userName;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setGameConfig(GameConfig config) {
        this.config = GameConfig.obtain(config);
    }

    public GameConfig getGameConfig() {
        return config;
    }

    public void setCustomToken(String customToken) {
        this.customToken = customToken;
    }

    public String getCustomToken() {
        return customToken;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
