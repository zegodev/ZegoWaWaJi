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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.zego.wawaji.R;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoLivePlayerCallback;
import com.zego.zegoliveroom.callback.IZegoLivePublisherCallback;
import com.zego.zegoliveroom.callback.IZegoLoginCompletionCallback;
import com.zego.zegoliveroom.callback.IZegoRoomCallback;
import com.zego.zegoliveroom.constants.ZegoConstants;
import com.zego.zegoliveroom.constants.ZegoVideoViewMode;
import com.zego.zegoliveroom.entity.AuxData;
import com.zego.zegoliveroom.entity.ZegoStreamInfo;
import com.zego.zegoliveroom.entity.ZegoStreamQuality;
import com.zego.zegoliveroom.entity.ZegoUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zego.wawaji_client.ZegoStream.STREAM_STATE_FAIL;
import static com.zego.wawaji_client.ZegoStream.STREAM_STATE_SUCCESS;

/**
 * Copyright © 2017 Zego. All rights reserved.
 */

public class PlayActivity extends AppCompatActivity {

    private Room mRoom;

    private Dialog mDialog;
    private TextView mTvStreamSate;

    private ImageButton mIbtnSwitchCamera;
    private ImageButton mIBtnLeft;
    private ImageButton mIBtnForward;
    private ImageButton mIBtnBackward;
    private ImageButton mIBtnRight;

    private ImageButton mIBtnApply;
    private ImageButton mIBtnGO;
    private TextView mTvApplyHint;
    private RelativeLayout mRlytControlPannel;
    private TextView mTvBoardingCountDown;

    private ZegoLiveRoom mZegoLiveRoom;

    private int mSwitchCameraTimes = 0;
    private List<ZegoStream> mListStream = new ArrayList<>();

    private int mCurrentQueueCount = 0;
    private CountDownTimer mCountDownTimerConfirmBoard;
    private CountDownTimer mCountDownTimerBoarding;

    private ImageView mIvQuality;
    private TextView mTvQuality;
    private TextView mTvRoomUserCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            mRoom = (Room) intent.getSerializableExtra("room");
        } else {
            Toast.makeText(this, "房间信息初始化错误, 请重新开始", Toast.LENGTH_LONG).show();
            finish();
        }

        setTitle(mRoom.roomName);
        setContentView(R.layout.activity_play);

        if (mRoom.streamList.size() >= 2) {
            ZegoStream zegoStream1 = new ZegoStream();
            zegoStream1.streamID = mRoom.streamList.get(0);
            zegoStream1.textureView = (TextureView) findViewById(R.id.textureview1);
            zegoStream1.stateStrings = getResources().getStringArray(R.array.video1_state);
            mListStream.add(zegoStream1);

            ZegoStream zegoStream2 = new ZegoStream();
            zegoStream2.streamID = mRoom.streamList.get(1);
            zegoStream2.textureView = (TextureView) findViewById(R.id.textureview2);
            zegoStream2.stateStrings = getResources().getStringArray(R.array.video2_state);
            mListStream.add(zegoStream2);
        } else {
            Toast.makeText(this, "流信息数量错误, 请重新开始", Toast.LENGTH_LONG).show();
            finish();
        }

        mZegoLiveRoom = ZegoApiManager.getInstance().getZegoLiveRoom();
        initViews();
        startPlay();
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

    private void setControlPannel(boolean enable) {
        if (enable) {
            mRlytControlPannel.setVisibility(View.VISIBLE);
        } else {
            mRlytControlPannel.setVisibility(View.INVISIBLE);
        }

        mIBtnLeft.setEnabled(enable);
        mIBtnForward.setEnabled(enable);
        mIBtnRight.setEnabled(enable);
        mIBtnBackward.setEnabled(enable);
        mIBtnGO.setEnabled(enable);
        mTvBoardingCountDown.setText("");
    }

    private void pauseControlPannel() {
        mIBtnLeft.setEnabled(false);
        mIBtnForward.setEnabled(false);
        mIBtnRight.setEnabled(false);
        mIBtnBackward.setEnabled(false);
        mIBtnGO.setEnabled(false);
        mTvBoardingCountDown.setText("");
    }

    private void sendCMDFail(String cmd) {
        CommandUtil.getInstance().printLog("send cmd error: " + cmd);
        Toast.makeText(PlayActivity.this, getString(R.string.send_cmd_error), Toast.LENGTH_SHORT).show();
        reinitGame();
    }

    private void reinitGame() {
        CommandUtil.getInstance().setCurrentBoardSate(BoardState.Ended);
        setControlPannel(false);
        mIBtnApply.setEnabled(true);
        String msg = getString(R.string.apply_grub) + "\n" + getString(R.string.current_queue_count, mCurrentQueueCount + "");
        showApplyHint(msg, 6);
        mIBtnApply.setVisibility(View.VISIBLE);
        mTvApplyHint.setVisibility(View.VISIBLE);
    }

    private void startGame() {
        mIBtnApply.setVisibility(View.INVISIBLE);
        mTvApplyHint.setVisibility(View.INVISIBLE);
        setControlPannel(true);
    }

    private void showApplyHint(String msg, int separatePostion) {

        SpannableString ss = new SpannableString(msg);
        ss.setSpan(new TextAppearanceSpan(PlayActivity.this, R.style.tv_style1), 0, separatePostion,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new TextAppearanceSpan(PlayActivity.this, R.style.tv_style2), separatePostion,
                msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mTvApplyHint.setText(ss, TextView.BufferType.SPANNABLE);
    }

    private void initViews() {

        mTvBoardingCountDown = (TextView) findViewById(R.id.tv_boarding_countdown);

        // 状态提示
        mTvStreamSate = (TextView) findViewById(R.id.tv_stream_state);
        mTvStreamSate.setText(mListStream.get(0).getStateString());

        mRlytControlPannel = (RelativeLayout) findViewById(R.id.rlyt_control_pannel);

        mTvApplyHint = (TextView) findViewById(R.id.tv_apply_hint);
        mTvApplyHint.setText(getString(R.string.apply_grub));

        mIBtnApply = (ImageButton) findViewById(R.id.ibtn_apply);
        mIBtnApply.setEnabled(false);
        mIBtnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIBtnApply.setEnabled(false);
                if (CommandUtil.getInstance().getCurrentBoardSate() == BoardState.Ended) {
                    CommandUtil.getInstance().apply(new CommandUtil.OnCommandSendCallback() {
                        @Override
                        public void onSendSuccess() {

                        }

                        @Override
                        public void onSendFail() {
                            sendCMDFail("Apply");
                        }
                    });
                }
            }
        });

        mIBtnGO = (ImageButton) findViewById(R.id.ibtn_go);
        mIBtnGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommandUtil.getInstance().getCurrentBoardSate() == BoardState.Boarding) {

                    mCountDownTimerBoarding.cancel();

                    pauseControlPannel();

                    CommandUtil.getInstance().grub(new CommandUtil.OnCommandSendCallback() {
                        @Override
                        public void onSendSuccess() {

                        }

                        @Override
                        public void onSendFail() {
                            sendCMDFail("Grub");
                        }
                    });
                }
            }
        });

        // 切换摄像头
        mIbtnSwitchCamera = (ImageButton) findViewById(R.id.ibtn_switch_camera);
        mIbtnSwitchCamera.setEnabled(false);
        mIbtnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwitchCameraTimes++;

                mTvStreamSate.setVisibility(View.GONE);

                int index = mSwitchCameraTimes % 2;
                if (index == 1) {
                    mListStream.get(0).textureView.setVisibility(View.INVISIBLE);
                    mZegoLiveRoom.setPlayVolume(0, mListStream.get(0).streamID);

                    if (mListStream.get(1).isPlaySuccess()) {
                        mListStream.get(1).textureView.setVisibility(View.VISIBLE);
                        mZegoLiveRoom.setPlayVolume(100, mListStream.get(1).streamID);
                    } else {
                        mTvStreamSate.setText(mListStream.get(1).getStateString());
                        mTvStreamSate.setVisibility(View.VISIBLE);
                    }
                } else {
                    mListStream.get(1).textureView.setVisibility(View.INVISIBLE);
                    mZegoLiveRoom.setPlayVolume(0, mListStream.get(1).streamID);

                    if (mListStream.get(0).isPlaySuccess()) {
                        mListStream.get(0).textureView.setVisibility(View.VISIBLE);
                        mZegoLiveRoom.setPlayVolume(100, mListStream.get(0).streamID);
                    } else {
                        mTvStreamSate.setText(mListStream.get(0).getStateString());
                        mTvStreamSate.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        // 娃娃机操作
        mIBtnLeft = (ImageButton) findViewById(R.id.ibtn_left);
        mIBtnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSwitchCameraTimes % 2 == 1) {
                    CommandUtil.getInstance().moveLeft();
                } else {
                    CommandUtil.getInstance().moveForward();
                }
            }
        });

        mIBtnForward = (ImageButton) findViewById(R.id.ibtn_forward);
        mIBtnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSwitchCameraTimes % 2 == 1) {
                    CommandUtil.getInstance().moveForward();
                } else {
                    CommandUtil.getInstance().moveRight();
                }
            }
        });

        mIBtnBackward = (ImageButton) findViewById(R.id.ibtn_backward);
        mIBtnBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSwitchCameraTimes % 2 == 1) {
                    CommandUtil.getInstance().moveBackward();
                } else {
                    CommandUtil.getInstance().moveLeft();
                }

            }
        });

        mIBtnRight = (ImageButton) findViewById(R.id.ibtn_right);
        mIBtnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSwitchCameraTimes % 2 == 1) {
                    CommandUtil.getInstance().moveRight();
                } else {
                    CommandUtil.getInstance().moveBackward();
                }
            }
        });

        setControlPannel(false);

        mIvQuality = (ImageView) findViewById(R.id.iv_quality);
        mTvQuality = (TextView) findViewById(R.id.tv_quality);
        mTvRoomUserCount = (TextView) findViewById(R.id.tv_room_user_count);
    }

    private void startPlay() {
        mZegoLiveRoom.loginRoom(mRoom.roomID, ZegoConstants.RoomRole.Audience, new IZegoLoginCompletionCallback() {
            @Override
            public void onLoginCompletion(int errCode, ZegoStreamInfo[] zegoStreamInfos) {
                CommandUtil.getInstance().printLog("[onLoginCompletion], roomID: " + mRoom.roomID + ", errorCode: " + errCode + ", streamCount: " + zegoStreamInfos.length);
                if (errCode == 0) {
                    for (ZegoStreamInfo streamInfo : zegoStreamInfos) {
                        if (!TextUtils.isEmpty(streamInfo.extraInfo)) {

                            CommandUtil.getInstance().printLog("[onLoginCompletion], streamID: " + streamInfo.streamID + ", extraInfo: " + streamInfo.extraInfo);

                            mIBtnApply.setEnabled(true);

                            ZegoUser zegoUser = new ZegoUser();
                            zegoUser.userID = streamInfo.userID;
                            zegoUser.userName = streamInfo.userName;

                            CommandUtil.getInstance().setAnchor(zegoUser);

                            Map<String, Object> map = getMapFromJson(streamInfo.extraInfo);
                            if (map != null) {
                                int count = ((Double) map.get("queue_number")).intValue();
                                mCurrentQueueCount = count;

                                String msg = getString(R.string.apply_grub) + "\n" + getString(R.string.current_queue_count, count + "");
                                showApplyHint(msg, 6);

                                int total = ((Double) map.get("total")).intValue();
                                mTvRoomUserCount.setText(getString(R.string.room_user_count, total + ""));
                                mTvRoomUserCount.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    mIbtnSwitchCamera.setEnabled(true);
                }
            }
        });

        mZegoLiveRoom.startPlayingStream(mListStream.get(0).streamID, mListStream.get(0).textureView);
        mZegoLiveRoom.setViewMode(ZegoVideoViewMode.ScaleAspectFit, mListStream.get(0).streamID);
        mZegoLiveRoom.setPlayVolume(100, mListStream.get(0).streamID);

        mZegoLiveRoom.startPlayingStream(mListStream.get(1).streamID, mListStream.get(1).textureView);
        mZegoLiveRoom.setViewMode(ZegoVideoViewMode.ScaleAspectFit, mListStream.get(1).streamID);
        mZegoLiveRoom.setPlayVolume(0, mListStream.get(1).streamID);

        mZegoLiveRoom.setZegoLivePlayerCallback(new IZegoLivePlayerCallback() {
            @Override
            public void onPlayStateUpdate(int errCode, String streamID) {

                int currentShowIndex = mSwitchCameraTimes % 2;

                if (errCode != 0) {
                    for (ZegoStream zegoStream : mListStream) {
                        if (zegoStream.streamID.equals(streamID)) {
                            zegoStream.state = STREAM_STATE_FAIL;
                            break;
                        }
                    }

                    if (mListStream.get(currentShowIndex).streamID.equals(streamID)) {
                        mTvStreamSate.setText(mListStream.get(currentShowIndex).getStateString());
                        mTvStreamSate.setVisibility(View.VISIBLE);
                    }
                }

                CommandUtil.getInstance().printLog("[onPlayStateUpdate], streamID: " + streamID + " ,errorCode: " + errCode + ", currentShowIndex: " + currentShowIndex);
            }

            @Override
            public void onPlayQualityUpdate(String streamID, ZegoStreamQuality zegoStreamQuality) {
                // 当前显示的流质量
                if (mListStream.get(mSwitchCameraTimes % 2).streamID.equals(streamID)) {
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
                    if (zegoStream.streamID.equals(streamID)) {
                        zegoStream.state = STREAM_STATE_SUCCESS;
                        break;
                    }
                }

                int currentShowIndex = mSwitchCameraTimes % 2;
                if (mListStream.get(currentShowIndex).streamID.equals(streamID)) {
                    mTvStreamSate.setVisibility(View.GONE);
                    mListStream.get(currentShowIndex).textureView.setVisibility(View.VISIBLE);
                }

                CommandUtil.getInstance().printLog("[onVideoSizeChanged], streamID: " + streamID + ", currentShowIndex: " + currentShowIndex);
            }
        });

        mZegoLiveRoom.setZegoLivePublisherCallback(new IZegoLivePublisherCallback() {
            @Override
            public void onPublishStateUpdate(int errCode, String streamID, HashMap<String, Object> hashMap) {
                CommandUtil.getInstance().printLog("[onPublishStateUpdate], streamID: " + streamID + ", errorCode: " + errCode);
            }

            @Override
            public void onJoinLiveRequest(int i, String s, String s1, String s2) {

            }

            @Override
            public void onPublishQualityUpdate(String s, ZegoStreamQuality zegoStreamQuality) {

            }

            @Override
            public AuxData onAuxCallback(int i) {
                return null;
            }

            @Override
            public void onCaptureVideoSizeChangedTo(int i, int i1) {

            }

            @Override
            public void onMixStreamConfigUpdate(int i, String s, HashMap<String, Object> hashMap) {

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
                if (CommandUtil.getInstance().isCommandFromAnchor(userID) && mRoom.roomID.equals(roomID)) {
                    if (!TextUtils.isEmpty(content)) {
                        handleRecvCustomCMD(content);
                    }
                }
            }
        });
    }

    private Map<String, Object> getMapFromJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }

        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map = gson.fromJson(json, map.getClass());

        return map;
    }


    /**
     * 房间人数更新.
     */
    private void handleUserCountUpdate(Map<String, Object> data) {
        CommandUtil.getInstance().printLog("[handleUserCountUpdate]");
        if (data == null) {
            CommandUtil.getInstance().printLog("[handleUserCountUpdate], data is null");
            return;
        }

        ArrayList queueList = (ArrayList) data.get("queue");

        if (queueList != null) {
            mCurrentQueueCount = queueList.size();
            if (CommandUtil.getInstance().getCurrentBoardSate() == BoardState.Ended) {
                String msg = getString(R.string.apply_grub) + "\n" + getString(R.string.current_queue_count, mCurrentQueueCount + "");
                showApplyHint(msg, 6);
            } else if (CommandUtil.getInstance().getCurrentBoardSate() == BoardState.WaitingBoard) {
                for (int i = 0, size = queueList.size(); i < size; i++) {
                    if (PreferenceUtil.getInstance().getUserID().equals(((Map<String, Object>) queueList.get(i)).get("userId"))) {
                        String msg = getString(R.string.apply_grub) + "\n" + getString(R.string.apply_success, i + "");
                        showApplyHint(msg, 6);
                        break;
                    }
                }
            }
        }

        int total = ((Double) data.get("total")).intValue();
        mTvRoomUserCount.setText(getString(R.string.room_user_count, total + ""));
    }

    /**
     * "预约上机"结果.
     */
    private void handleApplyResult(Map<String, Object> data) {
        if (data == null) {
            CommandUtil.getInstance().printLog("[handleApplyResult], data is null");
            return;
        }

        CommandUtil.getInstance().printLog("[handleApplyResult], currentSate: " + CommandUtil.getInstance().getCurrentBoardSate());

        if (CommandUtil.getInstance().getCurrentBoardSate() != BoardState.Applying) {
            CommandUtil.getInstance().printLog("[handleApplyResult], state mismatch");
            return;
        }

        // 校验seq
        int rspSeq = ((Double) data.get("seq")).intValue();
        if (CommandUtil.getInstance().getCurrentSeq() != rspSeq) {
            CommandUtil.getInstance().printLog("[handleApplyResult], seq mismatch, rspSeq: " + rspSeq + ", currentSeq: " + CommandUtil.getInstance().getCurrentSeq());
            return;
        }

        int result = ((Double) data.get("result")).intValue();
        if (result == 0) {
            CommandUtil.getInstance().setCurrentBoardSate(BoardState.WaitingBoard);
            String msg = getString(R.string.apply_grub) + "\n" + getString(R.string.apply_success, ((Double) data.get("index")).intValue() + "");
            showApplyHint(msg, 6);
        } else {
            Toast.makeText(PlayActivity.this, getString(R.string.apply_faile), Toast.LENGTH_SHORT).show();
            reinitGame();
        }
    }

    /**
     * 准备游戏.
     */
    private void handleGameReady(final int rspSeq, Map<String, Object> data) {
        if (data == null) {
            CommandUtil.getInstance().printLog("[handleGameReady], data is null");
            return;
        }

        CommandUtil.getInstance().printLog("[handleGameReady], currentSate: " + CommandUtil.getInstance().getCurrentBoardSate());

        if (CommandUtil.getInstance().getCurrentBoardSate() == BoardState.Applying) {
            CommandUtil.getInstance().printLog("[handleGameReady], fix state");

            CommandUtil.getInstance().setCurrentBoardSate(BoardState.WaitingBoard);
        }

        if (CommandUtil.getInstance().getCurrentBoardSate() != BoardState.WaitingBoard) {
            CommandUtil.getInstance().printLog("[handleGameReady], state mismatch");
            return;
        }

        final String sessionData = (String) data.get("session_data");

        final AlertDialog confirmDialog = new AlertDialog.Builder(this).setMessage(getString(R.string.confirm_board, "10")).setTitle("提示").setPositiveButton("上机", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mCountDownTimerConfirmBoard.cancel();

                // 确认上机
                CommandUtil.getInstance().confirmBoard(rspSeq, sessionData, 1, new CommandUtil.OnCommandSendCallback() {
                    @Override
                    public void onSendSuccess() {

                    }

                    @Override
                    public void onSendFail() {
                        sendCMDFail("ConfirmBoard: 1");
                    }
                });
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mCountDownTimerConfirmBoard.cancel();

                // 放弃上机
                CommandUtil.getInstance().confirmBoard(rspSeq, sessionData, 0, new CommandUtil.OnCommandSendCallback() {
                    @Override
                    public void onSendSuccess() {

                    }

                    @Override
                    public void onSendFail() {
                        sendCMDFail("ConfirmBoard: 0");
                    }
                });

                reinitGame();
            }
        }).create();
        confirmDialog.setCanceledOnTouchOutside(false);
        confirmDialog.show();

        mCountDownTimerConfirmBoard = new CountDownTimer(10000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (CommandUtil.getInstance().getCurrentBoardSate() == BoardState.WaitingBoard) {
                    confirmDialog.setMessage(getString(R.string.confirm_board, ((millisUntilFinished / 1000) + 1) + ""));
                }
            }

            @Override
            public void onFinish() {
                if (CommandUtil.getInstance().getCurrentBoardSate() == BoardState.WaitingBoard) {
                    confirmDialog.dismiss();
                    reinitGame();
                }
            }
        }.start();
    }

    private void handleConfirmBoardReply(Map<String, Object> data) {
        if (data == null) {
            CommandUtil.getInstance().printLog("[handleConfirmBoardReply], data is null");
            return;
        }

        CommandUtil.getInstance().printLog("[handleConfirmBoardReply], currentSate: " + CommandUtil.getInstance().getCurrentBoardSate());

        if (CommandUtil.getInstance().getCurrentBoardSate() != BoardState.ConfirmBoard) {
            CommandUtil.getInstance().printLog("[handleConfirmBoardReply], state mismatch");
            return;
        }

        if (CommandUtil.getInstance().isConfirmBoard()) {
            // 推流
            mZegoLiveRoom.enableCamera(false);
            mZegoLiveRoom.enableMic(false);
            mZegoLiveRoom.enableDTX(true);
            mZegoLiveRoom.setAudioBitrate(8 * 1000);
            mZegoLiveRoom.startPublishing(mRoom.publishStreamID, "title_" + mRoom.publishStreamID, ZegoConstants.PublishFlag.JoinPublish);

            // 正在上机
            CommandUtil.getInstance().setCurrentBoardSate(BoardState.Boarding);
            startGame();

            mCountDownTimerBoarding = new CountDownTimer(30000, 500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (CommandUtil.getInstance().getCurrentBoardSate() == BoardState.Boarding) {
                        mTvBoardingCountDown.setVisibility(View.VISIBLE);
                        mTvBoardingCountDown.setText(((millisUntilFinished / 1000) + 1) + "s");
                    }
                }

                @Override
                public void onFinish() {
                    if (CommandUtil.getInstance().getCurrentBoardSate() == BoardState.Boarding) {
                        pauseControlPannel();

                        CommandUtil.getInstance().grub(new CommandUtil.OnCommandSendCallback() {
                            @Override
                            public void onSendSuccess() {

                            }

                            @Override
                            public void onSendFail() {
                                sendCMDFail("Grub");
                            }
                        });
                    }
                }
            }.start();
        }
    }

    private void handleGameResult(final int rspSeq, Map<String, Object> data) {
        if (data == null) {
            CommandUtil.getInstance().printLog("[handleGameResult], data is null");
            return;
        }

        Map<String, Object> player = (Map<String, Object>) data.get("player");
        if (player == null) {
            CommandUtil.getInstance().printLog("[handleGameResult], player is null");
            return;
        }

        if (!PreferenceUtil.getInstance().getUserID().equals(player.get("id"))) {
            CommandUtil.getInstance().printLog("[handleGameResult], not my message, don't care");
            return;
        }

        CommandUtil.getInstance().printLog("[handleGameResult], currentSate: " + CommandUtil.getInstance().getCurrentBoardSate());

        if (CommandUtil.getInstance().getCurrentBoardSate() != BoardState.WaitingGameResult) {
            String sessionData = (String) data.get("session_data");
            CommandUtil.getInstance().confirmGameResult(rspSeq, sessionData);
            CommandUtil.getInstance().printLog("[handleGameResult], remain handleGameResult from Server!");
            return;
        }

        CommandUtil.getInstance().setCurrentBoardSate(BoardState.Ended);

        int result = ((Double) data.get("result")).intValue();
        String message;
        if (result == 1) {
            message = getString(R.string.grub_successfully);
        } else {
            message = getString(R.string.grub_failed);
        }

        AlertDialog dialog = new AlertDialog.Builder(this).setMessage(message).setTitle("提示").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        reinitGame();
    }

    /**
     * 处理来自服务器的消息.
     */
    private void handleRecvCustomCMD(String msg) {
        CommandUtil.getInstance().printLog("[handleRecvCustomCMD], " + msg);

        Map<String, Object> map = getMapFromJson(msg);
        if (map == null) {
            CommandUtil.getInstance().printLog("[handleRecvCustomCMD], map is null");
            return;
        }

        int cmd = ((Double) map.get("cmd")).intValue();
        int rspSeq = ((Double) map.get("seq")).intValue();
        Map<String, Object> data = (Map<String, Object>) map.get("data");
        switch (cmd) {
            case CommandUtil.CMD_APPLY_RESULT:
                handleApplyResult(data);
                break;
            case CommandUtil.CMD_GAME_READY:
                handleGameReady(rspSeq, data);
                break;
            case CommandUtil.CMD_CONFIRM_BOARD_REPLY:
                handleConfirmBoardReply(data);
                break;
            case CommandUtil.CMD_GAME_RESULT:
                handleGameResult(rspSeq, data);
                break;
            case CommandUtil.CMD_USER_UPDATE:
                handleUserCountUpdate(data);
                break;
        }
    }

    public void showLogMenu() {

        mDialog = new Dialog(this);
        View viewBottom = LayoutInflater.from(this).inflate(R.layout.dialog_bottom, null);

        TextView tvUserID = viewBottom.findViewById(R.id.tv_user_id);
        tvUserID.setText("UserID: " + PreferenceUtil.getInstance().getUserID());
        tvUserID.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager cmb = (ClipboardManager)PlayActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
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
                mDialog.dismiss();
            }
        });

        mDialog.setContentView(viewBottom);
        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;
        dialogWindow.setAttributes(lp);
        mDialog.show();
    }

    private void doLogout() {
        mZegoLiveRoom.stopPlayingStream(mListStream.get(0).streamID);
        mZegoLiveRoom.stopPlayingStream(mListStream.get(1).streamID);
        mZegoLiveRoom.stopPublishing();
        mZegoLiveRoom.logoutRoom();
        CommandUtil.getInstance().reset();

        if (mCountDownTimerConfirmBoard != null) {
            mCountDownTimerConfirmBoard.cancel();
        }

        if (mCountDownTimerBoarding != null) {
            mCountDownTimerBoarding.cancel();
        }
    }

    private void logout() {
        if (CommandUtil.getInstance().getCurrentBoardSate() != BoardState.Ended) {
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
