package com.zego.zegowawaji_server.entity;

import com.zego.base.utils.AppLogger;
import com.zego.zegowawaji_server.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 游戏配置。
 * 包括游戏时间、中奖概率以及各段抓力值。
 *
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 30/11/2017.
 */

public class GameConfig {
    static final private int DEFAULT_GAME_TIME = Constants.DEFAULT_GAME_TIME_IN_SECONDS;
    static final private int DEFAULT_CLAW_POWER_GRAB = 32;
    static final private int DEFAULT_CLAW_POWER_UP = 16;
    static final private int DEFAULT_CLAW_POWER_MOVE = 10;
    static final private int DEFAULT_UP_HEIGHT = 7;

    /**
     * 游戏时长，单位：秒，默认值 30
     */
    private int gameTime = DEFAULT_GAME_TIME;

    /**
     * 下爪抓力值，取值范围 [0, 48], 默认值 32
     */
    private int clawPowerGrab = DEFAULT_CLAW_POWER_GRAB;

    /**
     * 提起抓力值，取值范围 [0, 48], 默认值 16
     */
    private int clawPowerUp = DEFAULT_CLAW_POWER_UP;

    /**
     * 移动时抓力值，取值范围 [0, 48], 默认值 10
     */
    private int clawPowerMove = DEFAULT_CLAW_POWER_MOVE;

    /**
     * 取值范围 [0, 10], 默认值 7
     */
    private int upHeight = DEFAULT_UP_HEIGHT;

    public int getGameTime() {
        return gameTime;
    }

    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }

    public int getClawPowerGrab() {
        return clawPowerGrab;
    }

    public void setClawPowerGrab(int clawPowerGrab) {
        this.clawPowerGrab = clawPowerGrab;
    }

    public int getClawPowerUp() {
        return clawPowerUp;
    }

    public void setClawPowerUp(int clawPowerUp) {
        this.clawPowerUp = clawPowerUp;
    }

    public int getClawPowerMove() {
        return clawPowerMove;
    }

    public void setClawPowerMove(int clawPowerMove) {
        this.clawPowerMove = clawPowerMove;
    }

    public int getUpHeight() {
        return upHeight;
    }

    public void setUpHeight(int upHeight) {
        this.upHeight = upHeight;
    }

    public boolean copyFrom(GameConfig srcConfig) {
        if (srcConfig != null) {
            this.gameTime = srcConfig.gameTime;
            this.clawPowerGrab = srcConfig.clawPowerGrab;
            this.clawPowerUp = srcConfig.clawPowerUp;
            this.clawPowerMove = srcConfig.clawPowerMove;
            this.upHeight = srcConfig.upHeight;
            return true;
        }
        return false;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("game_time", this.gameTime);
            jsonObject.put("claw_power_grab", this.clawPowerGrab);
            jsonObject.put("claw_power_up", this.clawPowerUp);
            jsonObject.put("claw_power_move", this.clawPowerMove);
            jsonObject.put("up_height", this.upHeight);
        } catch (JSONException e) {
            AppLogger.getInstance().writeLog("to json failed. exception: %s", e);
        }

        return jsonObject;
    }

    static public GameConfig obtain(GameConfig rawConfig) {
        GameConfig config = new GameConfig();
        config.gameTime = rawConfig.gameTime;
        config.clawPowerGrab = rawConfig.clawPowerGrab;
        config.clawPowerUp = rawConfig.clawPowerUp;
        config.clawPowerMove = rawConfig.clawPowerMove;
        config.upHeight = rawConfig.upHeight;
        return config;
    }

    static public GameConfig parseFromJson(JSONObject jsonConfig) {
        return parseFromJson(jsonConfig, null);
    }

    static public GameConfig parseFromJson(JSONObject jsonConfig, GameConfig defaultConfig) {
        GameConfig config = new GameConfig();
        config.gameTime = jsonConfig.optInt("game_time", defaultConfig == null ? DEFAULT_GAME_TIME : defaultConfig.getGameTime());
        config.clawPowerGrab = jsonConfig.optInt("claw_power_grab", defaultConfig == null ? DEFAULT_CLAW_POWER_GRAB : defaultConfig.getClawPowerGrab());
        config.clawPowerUp = jsonConfig.optInt("claw_power_up", defaultConfig == null ? DEFAULT_CLAW_POWER_UP : defaultConfig.getClawPowerUp());
        config.clawPowerMove = jsonConfig.optInt("claw_power_move", defaultConfig == null ? DEFAULT_CLAW_POWER_MOVE : defaultConfig.getClawPowerMove());
        config.upHeight = jsonConfig.optInt("up_height", defaultConfig == null ? DEFAULT_UP_HEIGHT : defaultConfig.getUpHeight());
        return config;
    }
}
