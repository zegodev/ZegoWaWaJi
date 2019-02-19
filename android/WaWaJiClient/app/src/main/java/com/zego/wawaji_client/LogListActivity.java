package com.zego.wawaji_client;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zego.wawaji.R;
import com.zego.wawaji_client.adapter.LogListAdapter;
import com.zego.wawaji_client.utils.AppLogger;
import com.zego.wawaji_client.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Copyright © 2016 Zego. All rights reserved.
 * des:
 */
public class LogListActivity extends AppCompatActivity {

    public static final String KEY_LIST_LOG = "KEY_LIST_LOG";


    public RecyclerView recyclerView;

    private LogListAdapter mLogListAdapter;

    private List<String> mLinkedListData;

    private PreferenceUtil.OnChangeListener mOnChangeListener;

    private AppLogger.OnLogChangedListener onLogChangedListener;

    public static void actionStart(Activity activity) {
        Intent intent = new Intent(activity, LogListActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_list);

        mLinkedListData = AppLogger.getInstance().getAllLog();
        if (mLinkedListData == null) {
            mLinkedListData = new ArrayList<>();
        }


        onLogChangedListener = new AppLogger.OnLogChangedListener() {
            @Override
            public void onLogDataChanged() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        List<String> data = AppLogger.getInstance().getAllLog();
                        if (data != null) {
                          //  mLinkedListData.addAll(data);
                            mLogListAdapter.setDatas(data);
                        }

                    }
                });
            }
        };

        AppLogger.getInstance().registerLogChangedListener(onLogChangedListener);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLogListAdapter = new LogListAdapter(this, mLinkedListData);
        recyclerView.setAdapter(mLogListAdapter);


        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppLogger.getInstance().unregisterLogChangedListener(onLogChangedListener);

    }
}
