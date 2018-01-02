package com.zego.wawaji_client;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.zego.wawaji.R;
import com.zego.wawaji_client.constants.BoardState;
import com.zego.wawaji_client.constants.CMDKey;
import com.zego.wawaji_client.entity.Room;
import com.zego.wawaji_client.entity.ZegoStream;
import com.zego.wawaji_client.utils.PreferenceUtil;
import com.zego.wawaji_client.utils.SystemUtil;
import com.zego.wawaji_client.widgets.GameResultDialog;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoLivePlayerCallback;
import com.zego.zegoliveroom.callback.IZegoLoginCompletionCallback;
import com.zego.zegoliveroom.callback.IZegoRoomCallback;
import com.zego.zegoliveroom.constants.ZegoConstants;
import com.zego.zegoliveroom.entity.ZegoStreamInfo;
import com.zego.zegoliveroom.entity.ZegoStreamQuality;
import com.zego.zegoliveroom.entity.ZegoUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright © 2017 Zego. All rights reserved.
 */
public class PlayActivity extends AppCompatActivity {
    /**
     * 流状态.
     */
    private TextView mTvStreamSate;

    /**
     * 切换摄像头.
     */
    private ImageButton mIbtnSwitchCamera;

    /**
     * 操作.
     */
    private ImageButton mIBtnLeft;
    private ImageButton mIBtnForward;
    private ImageButton mIBtnBackward;
    private ImageButton mIBtnRight;
    private ImageButton mIBtnGO;

    /**
     * 预约按钮.
     */
    private ImageButton mIBtnApply;
    private TextView mTvApply;

    /**
     * 抓娃娃操作面板.
     */
    private RelativeLayout mRlytControlPannel;

    /**
     * 上机倒计时.
     */
    private TextView mTvBoardingCountDown;

    /**
     * 切换摄像头次数, 用于记录当前正在显示"哪一条流"
     */
    private int mSwitchCameraTimes = 0;

    private List<ZegoStream> mListStream = new ArrayList<>();

    /**
     * 计时器.
     */
    private CountDownTimer mCountDownTimer;

    /**
     * 网络质量.
     */
    private ImageView mIvQuality;
    private TextView mTvQuality;

    /**
     * 房间人数.
     */
    private TextView mTvRoomUserCount;

    /**
     * "确认上机"对话框.
     */
    private AlertDialog mDialogConfirmGameReady;

    private GameResultDialog mDialogGameResult;

    private ZegoLiveRoom mZegoLiveRoom = ZegoApiManager.getInstance().getZegoLiveRoom();

    private Room mRoom;

    /**
     * app是否在后台.
     */
    private boolean mIsAppInBackground = true;

    /**
     * 是否继续玩.
     */
    private boolean mContinueToPlay = false;

    /**
     * 房间内的排队人数.
     */
    private int mUsersInQueue = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent != null) {
            mRoom = (Room) intent.getSerializableExtra("room");

            setTitle(mRoom.roomName);
            setContentView(R.layout.activity_play);

            initStreamList();
            initViews();
            startPlay();

        } else {
            Toast.makeText(this, "房间信息初始化错误, 请重新开始", Toast.LENGTH_LONG).show();
            finish();
        }


        // 从加速服务器拉流
        ZegoLiveRoom.setConfig(ZegoConstants.Config.PREFER_PLAY_ULTRA_SOURCE + "=1");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mIsAppInBackground){
            mIsAppInBackground = false;

            Log.i("PlayActivity", "App comes to foreground");

            int currentShowIndex = mSwitchCameraTimes % 2;
            if (currentShowIndex == 0){
                mListStream.get(0).playStream();
                mListStream.get(1).playStream();
            }else {
                mListStream.get(0).playStream();
                mListStream.get(1).playStream();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!SystemUtil.isAppForeground()){
            mIsAppInBackground = true;

            Log.i("PlayActivity", "App goes to background");

            for(ZegoStream zegoStream : mListStream){
                zegoStream.stopPlayStream();
            }
        }
    }

    private ZegoStream constructStream(int index, String streamID) {
        String sateStrings[];
        TextureView textureView;
        if (index == 0) {
            sateStrings = getResources().getStringArray(R.array.video1_state);
            textureView = (TextureView) findViewById(R.id.textureview1);
        } else {
            sateStrings = getResources().getStringArray(R.array.video2_state);
            textureView = (TextureView) findViewById(R.id.textureview2);
        }
        return new ZegoStream(streamID, textureView, sateStrings);
    }

    /**
     * 初始化流信息. 当前, 一个房间内只需要2条流用于播放，少了用"空流"替代.
     */
    private void initStreamList() {

        int streamSize = mRoom.streamList.size() >= 2 ? 2 : mRoom.streamList.size();

        for (int index = 0; index < streamSize; index++) {
            mListStream.add(constructStream(index, mRoom.streamList.get(index)));
        }

        if (mListStream.size() < 2) {
            for (int index = mListStream.size(); index < 2; index++) {
                mListStream.add(constructStream(index, null));
            }
        }
    }

    private void initViews() {

        mTvBoardingCountDown = (TextView) findViewById(R.id.tv_boarding_countdown);

        mTvStreamSate = (TextView) findViewById(R.id.tv_stream_state);
        mTvStreamSate.setText(mListStream.get(0).getStateString());

        mRlytControlPannel = (RelativeLayout) findViewById(R.id.rlyt_control_pannel);

        mTvApply = (TextView) findViewById(R.id.tv_apply);

        mIBtnApply = (ImageButton) findViewById(R.id.ibtn_apply);
        mIBtnApply.setEnabled(false);
        mIBtnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIBtnApply.setEnabled(false);
                if (CMDCenter.getInstance().getCurrentBoardSate() == BoardState.Ended) {
                    CMDCenter.getInstance().apply(false, new CMDCenter.OnCommandSendCallback() {
                        @Override
                        public void onSendFail() {
                            sendCMDFail("Apply");
                        }
                    });
                } else {
                    if (CMDCenter.getInstance().getCurrentBoardSate() == BoardState.WaitingBoard){
                        CMDCenter.getInstance().cancelApply(new CMDCenter.OnCommandSendCallback() {
                            @Override
                            public void onSendFail() {
                                Toast.makeText(PlayActivity.this, getString(R.string.cancel_apply_failed), Toast.LENGTH_SHORT).show();
                                mIBtnApply.setEnabled(true);
                            }
                        });
                    }
                }
            }
        });

        mIBtnGO = (ImageButton) findViewById(R.id.ibtn_go);
        mIBtnGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CMDCenter.getInstance().getCurrentBoardSate() == BoardState.Boarding) {
                    if (mCountDownTimer != null){
                        mCountDownTimer.cancel();
                    }
                    enbleControl(false);
                    CMDCenter.getInstance().grub();
                }
            }
        });

        mIbtnSwitchCamera = (ImageButton) findViewById(R.id.ibtn_switch_camera);
        mIbtnSwitchCamera.setEnabled(false);
        mIbtnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwitchCameraTimes++;

                mTvStreamSate.setVisibility(View.GONE);

                int currentShowIndex = mSwitchCameraTimes % 2;
                if (currentShowIndex == 1) {
                    // 隐藏第一路流
                    mListStream.get(0).hide();

                    // 显示第二路流
                    if (mListStream.get(1).isPlaySuccess()) {
                        mListStream.get(1).show();
                    } else {
                        mTvStreamSate.setText(mListStream.get(1).getStateString());
                        mTvStreamSate.setVisibility(View.VISIBLE);
                    }
                } else {
                    // 隐藏第二路流
                    mListStream.get(1).hide();

                    // 显示第一路流
                    if (mListStream.get(0).isPlaySuccess()) {
                        mListStream.get(0).show();
                    } else {
                        mTvStreamSate.setText(mListStream.get(0).getStateString());
                        mTvStreamSate.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        mIBtnLeft = (ImageButton) findViewById(R.id.ibtn_left);
        mIBtnLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    if (mSwitchCameraTimes % 2 == 0) {
                        CMDCenter.getInstance().moveLeft();
                    } else {
                        CMDCenter.getInstance().moveForward();
                    }
                }else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    CMDCenter.getInstance().stopMove();
                }
                return false;
            }
        });

        mIBtnForward = (ImageButton) findViewById(R.id.ibtn_forward);
        mIBtnForward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    if (mSwitchCameraTimes % 2 == 0) {
                        CMDCenter.getInstance().moveForward();
                    } else {
                        CMDCenter.getInstance().moveRight();
                    }
                }else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    CMDCenter.getInstance().stopMove();
                }
                return false;
            }
        });

        mIBtnBackward = (ImageButton) findViewById(R.id.ibtn_backward);
        mIBtnBackward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    if (mSwitchCameraTimes % 2 == 0) {
                        CMDCenter.getInstance().moveBackward();
                    } else {
                        CMDCenter.getInstance().moveLeft();
                    }
                }else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    CMDCenter.getInstance().stopMove();
                }
                return false;
            }
        });

        mIBtnRight = (ImageButton) findViewById(R.id.ibtn_right);
        mIBtnRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    if (mSwitchCameraTimes % 2 == 0) {
                        CMDCenter.getInstance().moveRight();
                    } else {
                        CMDCenter.getInstance().moveBackward();
                    }
                }else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    CMDCenter.getInstance().stopMove();
                }
                return false;
            }
        });

        showControlPannel(false);

        mIvQuality = (ImageView) findViewById(R.id.iv_quality);
        mTvQuality = (TextView) findViewById(R.id.tv_quality);
        mTvRoomUserCount = (TextView) findViewById(R.id.tv_room_user_count);
    }

    private void startPlay() {
        mZegoLiveRoom.loginRoom(mRoom.roomID, ZegoConstants.RoomRole.Audience, new IZegoLoginCompletionCallback() {
            @Override
            public void onLoginCompletion(int errCode, ZegoStreamInfo[] zegoStreamInfos) {
                CMDCenter.getInstance().printLog("[onLoginCompletion], roomID: " + mRoom.roomID + ", errorCode: " + errCode + ", streamCount: " + zegoStreamInfos.length);
                if (errCode == 0) {
                    for (ZegoStreamInfo streamInfo : zegoStreamInfos) {
                        CMDCenter.getInstance().printLog("[onLoginCompletion], streamInfo: " + streamInfo.toString());

                        if (!TextUtils.isEmpty(streamInfo.userID) &&
                                !TextUtils.isEmpty(streamInfo.userName) && streamInfo.userName.startsWith("WWJS")) {
                            ZegoUser zegoUser = new ZegoUser();
                            zegoUser.userID = streamInfo.userID;
                            zegoUser.userName = streamInfo.userName;
                            CMDCenter.getInstance().setUserInfoOfWaWaJi(zegoUser);
                            break;
                        }
                    }

                    if (CMDCenter.getInstance().getUserInfoOfWaWaJi() == null){
                        CMDCenter.getInstance().printLog("[onLoginCompletion] error, No UserInfo Of WaWaJi");
                    }

                    mIBtnApply.setEnabled(true);
                    mIbtnSwitchCamera.setEnabled(true);

                    // 查询游戏信息
                    CMDCenter.getInstance().queryGameInfo();
                }
            }
        });

        mZegoLiveRoom.setZegoLivePlayerCallback(new IZegoLivePlayerCallback() {
            @Override
            public void onPlayStateUpdate(int errCode, String streamID) {

                int currentShowIndex = mSwitchCameraTimes % 2;

                if (errCode != 0) {
                    // 设置流的状态
                    for (ZegoStream zegoStream : mListStream) {
                        if (zegoStream.getStreamID().equals(streamID)) {
                            zegoStream.setStreamSate(ZegoStream.StreamState.PlayFail);
                            break;
                        }
                    }

                    ZegoStream currentShowStream = mListStream.get(currentShowIndex);
                    if (currentShowStream.getStreamID().equals(streamID)) {
                        mTvStreamSate.setText(currentShowStream.getStateString());
                        mTvStreamSate.setVisibility(View.VISIBLE);
                    }
                }
                CMDCenter.getInstance().printLog("[onPlayStateUpdate], streamID: " + streamID + " ,errorCode: " + errCode + ", currentShowIndex: " + currentShowIndex);
            }

            @Override
            public void onPlayQualityUpdate(String streamID, ZegoStreamQuality zegoStreamQuality) {
                // 当前显示的流质量
                if (mListStream.get(mSwitchCameraTimes % 2).getStreamID().equals(streamID)) {
                    switch (zegoStreamQuality.quality) {
                        case 0:
                            mTvQuality.setText("网络优秀");
                            mIvQuality.setImageResource(R.mipmap.excellent);
                            mTvQuality.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                            break;
                        case 1:
                            mTvQuality.setText("网络流畅");
                            mIvQuality.setImageResource(R.mipmap.good);
                            mTvQuality.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                            break;
                        case 2:
                            mTvQuality.setText("网络缓慢");
                            mIvQuality.setImageResource(R.mipmap.average);
                            mTvQuality.setTextColor(getResources().getColor(R.color.bg_yellow_p));
                            break;
                        case 3:
                            mTvQuality.setText("网络拥堵");
                            mIvQuality.setImageResource(R.mipmap.pool);
                            mTvQuality.setTextColor(getResources().getColor(R.color.text_red));
                            break;
                    }
                    mIvQuality.setVisibility(View.VISIBLE);
                    mTvQuality.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onInviteJoinLiveRequest(int i, String s, String s1, String s2) {

            }

            @Override
            public void onRecvEndJoinLiveCommand(String s, String s1, String s2) {

            }

            @Override
            public void onVideoSizeChangedTo(String streamID, int i, int i1) {
                for (ZegoStream zegoStream : mListStream) {
                    if (zegoStream.getStreamID().equals(streamID)) {
                        zegoStream.setStreamSate(ZegoStream.StreamState.PlaySuccess);
                        break;
                    }
                }

                int currentShowIndex = mSwitchCameraTimes % 2;
                ZegoStream currentShowStream = mListStream.get(currentShowIndex);
                if (currentShowStream.getStreamID().equals(streamID)) {
                    mTvStreamSate.setVisibility(View.GONE);
                    currentShowStream.show();
                }

                CMDCenter.getInstance().printLog("[onVideoSizeChanged], streamID: " + streamID + ", currentShowIndex: " + currentShowIndex);
            }
        });

        mZegoLiveRoom.setZegoRoomCallback(new IZegoRoomCallback() {
            @Override
            public void onKickOut(int reason, String roomID) {

            }

            @Override
            public void onDisconnect(int errorCode, String roomID) {
            }

            @Override
            public void onReconnect(int i, String s) {

            }

            @Override
            public void onTempBroken(int i, String s) {

            }

            @Override
            public void onStreamUpdated(final int type, final ZegoStreamInfo[] listStream, final String roomID) {
            }

            @Override
            public void onStreamExtraInfoUpdated(ZegoStreamInfo[] zegoStreamInfos, String s) {

            }

            @Override
            public void onRecvCustomCommand(String userID, String userName, String content, String roomID) {
                // 只接收当前房间，当前主播发来的消息
                if (CMDCenter.getInstance().isCommandFromAnchor(userID) && mRoom.roomID.equals(roomID)) {
                    if (!TextUtils.isEmpty(content)) {
                        handleRecvCustomCMD(content);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_log:
                showLogMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendCMDFail(String cmd) {
        CMDCenter.getInstance().printLog("send cmd error: " + cmd);
        Toast.makeText(PlayActivity.this, getString(R.string.send_cmd_error), Toast.LENGTH_SHORT).show();
        reinitGame();
    }

    private void startGame() {
        mIBtnApply.setVisibility(View.INVISIBLE);
        mTvApply.setVisibility(View.INVISIBLE);
        showControlPannel(true);
    }

    private void reinitGame() {
        mContinueToPlay = false;
        CMDCenter.getInstance().reinitGame();
        showControlPannel(false);

        if (mUsersInQueue == 0){
            String text = getString(R.string.start_game);
            showApplyBtn(true, R.mipmap.start, text, getString(R.string.start_game).length());
        }else {
            String text = getString(R.string.apply_grub) + "\n" + getString(R.string.current_queue_count, mUsersInQueue + "");
            showApplyBtn(true, R.mipmap.book, text, getString(R.string.apply_grub).length());
        }
    }

    private void continueToPlay(){
        mContinueToPlay = true;
        CMDCenter.getInstance().continueToPlay();
        showControlPannel(false);

        if (mUsersInQueue == 0){
            String text = getString(R.string.start_game);
            showApplyBtn(false, R.mipmap.start, text, getString(R.string.start_game).length());
        }else {
            String text = getString(R.string.apply_grub) + "\n" + getString(R.string.current_queue_count, mUsersInQueue + "");
            showApplyBtn(false, R.mipmap.book, text, getString(R.string.apply_grub).length());
        }

        CMDCenter.getInstance().apply(true, new CMDCenter.OnCommandSendCallback() {
            @Override
            public void onSendFail() {
                sendCMDFail("Apply");
            }
        });
    }

    private void showControlPannel(boolean show) {
        if (show) {
            mRlytControlPannel.setVisibility(View.VISIBLE);
        } else {
            mRlytControlPannel.setVisibility(View.INVISIBLE);
        }

        enbleControl(show);
    }

    private void enbleControl(boolean enable) {
        mIBtnLeft.setEnabled(enable);
        mIBtnForward.setEnabled(enable);
        mIBtnRight.setEnabled(enable);
        mIBtnBackward.setEnabled(enable);
        mIBtnGO.setEnabled(enable);
        mTvBoardingCountDown.setText("");
    }

    private void showApplyBtn(boolean enable, int background, String text, int mainTextLen){
        mIBtnApply.setBackgroundResource(background);

        // 如果有副标题，则分行显示，字体要小一号
        if (text.length() <= mainTextLen){
            mTvApply.setText(text);
        }else {
            // 主标题后面有换行符，需要mainTextLen+1
            showApplyText(text, mainTextLen + 1);
        }

        mIBtnApply.setEnabled(enable);
        mIBtnApply.setVisibility(View.VISIBLE);
        mTvApply.setVisibility(View.VISIBLE);
    }

    private void showApplyText(String text, int separatePostion) {
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new TextAppearanceSpan(PlayActivity.this, R.style.tv_style1), 0, separatePostion,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new TextAppearanceSpan(PlayActivity.this, R.style.tv_style2), separatePostion,
                text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mTvApply.setText(ss, TextView.BufferType.SPANNABLE);
    }

    private Map<String, Object> getMapFromJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }

        try{
            Gson gson = new Gson();
            Map<String, Object> map = new HashMap<>();
            map = gson.fromJson(json, map.getClass());
            return map;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private boolean checkSeq(int rspSeq){
        if (CMDCenter.getInstance().getCurrentSeq() != rspSeq) {
            CMDCenter.getInstance().printLog("Error, seq mismatch, rspSeq: " + rspSeq + ", currentSeq: " + CMDCenter.getInstance().getCurrentSeq());
            return false;
        }

        return true;
    }

    private boolean checkSessionID(String sessionID){
        if (TextUtils.isEmpty(sessionID) || !sessionID.equals(CMDCenter.getInstance().getSessionID())){
            CMDCenter.getInstance().printLog("Error, sessionID not equal, my sessionID: " + CMDCenter.getInstance().getSessionID() + ", sessionID: "
                    + sessionID);
            return false;
        }

        return true;
    }

    private boolean checkIsMyMsg(LinkedTreeMap<String, String> mapPlayer){
        if (mapPlayer == null){
            CMDCenter.getInstance().printLog("Error, player is null");
            return false;
        }

        if (!PreferenceUtil.getInstance().getUserID().equals(mapPlayer.get(CMDKey.USER_ID))){
            CMDCenter.getInstance().printLog("Error, msg is not mine, my UserID: "
                    + PreferenceUtil.getInstance().getUserID() + ", player UserID: " + mapPlayer.get(CMDKey.USER_ID));
            return false;
        }

        return true;
    }

    /**
     * 处理服务器返回的"预约结果".
     */
    private void handleApplyResult(int rspSeq, Map<String, Object> data) {
        CMDCenter.getInstance().printLog("[handleApplyResult] enter");

        if (!checkSeq(rspSeq)){
            return;
        }

        if (!checkIsMyMsg((LinkedTreeMap<String, String>) data.get(CMDKey.PLAYER))){
            return;
        }

        CMDCenter.getInstance().printLog("[handleApplyResult], currentSate: " + CMDCenter.getInstance().getCurrentBoardSate());
        if (CMDCenter.getInstance().getCurrentBoardSate() != BoardState.Applying) {
            CMDCenter.getInstance().printLog("[handleApplyResult] error, state mismatch");
            return;
        }

        int result = ((Double) data.get(CMDKey.RESULT)).intValue();
        if (result == 0) {
            String sessionID = (String) data.get(CMDKey.SESSION_ID);
            if(TextUtils.isEmpty(sessionID)){
                CMDCenter.getInstance().printLog("[handleApplyResult] error, sessionID is null");
                return;
            }

            CMDCenter.getInstance().setSessionID(sessionID);
            CMDCenter.getInstance().setCurrentBoardSate(BoardState.WaitingBoard);

            int myPostion = ((Double) data.get(CMDKey.INDEX)).intValue();
            if (!mContinueToPlay){
                String text = getString(R.string.cancel_apply) + "\n" + getString(R.string.my_position, (myPostion + ""));
                showApplyBtn(true, R.mipmap.cancel, text, getString(R.string.cancel_apply).length());
            }
        } else {
            Toast.makeText(PlayActivity.this, getString(R.string.apply_faile), Toast.LENGTH_SHORT).show();
            reinitGame();
        }
    }

    private void handleReplyCancelApply(final int rspSeq, String sessionID, Map<String, Object> data){
        CMDCenter.getInstance().printLog("[handleReplyCancelApply] enter");

        if (!checkSeq(rspSeq)){
            return;
        }

        if (!checkSessionID(sessionID)){
            return;
        }

        CMDCenter.getInstance().printLog("[handleReplyCancelApply], currentSate: " + CMDCenter.getInstance().getCurrentBoardSate());

        if (CMDCenter.getInstance().getCurrentBoardSate() != BoardState.WaitingBoard){
            CMDCenter.getInstance().printLog("[handleReplyCancelApply] error, state mismatch");
            return;
        }

        Toast.makeText(this, getString(R.string.cancel_apply_success), Toast.LENGTH_SHORT).show();

        reinitGame();
    }

    /**
     * 处理服务器返回的"准备游戏"的指令.
     */
    private void handleGameReady(final int rspSeq, String sessionID, Map<String, Object> data) {
        CMDCenter.getInstance().printLog("[handleGameReady] enter");

        if (!checkSessionID(sessionID)){
            return;
        }

        if (!checkIsMyMsg((LinkedTreeMap<String, String>) data.get(CMDKey.PLAYER))){
            return;
        }

        CMDCenter.getInstance().printLog("[handleGameReady], currentSate: " + CMDCenter.getInstance().getCurrentBoardSate());

        if (CMDCenter.getInstance().getCurrentBoardSate() == BoardState.Applying) {
            CMDCenter.getInstance().printLog("[handleGameReady], fix state");
            CMDCenter.getInstance().setCurrentBoardSate(BoardState.WaitingBoard);
        }

        if (CMDCenter.getInstance().getCurrentBoardSate() != BoardState.WaitingBoard) {
            CMDCenter.getInstance().printLog("[handleGameReady] error, state mismatch");
            return;
        }

        if (mCountDownTimer != null){
            mCountDownTimer.cancel();
        }

        // 通知服务器，客户端已经收到GameReady指令
        CMDCenter.getInstance().replyRecvGameReady(rspSeq);

        // 继续玩，直接确认上机
        if (mContinueToPlay){
            mContinueToPlay = false;
            CMDCenter.getInstance().printLog("[handleGameReady], continue to play");
            CMDCenter.getInstance().getEntrptedConfig();
            return;
        }

        if (mDialogConfirmGameReady != null && mDialogConfirmGameReady.isShowing()) {
            CMDCenter.getInstance().printLog("[handleGameReady], confirm dialog is showing");
            return;
        }

        mDialogConfirmGameReady = new AlertDialog.Builder(this).setMessage(getString(R.string.confirm_board, "10")).setTitle("提示").setPositiveButton("上机", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mCountDownTimer.cancel();

                // 按钮不能再点击
                mIBtnApply.setEnabled(false);
                CMDCenter.getInstance().getEntrptedConfig();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mCountDownTimer.cancel();

                CMDCenter.getInstance().confirmBoard(false, null, System.currentTimeMillis(), new CMDCenter.OnCommandSendCallback() {
                    @Override
                    public void onSendFail() {
                        sendCMDFail("ConfirmBoard(false)");
                    }
                });

                reinitGame();
            }
        }).create();
        mDialogConfirmGameReady.setCanceledOnTouchOutside(false);
        mDialogConfirmGameReady.show();

        mCountDownTimer = new CountDownTimer(10000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (CMDCenter.getInstance().getCurrentBoardSate() == BoardState.WaitingBoard) {
                    mDialogConfirmGameReady.setMessage(getString(R.string.confirm_board, ((millisUntilFinished / 1000) + 1) + ""));
                }
            }

            @Override
            public void onFinish() {
                if (CMDCenter.getInstance().getCurrentBoardSate() == BoardState.WaitingBoard) {
                    mDialogConfirmGameReady.dismiss();
                    reinitGame();
                }
            }
        }.start();
    }

    /**
     * 处理服务器返回的"收到上机选择"指令.
     */
    private void handleConfirmBoardReply(int rspSeq, String sessionID, Map<String, Object> data) {
        CMDCenter.getInstance().printLog("[handleConfirmBoardReply] enter");

        if (!checkSeq(rspSeq)){
            return;
        }

        if (!checkSessionID(sessionID)) {
            return;
        }

        CMDCenter.getInstance().printLog("[handleConfirmBoardReply], currentSate: " + CMDCenter.getInstance().getCurrentBoardSate());

        if (CMDCenter.getInstance().getCurrentBoardSate() != BoardState.ConfirmBoard) {
            CMDCenter.getInstance().printLog("[handleConfirmBoardReply] error, state mismatch");
            return;
        }

        int result = ((Double) data.get(CMDKey.RESULT)).intValue();
        if (result != 0){
            CMDCenter.getInstance().printLog("[handleConfirmBoardReply] error, confirm board fail");
            Toast.makeText(this, "上机校验失败", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mCountDownTimer != null){
            mCountDownTimer.cancel();
        }

        if (CMDCenter.getInstance().isConfirmBoard()) {

            CMDCenter.getInstance().setCurrentBoardSate(BoardState.Boarding);
            startGame();

            mCountDownTimer = new CountDownTimer(CMDCenter.getInstance().getUserBoardingTime(), 500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (CMDCenter.getInstance().getCurrentBoardSate() == BoardState.Boarding) {
                        mTvBoardingCountDown.setVisibility(View.VISIBLE);
                        mTvBoardingCountDown.setText(((millisUntilFinished / 1000) + 1) + "s");
                    }
                }

                @Override
                public void onFinish() {
                    if (CMDCenter.getInstance().getCurrentBoardSate() == BoardState.Boarding) {
                        enbleControl(false);
                        CMDCenter.getInstance().grub();
                    }
                }
            }.start();
        }
    }

    /**
     * 处理服务器返回的"游戏结果".
     */
    private void handleGameResult(final int rspSeq, String sessionID, Map<String, Object> data) {
        CMDCenter.getInstance().printLog("[handleGameResult] enter");

        if (!checkSessionID(sessionID)){
            return;
        }

        if (!checkIsMyMsg((LinkedTreeMap<String, String>) data.get(CMDKey.PLAYER))){
            return;
        }

        CMDCenter.getInstance().printLog("[handleGameResult], currentSate: " + CMDCenter.getInstance().getCurrentBoardSate());

        if (CMDCenter.getInstance().getCurrentBoardSate() != BoardState.WaitingGameResult) {
            // 服务器可能没有收到客户端发送的"确认收到游戏结果"消息，会继续发送"游戏结果"到客户端
            CMDCenter.getInstance().confirmGameResult(rspSeq, mContinueToPlay);

            CMDCenter.getInstance().printLog("[handleGameResult], remain handleGameResult from Server");
            return;
        }

        if (mDialogGameResult != null && mDialogGameResult.isVisible()) {
            CMDCenter.getInstance().printLog("[handleGameResult], confirm dialog is visible");
            return;
        }

        if (mCountDownTimer != null){
            mCountDownTimer.cancel();
        }

        int result = ((Double) data.get(CMDKey.RESULT)).intValue();
        String message;
        if (result == 1) {
            message = getString(R.string.grub_successfully);
        } else {
            message = getString(R.string.grub_failed);
        }

        mDialogGameResult = new GameResultDialog();
        mDialogGameResult.setTitle(message);
        mDialogGameResult.setGameResultCallback(new GameResultDialog.OnGameResultCallback() {
            @Override
            public void onGiveUpPlaying() {
                mCountDownTimer.cancel();
                mDialogGameResult.dismiss();

                CMDCenter.getInstance().confirmGameResult(rspSeq, false);
                reinitGame();
            }

            @Override
            public void onContinueToPlay() {
                mCountDownTimer.cancel();
                mDialogGameResult.dismiss();


                CMDCenter.getInstance().confirmGameResult(rspSeq, true);
                continueToPlay();
            }
        });

        mDialogGameResult.setCancelable(false);
        mDialogGameResult.show(getFragmentManager(), "GameResultDialog");

        mCountDownTimer = new CountDownTimer(10000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (CMDCenter.getInstance().getCurrentBoardSate() == BoardState.WaitingGameResult) {
                    mDialogGameResult.setContinueText(getString(R.string.continue_to_play, ((millisUntilFinished / 1000) + 1) + ""));
                }
            }

            @Override
            public void onFinish() {
                if (CMDCenter.getInstance().getCurrentBoardSate() == BoardState.WaitingGameResult) {
                    mDialogGameResult.dismiss();
                    reinitGame();
                }
            }
        }.start();
    }

    private void showGameInfo(Map<String, Object> data){

        int total = ((Double) data.get(CMDKey.TOTAL)).intValue();
        mTvRoomUserCount.setText(getString(R.string.room_user_count, total + ""));

        ArrayList queueList = (ArrayList) data.get(CMDKey.QUEUE);
        int myPosition = 0;
        if (queueList != null){
            mUsersInQueue = queueList.size();
            for (int index = 0, size = queueList.size(); index < size; index++) {
                if (PreferenceUtil.getInstance().getUserID().equals(((Map<String, Object>) queueList.get(index)).get(CMDKey.USER_ID))) {
                    myPosition = index;
                    break;
                }
            }
        }

        if (CMDCenter.getInstance().getCurrentBoardSate() == BoardState.Ended ||
                CMDCenter.getInstance().getCurrentBoardSate() == BoardState.Applying){

            boolean enable = true;
            if ( CMDCenter.getInstance().getCurrentBoardSate() == BoardState.Applying){
                enable = false;
            }

            if (mUsersInQueue == 0){
                String text = getString(R.string.start_game);
                showApplyBtn(enable, R.mipmap.start, text, text.length());
            }else {
                String text = getString(R.string.apply_grub) + "\n" + getString(R.string.current_queue_count, mUsersInQueue + "");
                showApplyBtn(enable, R.mipmap.book, text, getString(R.string.apply_grub).length());
            }

        }else if (CMDCenter.getInstance().getCurrentBoardSate() == BoardState.WaitingBoard
                || CMDCenter.getInstance().getCurrentBoardSate() == BoardState.ConfirmBoard) {

            boolean enable = true;
            if ( CMDCenter.getInstance().getCurrentBoardSate() == BoardState.ConfirmBoard){
                enable = false;
            }

            String text = getString(R.string.cancel_apply) + "\n" + getString(R.string.my_position, myPosition + "");
            showApplyBtn(enable, R.mipmap.cancel, text, getString(R.string.cancel_apply).length());
        }
    }


    /**
     * 房间人数更新.
     */
    private void handleGameInfoUpdate(Map<String, Object> data) {
        CMDCenter.getInstance().printLog("[handleGameInfoUpdate] enter");

        showGameInfo(data);
    }

    /**
     * 处理服务器返回的"游戏信息".
     */
    private void handleResponseGameInfo(int rspSeq, Map<String, Object> data){
        CMDCenter.getInstance().printLog("[handleResponseGameInfo] enter");

        if (!checkSeq(rspSeq)){
            return;
        }

        int gameTime = ((Double) data.get(CMDKey.GAME_TIME)).intValue();
        CMDCenter.getInstance().setUserBoardingTime(gameTime);

        showGameInfo(data);
    }

    /**
     * 处理来自服务器的消息.
     */
    private void handleRecvCustomCMD(String msg) {
        CMDCenter.getInstance().printLog("[handleRecvCustomCMD], msg: " + msg);

        Map<String, Object> map = getMapFromJson(msg);
        if (map == null) {
            CMDCenter.getInstance().printLog("[handleRecvCustomCMD] error, map is null");
            return;
        }

        int cmd = ((Double) map.get(CMDKey.CMD)).intValue();
        int rspSeq = ((Double) map.get(CMDKey.SEQ)).intValue();
        String sessionID = (String) map.get(CMDKey.SESSION_ID);

        Map<String, Object> data = (Map<String, Object>) map.get("data");
        if (data == null){
            CMDCenter.getInstance().printLog("[handleRecvCustomCMD] error, data is null");
            return;
        }

        switch (cmd) {
            case CMDCenter.CMD_APPLY_RESULT:
                handleApplyResult(rspSeq, data);
                break;
            case CMDCenter.CMD_REPLY_CANCEL_APPLY:
                handleReplyCancelApply(rspSeq, sessionID, data);
                break;
            case CMDCenter.CMD_GAME_READY:
                handleGameReady(rspSeq, sessionID, data);
                break;
            case CMDCenter.CMD_CONFIRM_BOARD_REPLY:
                handleConfirmBoardReply(rspSeq, sessionID, data);
                break;
            case CMDCenter.CMD_GAME_RESULT:
                handleGameResult(rspSeq, sessionID, data);
                break;
            case CMDCenter.CMD_RESPONSE_GAME_INFO:
                handleResponseGameInfo(rspSeq, data);
                break;
            case CMDCenter.CMD_GAME_INFO_UPDATE:
                handleGameInfoUpdate(data);
                break;
        }
    }

    public void showLogMenu() {

        final Dialog dialog = new Dialog(this);
        View viewBottom = LayoutInflater.from(this).inflate(R.layout.dialog_bottom, null);

        TextView tvUserID = viewBottom.findViewById(R.id.tv_user_id);
        tvUserID.setText("UserID: " + PreferenceUtil.getInstance().getUserID());
        tvUserID.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager cmb = (ClipboardManager) PlayActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(PreferenceUtil.getInstance().getUserID());
                Toast.makeText(PlayActivity.this, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        viewBottom.findViewById(R.id.btn_log_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogListActivity.actionStart(PlayActivity.this);
            }
        });

        viewBottom.findViewById(R.id.btn_upload_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZegoLiveRoom.uploadLog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PlayActivity.this, "上传日志成功!", Toast.LENGTH_SHORT).show();
                    }
                }, 2000);
            }
        });

        viewBottom.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(viewBottom);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    private void doLogout() {

        // 回复从CDN拉流
        ZegoLiveRoom.setConfig(ZegoConstants.Config.PREFER_PLAY_ULTRA_SOURCE + "=0");

        for (ZegoStream zegoStream : mListStream) {
            zegoStream.stopPlayStream();
        }

        mZegoLiveRoom.logoutRoom();
        CMDCenter.getInstance().reset();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    private void logout() {
        if (CMDCenter.getInstance().getCurrentBoardSate() != BoardState.Ended) {
            AlertDialog dialog = new AlertDialog.Builder(this).setMessage("正在游戏中，确定要离开吗？").setTitle("提示").setPositiveButton("离开", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    doLogout();
                    finish();
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create();
            dialog.show();
        }else{
            doLogout();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 退出
            logout();
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void actionStart(Activity activity, Room room) {
        Intent intent = new Intent(activity, PlayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("room", room);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }


}
