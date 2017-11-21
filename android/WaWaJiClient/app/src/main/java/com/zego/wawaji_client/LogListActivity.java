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

import java.util.LinkedList;


/**
 * Copyright © 2016 Zego. All rights reserved.
 * des:
 */
public class LogListActivity extends AppCompatActivity {

    public static final String KEY_LIST_LOG = "KEY_LIST_LOG";


    public RecyclerView recyclerView;

    private LogListAdapter mLogListAdapter;

    private LinkedList<String> mLinkedListData;

    private PreferenceUtil.OnChangeListener mOnChangeListener;

    public static void actionStart(Activity activity){
        Intent intent = new Intent(activity, LogListActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_list);

        mLinkedListData = (LinkedList<String>) PreferenceUtil.getInstance().getObjectFromString(KEY_LIST_LOG);
        if (mLinkedListData == null) {
            mLinkedListData = new LinkedList<>();
        }
        mOnChangeListener = new PreferenceUtil.OnChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (KEY_LIST_LOG.equals(key)) {
                    mLinkedListData.clear();
                    LinkedList<String> data = (LinkedList<String>) PreferenceUtil.getInstance().getObjectFromString(KEY_LIST_LOG);
                    if (data != null) {
                        mLinkedListData.addAll(data);
                    }
                    mLogListAdapter.notifyDataSetChanged();
                }
            }
        };
        PreferenceUtil.getInstance().registerOnChangeListener(mOnChangeListener);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLogListAdapter = new LogListAdapter(this, mLinkedListData);
        recyclerView.setAdapter(mLogListAdapter);


        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceUtil.getInstance().unregisterOnChangeListener(mOnChangeListener);
                finish();
            }
        });
    }
}
