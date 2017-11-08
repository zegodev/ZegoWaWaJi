package com.zego.base.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.zego.zegoliveroom.constants.ZegoAvConfig;
import com.zego.zegowawaji_server.ZegoApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 24/10/2017.
 */

public class PrefUtil {
    static private PrefUtil sInstance;

    static private String KEY_USER_ID = "_zego_user_id";
    static private String KEY_USER_NAME = "_zego_user_name";

    static private String KEY_LIVE_QUALITY = "_zego_live_quality_index";
    static private String KEY_LIVE_QUALITY_RESOLUTION = "_zego_live_resolution";
    static private String KEY_LIVE_QUALITY_FPS = "_zego_live_fps";
    static private String KEY_LIVE_QUALITY_BITRATE = "_zego_live_bitrate";

    static private String KEY_ROOM_ID = "_zego_room_id";
    static private String KEY_STREAM_ID = "_zego_stream_id";
    static private String KEY_STREAM_ID2 = "_zego_stream_id2";

    private SharedPreferences mPref;

    private PrefUtil() {
        mPref = ZegoApplication.getAppContext().getSharedPreferences("__global_pref", Context.MODE_PRIVATE);
    }

    static public PrefUtil getInstance() {
        if (sInstance == null) {
            synchronized (PrefUtil.class) {
                if (sInstance == null) {
                    sInstance = new PrefUtil();
                }
            }
        }
        return sInstance;
    }

    private PrefUtil setInt(String key, int value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(key, value);
        editor.apply();
        return this;
    }

    private PrefUtil setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
        return this;
    }

    private PrefUtil setLong(String key, long value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putLong(key, value);
        editor.apply();
        return this;
    }

    private PrefUtil setString(String key, String value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(key, value);
        editor.apply();
        return this;
    }

    private PrefUtil setObject(String key, Object value) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            String textData = new String(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));

            setString(key, textData);
        } catch (IOException e) {
            e.printStackTrace();
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return this;
    }

    private Object getObject(String key) {
        Object value = null;
        ByteArrayInputStream bais = null;
        try {
            String rawValue = mPref.getString(key, null);
            if (rawValue != null) {
                byte[] rawBytes = Base64.decode(rawValue, Base64.DEFAULT);
                bais = new ByteArrayInputStream(rawBytes);
                ObjectInputStream ois = new ObjectInputStream(bais);
                value = ois.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return value;
    }

    public String getUserId() {
        return mPref.getString(KEY_USER_ID, "");
    }

    public void setUserId(String userId) {
        setString(KEY_USER_ID, userId);
    }

    public String getUserName() {
        return mPref.getString(KEY_USER_NAME, "");
    }

    public void setUserName(String userName) {
        setString(KEY_USER_NAME, userName);
    }

    public void setLiveQuality(int liveQualityIndex) {
        setInt(KEY_LIVE_QUALITY, liveQualityIndex);
    }

    public int getLiveQuality() {
        return mPref.getInt(KEY_LIVE_QUALITY, -1);
    }

    public void setLiveQualityResolution(int resolutionIndex) {
        setInt(KEY_LIVE_QUALITY_RESOLUTION, resolutionIndex);
    }

    public int getLiveQualityResolution() {
        return mPref.getInt(KEY_LIVE_QUALITY_RESOLUTION, ZegoAvConfig.Level.High);
    }

    public void setLiveQualityFps(int fps) {
        setInt(KEY_LIVE_QUALITY_FPS, fps);
    }

    public int getLiveQualityFps() {
        return mPref.getInt(KEY_LIVE_QUALITY_FPS, 15);
    }

    public void setLiveQualityBitrate(int bitrate) {
        setInt(KEY_LIVE_QUALITY_BITRATE, bitrate);
    }

    public int getLiveQualityBitrate() {
        return mPref.getInt(KEY_LIVE_QUALITY_BITRATE, 600);
    }

    public void setRoomId(String roomId) {
        setString(KEY_ROOM_ID, roomId);
    }

    public String getRoomId() {
        return mPref.getString(KEY_ROOM_ID, "");
    }

    public void setStreamId(String streamId) {
        setString(KEY_STREAM_ID, streamId);
    }

    public String getStreamId() {
        return mPref.getString(KEY_STREAM_ID, "");
    }

    public void setStreamId2(String streamId2) {
        setString(KEY_STREAM_ID2, streamId2);
    }

    public String getStreamId2() {
        return mPref.getString(KEY_STREAM_ID2, "");
    }
}
