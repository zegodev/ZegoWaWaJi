package com.zego.zegowawaji_server;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zego.base.utils.AppLogger;
import com.zego.zegowawaji_server.manager.DeviceManager;

import java.util.List;
import java.util.Random;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 25/11/2017.
 */

public class TestActivity extends AppCompatActivity {

    private AppLogger.OnLogChangedListener mLogDataChangedListener;

    private Button btnInit;
    private Button btnLeft;
    private Button btnRight;
    private Button btnForward;
    private Button btnBackward;
    private Button btnGrab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // enable the up button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        initCtrls();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        AppLogger.getInstance().unregisterLogChangedListener(mLogDataChangedListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initCtrls() {
        btnInit = (Button) findViewById(R.id.init);
        btnInit.setOnClickListener(mOperationListener);

        btnLeft = (Button) findViewById(R.id.left);
//        btnLeft.setOnClickListener(mOperationListener);
        btnLeft.setOnTouchListener(mTouchListener);
//
        btnRight = (Button) findViewById(R.id.right);
//        btnRight.setOnClickListener(mOperationListener);
        btnRight.setOnTouchListener(mTouchListener);
//
        btnForward = (Button) findViewById(R.id.forward);
//        btnForward.setOnClickListener(mOperationListener);
        btnForward.setOnTouchListener(mTouchListener);
//
        btnBackward = (Button) findViewById(R.id.backward);
//        btnBackward.setOnClickListener(mOperationListener);
        btnBackward.setOnTouchListener(mTouchListener);

        btnGrab = (Button) findViewById(R.id.grab);
        btnGrab.setOnClickListener(mOperationListener);

        updateCtrlsState(0);

        final TextView logView = (TextView) findViewById(R.id.log);

        mLogDataChangedListener = new AppLogger.OnLogChangedListener() {
            @Override
            public void onLogDataChanged() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder sb = new StringBuilder();
                        List<String> logs = AppLogger.getInstance().getAllLog();
                        int size = logs.size();
                        for (int i = 0; i < size; i++) {
                            sb.append(logs.get(i)).append("\r\n");
                        }
                        logView.setText(sb.toString());
                    }
                });
            }
        };
        AppLogger.getInstance().registerLogChangedListener(mLogDataChangedListener);
    }

    private void updateCtrlsState(int state) {
        boolean canOperation = false;
        boolean shouldInit = true;

        if (state == 0) {   // init
            // default state
        } else if (state == 1) {    // init success
            canOperation = true;
            shouldInit = false;
        } else if (state == 2) {    // waiting result
            canOperation = false;
            shouldInit = false;
        }

        btnLeft.setEnabled(canOperation);
        btnRight.setEnabled(canOperation);
        btnForward.setEnabled(canOperation);
        btnBackward.setEnabled(canOperation);
        btnGrab.setEnabled(canOperation);
        btnInit.setEnabled(shouldInit);
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    switch (v.getId()) {
                        case R.id.left:
                            DeviceManager.getInstance().sendLeftCmd();
                            return true;

                        case R.id.right:
                            DeviceManager.getInstance().sendRightCmd();
                            return true;

                        case R.id.forward:
                            DeviceManager.getInstance().sendForwardCmd();
                            return true;

                        case R.id.backward:
                            DeviceManager.getInstance().sendBackwardCmd();
                            return true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    DeviceManager.getInstance().sendStopCmd();
                    return true;
            }
            return false;
        }
    };

    private View.OnClickListener mOperationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.init: {
                    Random random = new Random(System.currentTimeMillis());

                    int grabPower = Math.max(50, random.nextInt(100));
                    int upPower = Math.min(grabPower, random.nextInt(100));
                    int movePower = Math.min(upPower, random.nextInt(100));
                    int upHeight = random.nextInt(10);
                    DeviceManager.getInstance().initGameConfig(30, grabPower, upPower, movePower, upHeight);

                    updateCtrlsState(1);
                }
                    break;

                case R.id.left:
                    DeviceManager.getInstance().sendLeftCmd();
                    break;

                case R.id.right:
                    DeviceManager.getInstance().sendRightCmd();
                    break;

                case R.id.forward:
                    DeviceManager.getInstance().sendForwardCmd();
                    break;

                case R.id.backward:
                    DeviceManager.getInstance().sendBackwardCmd();
                    break;

                case R.id.grab:
                    updateCtrlsState(2);

                    DeviceManager.getInstance().sendDownCmd(new DeviceManager.OnGameOverObserver() {
                        @Override
                        public void onGameOver(boolean win) {
                            AppLogger.getInstance().writeLog("game result, win? %s ", win);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateCtrlsState(0);
                                }
                            });
                        }
                    });
                    break;
            }
        }
    };
}
