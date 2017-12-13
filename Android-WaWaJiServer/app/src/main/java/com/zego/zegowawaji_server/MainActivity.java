package com.zego.zegowawaji_server;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zego.base.utils.AppLogger;
import com.zego.base.utils.ByteSizeUnit;
import com.zego.base.utils.DeviceIdUtil;
import com.zego.base.utils.PrefUtil;
import com.zego.base.widget.CustomSeekBar;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoCustomCommandCallback;
import com.zego.zegoliveroom.callback.IZegoLoginCompletionCallback;
import com.zego.zegoliveroom.constants.ZegoAvConfig;
import com.zego.zegoliveroom.constants.ZegoBeauty;
import com.zego.zegoliveroom.constants.ZegoConstants;
import com.zego.zegoliveroom.constants.ZegoVideoViewMode;
import com.zego.zegoliveroom.entity.ZegoStreamInfo;
import com.zego.zegoliveroom.entity.ZegoUser;
import com.zego.zegowawaji_server.callback.ZegoIMCallack;
import com.zego.zegowawaji_server.callback.ZegoLivePublisherCallback;
import com.zego.zegowawaji_server.callback.ZegoLivePublisherCallback2;
import com.zego.zegowawaji_server.callback.ZegoRoomCallback;
import com.zego.zegowawaji_server.manager.CommandSeqManager;
import com.zego.zegowawaji_server.service.GuardService;
import com.zego.zegowawaji_server.service.IRemoteApi;
import com.zego.zegowawaji_server.tcp.TcpSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IStateChangedListener, IRoomClient {

    static final private String ROOM_ID_PREFIX = "WWJ_ZEGO";
    static final private String STREAM_ID_PREFIX = "WWJ_ZEGO_STREAM";

    static final private int REQUEST_PERMISSION_CODE = 101;

    private TextureView mMainPreviewView;
    private TextureView mSecondPreviewView;

    private Spinner mLiveQualityView;
    private TextView mResolutionDescView;
    private CustomSeekBar mResolutionView;

    private TextView mFPSDescView;
    private CustomSeekBar mFPSView;

    private TextView mBitrateDescView;
    private CustomSeekBar mBitrateView;

    private TextView mTotalUserView;
    private TextView mQueueUserView;
    private TextView mCurrentOperatorView;

    private String[] mResolutionText;

    private ZegoLiveRoom mZegoLiveRoom;

    private HandlerThread mWorkThread;
    private Handler mWorkHandler;

    private Handler mRetryHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mZegoLiveRoom = ((ZegoApplication) getApplication()).getZegoLiveRoom();
        mResolutionText = getResources().getStringArray(R.array.zg_resolutions);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getNetTime();
            }
        }).start();

        mWorkThread = new HandlerThread("worker_thread", Thread.NORM_PRIORITY);
        mWorkThread.start();

        mWorkHandler = new Handler(mWorkThread.getLooper());

        mRetryHandler = new RetryHandler(Looper.getMainLooper());

        initCtrls();

        if (checkOrRequestPermission()) {
            startPreview();
            loginRoomAndPublishStream();
        }

        bindGuardService();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                String str = null;
//                str.length();
//            }
//        },5000);
//        FileUtil.writeLogFile(this,"111");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_log:
                startActivity(new Intent(this, LogActivity.class));
                return true;

            case R.id.action_leave:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("")   //R.string.vt_dialog_logout_title
                .setMessage(R.string.vt_dialog_logout_message)
                .setPositiveButton(R.string.vt_dialog_btn_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        quit();
                    }
                })
                .setNegativeButton(R.string.vt_dialog_btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nothing to do
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                boolean allPermissionGranted = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        allPermissionGranted = false;
                        Toast.makeText(this, getString(R.string.zg_toast_permission_denied, permissions[i]), Toast.LENGTH_LONG).show();
                    }
                }
                if (allPermissionGranted) {
                    startPreview();
                    loginRoomAndPublishStream();
                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            }
            break;
        }
    }

    /**
     * 通过绑定 Service 达到监听 UI 进程是否异常退出的目的
     */
    private void bindGuardService() {
        Intent intent = new Intent(this, GuardService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 正常退出时，必须解绑，否则会陷入死循环，无法退出应用
     */
    private void unbindGuardService() {
        if (mRemoteApi != null) {
            try {
                mRemoteApi.leave(mBinder);  // 必须调用
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        unbindService(mServiceConnection);
    }

    private void initCtrls() {
        mMainPreviewView = (TextureView) findViewById(R.id.main_preview_view);
        mSecondPreviewView = (TextureView) findViewById(R.id.second_preview_view);

        int defaultLevel = PrefUtil.getInstance().getLiveQuality();
        mLiveQualityView = (Spinner) findViewById(R.id.sp_resolutions);
        mLiveQualityView.setSelection(defaultLevel);
        mLiveQualityView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean showToast = false;  // 首次不显示  Toast，以忽略初始化时也显示提示
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeResolution(position, showToast);
                showToast = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        int defaultResolution = PrefUtil.getInstance().getLiveQualityResolution();
        mResolutionView = (CustomSeekBar) findViewById(R.id.sb_resolution);
        mResolutionView.setMax(ZegoAvConfig.VIDEO_BITRATES.length - 1);
        mResolutionView.setProgress(defaultResolution);
        mResolutionView.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mResolutionDescView = (TextView) findViewById(R.id.tv_resolution);
        mResolutionDescView.setText(getString(R.string.resolution_prefix, mResolutionText[defaultResolution]));

        int defaultFPS = PrefUtil.getInstance().getLiveQualityFps();
        mFPSView = (CustomSeekBar) findViewById(R.id.sb_fps);
        mFPSView.setMax(30);
        mFPSView.setProgress(defaultFPS);
        mFPSView.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mFPSDescView = (TextView) findViewById(R.id.tv_fps);
        mFPSDescView.setText(getString(R.string.fps_prefix, String.valueOf(defaultFPS)));

        int defaultBitrate = PrefUtil.getInstance().getLiveQualityBitrate();
        mBitrateView = (CustomSeekBar) findViewById(R.id.sb_bitrate);
        mBitrateView.setMax(ZegoAvConfig.VIDEO_BITRATES[ZegoAvConfig.VIDEO_BITRATES.length - 1] + 1000 * 1000);
        mBitrateView.setProgress(defaultBitrate);
        mBitrateView.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mBitrateDescView = (TextView) findViewById(R.id.tv_bitrate);
        mBitrateDescView.setText(getString(R.string.bitrate_prefix, ByteSizeUnit.toHumanString(defaultBitrate, ByteSizeUnit.RADIX_TYPE.K, 2)) + "ps");

        mTotalUserView = (TextView) findViewById(R.id.zg_current_total_user);
        mTotalUserView.setText(Html.fromHtml(getString(R.string.zg_text_current_total_user, 0)));

        mQueueUserView = (TextView) findViewById(R.id.zg_current_queue_user);
        mQueueUserView.setText(Html.fromHtml(getString(R.string.zg_text_current_queue_user, 0)));

        mCurrentOperatorView = (TextView) findViewById(R.id.zg_current_operator);
        mCurrentOperatorView.setText(getString(R.string.zg_text_current_no_player));
    }

    private boolean checkOrRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
                return false;
            }
        }
        return true;
    }

    private void startPreview() {
//        mZegoLiveRoom.setAppOrientation(Surface.ROTATION_90);
//        mZegoLiveRoom.setAppOrientation(Surface.ROTATION_90, ZegoConstants.PublishChannelIndex.AUX);
        mZegoLiveRoom.enableMic(false);

        mZegoLiveRoom.enableCamera(true);
        mZegoLiveRoom.setFrontCam(true);
        mZegoLiveRoom.enablePreviewMirror(false);
        mZegoLiveRoom.setPreviewView(mMainPreviewView);
        mZegoLiveRoom.setPreviewViewMode(ZegoVideoViewMode.ScaleAspectFill);
        mZegoLiveRoom.startPreview();

        int channelIndex = ZegoConstants.PublishChannelIndex.AUX;
        mZegoLiveRoom.enableCamera(true, channelIndex);
        mZegoLiveRoom.setFrontCam(false, channelIndex);
        mZegoLiveRoom.setPreviewView(mSecondPreviewView, channelIndex);
        mZegoLiveRoom.setPreviewViewMode(ZegoVideoViewMode.ScaleAspectFill, channelIndex);
        mZegoLiveRoom.startPreview(channelIndex);

        AppLogger.getInstance().writeLog("Start preview");

        mZegoLiveRoom.enableBeautifying(ZegoBeauty.SHARPEN | ZegoBeauty.WHITEN);
        mZegoLiveRoom.setSharpenFactor(0.5f);
        mZegoLiveRoom.setWhitenFactor(1.0f);
        mZegoLiveRoom.enableBeautifying(ZegoBeauty.SHARPEN | ZegoBeauty.WHITEN | channelIndex);
        mZegoLiveRoom.setSharpenFactor(0.5f,channelIndex);
        mZegoLiveRoom.setWhitenFactor(1.0f,channelIndex);

    }

    private void loginRoomAndPublishStream() {
        // 初始化房间名及流名信息
        initRoomAndStreamInfo();

        boolean success = loginRoom(false);
        if (!success) { // 登录失败， 1 分钟后重试
            AppLogger.getInstance().writeLog("Login room failed");
            Toast.makeText(this, R.string.zg_toast_login_room_failed, Toast.LENGTH_LONG).show();

            mRetryHandler.removeMessages(RetryHandler.MSG_REPUBLISH_STREAM);
            mRetryHandler.sendEmptyMessageDelayed(RetryHandler.MSG_RELOGIN_ROOM, 60 * 1000);
        }
    }

    private void initRoomAndStreamInfo() {
        PrefUtil prefUtil = PrefUtil.getInstance();
        Log.e("zhen",prefUtil.getRoomId());
        if (TextUtils.isEmpty(prefUtil.getRoomId())
                || TextUtils.isEmpty(prefUtil.getRoomName())
                || TextUtils.isEmpty(prefUtil.getStreamId())
                || TextUtils.isEmpty(prefUtil.getStreamId2())) {
            String deviceId = DeviceIdUtil.generateDeviceId(this);

            String roomId = String.format("%s_%s", ROOM_ID_PREFIX, deviceId);
            PrefUtil.getInstance().setRoomId(roomId);
            AppLogger.getInstance().writeLog("initRoomAndStreamInfo, roomid: %s", roomId);

            // 对娃娃机名做特殊处理以区分是即构的还是开发者的
            if (deviceId.startsWith("12345_5432")) {
                int deviceNo = Integer.valueOf(deviceId.substring(deviceId.length() - 1));
                String roomName = getString(R.string.zg_text_wawaji_name_template, deviceNo);
                PrefUtil.getInstance().setRoomName(roomName);
            } else {
                PrefUtil.getInstance().setRoomName(roomId);
            }

            String streamId = String.format("%s_%s", STREAM_ID_PREFIX, deviceId);
            PrefUtil.getInstance().setStreamId(streamId);

            String streamId2 = String.format("%s_%s_2", STREAM_ID_PREFIX, deviceId);
            PrefUtil.getInstance().setStreamId2(streamId2);
        }
    }

    /**
     * 登录房间
     * @param retry 是否为失败后重试逻辑
     * @return
     */
    private boolean loginRoom(boolean retry) {
        if (!retry) {// 重试逻辑，不需要重新设置 callback 及 room config
            setupCallbacks();
        }

        mZegoLiveRoom.setRoomConfig(false, true);
        String roomId = PrefUtil.getInstance().getRoomId();
        String roomName = PrefUtil.getInstance().getRoomName();
        return mZegoLiveRoom.loginRoom(roomId, roomName, ZegoConstants.RoomRole.Anchor, new IZegoLoginCompletionCallback() {
            @Override
            public void onLoginCompletion(int errorCode, ZegoStreamInfo[] streamList) {
                AppLogger.getInstance().writeLog("onLoginCompletion, errorCode: %d", errorCode);
                if (errorCode == 0) {
                    publishStream(-1);
                } else {
                    Toast.makeText(MainActivity.this, R.string.zg_toast_login_room_failed, Toast.LENGTH_LONG).show();

                    mRetryHandler.removeMessages(RetryHandler.MSG_REPUBLISH_STREAM);
                    mRetryHandler.sendEmptyMessageDelayed(RetryHandler.MSG_RELOGIN_ROOM, 60 * 1000);
                }
            }
        });
    }

    private void setupCallbacks() {
        mZegoLiveRoom.setZegoLivePublisherCallback(new ZegoLivePublisherCallback(this));
        mZegoLiveRoom.setZegoLivePublisherCallback2(new ZegoLivePublisherCallback2(this));
        mZegoLiveRoom.setZegoRoomCallback(new ZegoRoomCallback(this, this));
        mZegoLiveRoom.setZegoIMCallback(new ZegoIMCallack(this, this));
    }

    private void publishStream(int channelIndex) {
        mZegoLiveRoom.enableDTX(true);
        mZegoLiveRoom.setLatencyMode(ZegoConstants.LatencyMode.Low2);

        // 开启自动流量监控
        int properties = ZegoConstants.ZegoTrafficControlProperty.ZEGOAPI_TRAFFIC_FPS
                | ZegoConstants.ZegoTrafficControlProperty.ZEGOAPI_TRAFFIC_RESOLUTION;
        mZegoLiveRoom.enableTrafficControl(properties, true);

        String extraInfo = generateStreamExtraInfo();   // 为第一条流添加附加信息

        if (channelIndex == -1 || channelIndex == ZegoConstants.PublishChannelIndex.MAIN) {
            String streamId = PrefUtil.getInstance().getStreamId();
            boolean success = mZegoLiveRoom.startPublishing(streamId, "", ZegoConstants.PublishFlag.JoinPublish, extraInfo);
            AppLogger.getInstance().writeLog("Publish main stream [%s] success ? %s", streamId, success);
            Log.e("zhen","Publish main stream  success | "+streamId);

            if (!success) {
                republishStreamDelay(ZegoConstants.PublishChannelIndex.MAIN);
            }
        }

        if (channelIndex == -1 || channelIndex == ZegoConstants.PublishChannelIndex.AUX) {
            String streamId2 = PrefUtil.getInstance().getStreamId2();
            boolean success = mZegoLiveRoom.startPublishing2(streamId2, "", ZegoConstants.PublishFlag.JoinPublish, ZegoConstants.PublishChannelIndex.AUX);
            AppLogger.getInstance().writeLog("Publish second stream [%s] success ? %s", streamId2, success);
            Log.e("zhen","Publish second stream  success | "+streamId2);

            if (!success) {
                republishStreamDelay(ZegoConstants.PublishChannelIndex.AUX);
            }
        }
    }

    private void republishStreamDelay(int channelIndex) {
        Message msg = Message.obtain();
        msg.what = RetryHandler.MSG_REPUBLISH_STREAM;
        msg.arg1 = channelIndex;
        mRetryHandler.sendMessageDelayed(msg, 60 * 1000);
    }

    private String generateStreamExtraInfo() {
        JSONObject extraInfo = new JSONObject();
        try {
            extraInfo.put(Constants.JsonKey.KEY_USER_TOTAL, mTotalUsers.size());
            extraInfo.put(Constants.JsonKey.KEY_QUEUE_NUMBER, mQueueUsers.size());

            JSONObject player = new JSONObject();
            player.put(Constants.JsonKey.KEY_USER_ID, mCurrentPlayer.userID);
            player.put(Constants.JsonKey.KEY_USER_NAME, mCurrentPlayer.userName);
            extraInfo.put(Constants.JsonKey.KEY_PLAYER, player);
        } catch (JSONException e) {
            AppLogger.getInstance().writeLog("create stream extra info failed. " + e);
        }
        return extraInfo.toString();
    }

    /**
     * 序列化房间内用户状态信息
     * @return
     */
    private String generateUserUpdateCommand(int seq) {
        JSONObject cmdInfo = new JSONObject();
        try {
            cmdInfo.put(Constants.JsonKey.KEY_SEQ, seq);
            cmdInfo.put(Constants.JsonKey.KEY_CMD, Constants.Command.CMD_USER_UPDATE);

            JSONObject data = new JSONObject();
            data.put(Constants.JsonKey.KEY_USER_TOTAL, mTotalUsers.size());

            JSONArray queueData = new JSONArray();
            for (ZegoUser zegoUser : mQueueUsers) {
                JSONObject jsonUser = new JSONObject();
                jsonUser.put(Constants.JsonKey.KEY_USER_ID, zegoUser.userID);
                jsonUser.put(Constants.JsonKey.KEY_USER_NAME, zegoUser.userName);
                queueData.put(jsonUser);
            }
            data.put(Constants.JsonKey.KEY_USER_QUEUE, queueData);

            JSONObject player = new JSONObject();
            player.put(Constants.JsonKey.KEY_USER_ID, mCurrentPlayer.userID);
            player.put(Constants.JsonKey.KEY_USER_NAME, mCurrentPlayer.userName);
            data.put(Constants.JsonKey.KEY_PLAYER, player);
            data.put(Constants.JsonKey.KEY_TIME_STAMP, System.currentTimeMillis());

            cmdInfo.put(Constants.JsonKey.KEY_DATA, data);
        } catch (JSONException e) {
            AppLogger.getInstance().writeLog("create stream extra info failed. " + e);
        }
        return cmdInfo.toString();
    }

    private void quit() {
        mZegoLiveRoom.stopPublishing();
        mZegoLiveRoom.stopPublishing(ZegoConstants.PublishChannelIndex.AUX);

        mZegoLiveRoom.logoutRoom();

        mZegoLiveRoom.setZegoLivePublisherCallback(null);
        mZegoLiveRoom.setZegoLivePlayerCallback(null);
        mZegoLiveRoom.setZegoRoomCallback(null);
        mZegoLiveRoom.setZegoIMCallback(null);

        mZegoLiveRoom.unInitSDK();

        mRetryHandler.removeCallbacksAndMessages(null);

        mWorkHandler.removeCallbacksAndMessages(null);
        if (Build.VERSION.SDK_INT >= 18) {
            mWorkThread.quitSafely();
        } else {
            mWorkThread.quit();
        }


        unbindGuardService();

        finish();

        System.exit(0);
    }

    private void changeResolution(int level, boolean showToast) {
        if (level < ZegoAvConfig.VIDEO_BITRATES.length) {
            mResolutionView.setEnabled(false);
            mFPSView.setEnabled(false);
            mBitrateView.setEnabled(false);

            mResolutionView.setProgress(level);
            // 预设级别中,帧率固定为"15"
            mFPSView.setProgress(15);
            mBitrateView.setProgress(ZegoAvConfig.VIDEO_BITRATES[level]);

            PrefUtil.getInstance().setLiveQuality(level);

            if (showToast) {
                Toast.makeText(this, R.string.zg_toast_change_quality_success, Toast.LENGTH_LONG).show();
            }
        } else {
            mResolutionView.setEnabled(true);
            mFPSView.setEnabled(true);
            mBitrateView.setEnabled(true);

            PrefUtil.getInstance().setLiveQuality(ZegoAvConfig.VIDEO_BITRATES.length);

            if (showToast) {
                Toast.makeText(this, R.string.zg_toast_reopen_app, Toast.LENGTH_LONG).show();
            }
        }
    }

    private List<ZegoUser> mTotalUsers = Collections.synchronizedList(new ArrayList<ZegoUser>());
    private List<ZegoUser> mQueueUsers = Collections.synchronizedList(new ArrayList<ZegoUser>());
    private ZegoUser mCurrentPlayer = new ZegoUser();

    /**
     * override from IStateChangedListener
     */
    @Override
    public void onRoomStateUpdate() {
        runOnWorkThread(new Runnable() {
            @Override
            public void run() {
                // 更新页面信息
                final int totalCount = mTotalUsers.size();
                final int queueCount = mQueueUsers.size();
                final boolean isPlaying = !TextUtils.isEmpty(mCurrentPlayer.userID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTotalUserView.setText(Html.fromHtml(getString(R.string.zg_text_current_total_user, totalCount)));
                        mQueueUserView.setText(Html.fromHtml(getString(R.string.zg_text_current_queue_user, queueCount)));
                        if (isPlaying) {
                            mCurrentOperatorView.setText(Html.fromHtml(getString(R.string.zg_text_current_player, mCurrentPlayer.userName)));
                        } else {
                            mCurrentOperatorView.setText(getString(R.string.zg_text_current_no_player));
                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                new TcpSocket().sendMessage("{\"message_type\":\"roomStatus\",\"data\":{\"status\":"+(isPlaying?2:1)+", \"room_id\":\""+PrefUtil.getInstance().getRoomId()+"\"}}\n");
                            }
                        }).start();
                    }
                });

                // 更新流信息
                String streamExtraInfo = generateStreamExtraInfo();
                mZegoLiveRoom.updateStreamExtraInfo(streamExtraInfo);

                // 广播通知房间内其他用户，不检查是否发送成功
                ZegoUser[] allMembers = new ZegoUser[mTotalUsers.size()];
                mTotalUsers.toArray(allMembers);

                int seq = CommandSeqManager.getInstance().getAndIncreaseSequence();
                String cmdContent = generateUserUpdateCommand(seq);
                boolean success = mZegoLiveRoom.sendCustomCommand(allMembers, cmdContent, new IZegoCustomCommandCallback() {
                    @Override
                    public void onSendCustomCommand(int errorCode, String roomId) {
                        AppLogger.getInstance().writeLog("broadcast User Update Command result: %d", errorCode);
                    }
                });

                AppLogger.getInstance().writeLog("broadcast User Update Command success? %s", success);
            }
        });
    }

    /**
     * override from IStateChangedListener
     * @param width
     * @param height
     * @param channelIndex
     */
    public void onVideoCaptureSizeChanged(int width, int height, int channelIndex) {
        if (channelIndex == ZegoConstants.PublishChannelIndex.MAIN) {
            ViewGroup.LayoutParams params = mMainPreviewView.getLayoutParams();
            if (params != null) {
                int newHeight = params.width * height / width;
                params.height = newHeight;
                mMainPreviewView.setLayoutParams(params);
            }
        } else if (channelIndex == ZegoConstants.PublishChannelIndex.AUX) {
            ViewGroup.LayoutParams params = mSecondPreviewView.getLayoutParams();
            if (params != null) {
                int newHeight = params.width * height / width;
                params.height = newHeight;
                mSecondPreviewView.setLayoutParams(params);
            }
        }
    }

    /**
     * override from IStateChangedListener
     */
    public void onPublishStateUpdate(int stateCode, String streamId) {
        if (stateCode != 0) { // retry
            AppLogger.getInstance().writeLog("republish stream : %s just a moment", streamId);

            String savedStreamId = PrefUtil.getInstance().getStreamId();
            String savedStreamId2 = PrefUtil.getInstance().getStreamId2();
            if (TextUtils.equals(savedStreamId, streamId)) {
                republishStreamDelay(ZegoConstants.PublishChannelIndex.MAIN);
            } else if (TextUtils.equals(savedStreamId2, streamId)) {
                republishStreamDelay(ZegoConstants.PublishChannelIndex.AUX);
            } else {
                AppLogger.getInstance().writeLog("unknown streamId : %s", streamId);
            }

        }
    }

    /**
     * override from IStateChangedListener
     */
    public void onDisconnect() {
        mRetryHandler.removeMessages(RetryHandler.MSG_RELOGIN_ROOM);
        mRetryHandler.removeMessages(RetryHandler.MSG_REPUBLISH_STREAM);

        mZegoLiveRoom.stopPublishing(ZegoConstants.PublishChannelIndex.MAIN);
        mZegoLiveRoom.stopPublishing(ZegoConstants.PublishChannelIndex.AUX);
        mZegoLiveRoom.logoutRoom();

        AppLogger.getInstance().writeLog("disconnect the server, relogin room and then publish stream");
        loginRoomAndPublishStream();
    }

    /**
     * override from IRoomClient
     * @return
     */
    @Override
    public List<ZegoUser> getTotalUser() {
        return mTotalUsers;
    }

    /**
     * override from IRoomClient
     * @return
     */
    @Override
    public List<ZegoUser> getQueueUser() {
        return mQueueUsers;
    }

    /**
     * override from IRoomClient
     */
    @Override
    public void updateCurrentPlayerInfo(String userId, String userName) {
        mCurrentPlayer.userID = userId;
        mCurrentPlayer.userName = userName;
    }

    /**
     * override from IRoomClient
     * @return
     */
    @Override
    public ZegoLiveRoom getZegoLiveRoom() {
        return mZegoLiveRoom;
    }

    @Override
    public void runOnWorkThread(Runnable task) {
        if (Looper.myLooper() == mWorkHandler.getLooper()) {
            task.run();
        } else {
            mWorkHandler.post(task);
        }
    }

    private Binder mBinder = new Binder();
    private IRemoteApi mRemoteApi;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("ZEGO_WWJ","ServiceConnection : onServiceConnected");
            mRemoteApi = IRemoteApi.Stub.asInterface(service);
            try {
                mRemoteApi.join(mBinder);   // 此处为 UI 进程异常退出时能重启的关键
                Log.d("ZEGO_WWJ","ServiceConnection : onServiceConnected join success");
            } catch (RemoteException e) {
                Log.d("ZEGO_WWJ","ServiceConnection : onServiceConnected join failed");
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mRemoteApi != null) {
                try {
                    mRemoteApi.leave(mBinder);
                    Log.d("ZEGO_WWJ","ServiceConnection : onServiceDisconnected leave success");
                } catch (RemoteException e) {
                    Log.d("ZEGO_WWJ","ServiceConnection : onServiceDisconnected leave failed");
                    e.printStackTrace();
                }
            }
        }
    };

    private AppCompatSeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new AppCompatSeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.sb_resolution:
                    mResolutionDescView.setText(getString(R.string.resolution_prefix, mResolutionText[progress]));
                    PrefUtil.getInstance().setLiveQualityResolution(progress);
                    break;

                case R.id.sb_fps:
                    mFPSDescView.setText(getString(R.string.fps_prefix, progress + ""));
                    PrefUtil.getInstance().setLiveQualityFps(progress);
                    break;

                case R.id.sb_bitrate:
                    mBitrateDescView.setText(getString(R.string.bitrate_prefix, ByteSizeUnit.toHumanString(progress, ByteSizeUnit.RADIX_TYPE.K, 2)) + "ps");
                    PrefUtil.getInstance().setLiveQualityBitrate(progress);
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private class RetryHandler extends Handler {

        static final int MSG_RELOGIN_ROOM = 1;
        static final int MSG_REPUBLISH_STREAM = 2;

        public RetryHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RELOGIN_ROOM:
                    AppLogger.getInstance().writeLog("relogin room");
                    loginRoom(true);
                    break;

                case MSG_REPUBLISH_STREAM:
                    int channelIndex = msg.arg1;
                    AppLogger.getInstance().writeLog("republish stream: %d", channelIndex);
                    publishStream(channelIndex);
                    break;

                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private void getNetTime() {
        URL url = null;//取得资源对象
        try {
            url = new URL("http://www.baidu.com");
            //url = new URL("http://www.ntsc.ac.cn");//中国科学院国家授时中心
            //url = new URL("http://www.bjtime.cn");
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接
            long ld = uc.getDate(); //取得网站日期时间
            DateFormat formatter = new SimpleDateFormat("yyyyMMdd.HHmmss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ld);
            final String format = formatter.format(calendar.getTime());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "当前网络时间为: \n" + format, Toast.LENGTH_SHORT).show();
                    setSystemDate(format);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //https://stackoverflow.com/questions/19496907/set-date-time-using-adb-shell
    public void setSystemDate(String str)
    {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("toolbox date -s "+str);
            os.flush();
        } catch (Exception e) {
            Log.d("MainActivity","error=="+e.toString());
            e.printStackTrace();
        }
    }
}
