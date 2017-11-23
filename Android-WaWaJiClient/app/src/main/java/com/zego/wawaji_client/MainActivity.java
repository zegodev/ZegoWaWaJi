package com.zego.wawaji_client;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zego.wawaji.R;
import com.zego.wawaji_client.controller.RoomController;
import com.zego.wawaji_client.entity.RoomInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Room> mRoomList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public TextView mTvHint;
    private Handler mHandler = new Handler();
    private RoomListAdapter mRoomListAdapter;
    private List<Integer> mListRoomIcon = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("即构抓娃娃");
        setContentView(R.layout.activity_main);

        initViews();
        doBusiness();
    }

    private void initViews() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl);
        // 设置 进度条的颜色变化，最多可以设置4种颜色
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark, android.R.color.holo_orange_dark);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 下拉刷新, 数据清零
                mRoomList.clear();
                RoomController.getInstance().getRoomList();
            }
        });

        mTvHint = (TextView) findViewById(R.id.tv_hint_pull_refresh);

        mRecyclerView = (RecyclerView) findViewById(R.id.rlv_room_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelOffset(R.dimen.dimen_5)));

        mRoomListAdapter = new RoomListAdapter(this, mRoomList);
        mRoomListAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                PlayActivity.actionStart(MainActivity.this, mRoomList.get(position));
            }
        });

        mRecyclerView.setAdapter(mRoomListAdapter);
    }

    private void doBusiness() {
        mListRoomIcon.add(R.mipmap.ic_room1);
        mListRoomIcon.add(R.mipmap.ic_room2);
        mListRoomIcon.add(R.mipmap.ic_room3);
        mListRoomIcon.add(R.mipmap.ic_room4);
        mListRoomIcon.add(R.mipmap.ic_room5);
        mListRoomIcon.add(R.mipmap.ic_room6);

        RoomController.getInstance().setUpdateRoomListListener(new RoomController.OnUpdateRoomListListener() {
            @Override
            public void onUpdateRoomList(List<RoomInfo> listRoom) {

                mRoomList.clear();

                for (int index = 0, size = listRoom.size(); index < size; index++) {
                    RoomInfo roomInfo = listRoom.get(index);
                    Room room = new Room();
                    room.roomID = roomInfo.room_id;
                    if (TextUtils.isEmpty(roomInfo.room_name)) {
                        room.roomName = "娃娃机" + (index + 1);
                    } else {
                        room.roomName = roomInfo.room_name;
                    }
                    room.roomIcon = mListRoomIcon.get((index % 6));
                    room.publishStreamID = "Stream-" + PreferenceUtil.getInstance().getUserID();

                    // 取Zego娃娃机推的流用于播放
                    for (StreamInfo streamInfo : roomInfo.stream_info) {
                        if (streamInfo.stream_id.startsWith("WWJ")) {
                            if (!streamInfo.stream_id.endsWith("_2")){
                                room.streamList.add(0, streamInfo.stream_id);
                            }else {
                                room.streamList.add(streamInfo.stream_id);
                            }
                        }
                    }
                    if (room.roomID.startsWith("WWJ_ZEGO_12345")){
                        mRoomList.add(0, room);
                    }else{
                        mRoomList.add(room);
                    }
                }
                Room roomInfo = new Room();
                roomInfo.roomID = "WWJ_ZEGO_7d643eb105d85b5c";
                roomInfo.roomName="leio";
                roomInfo.streamList = new ArrayList<String>();
                roomInfo.streamList.add("WWJ_ZEGO_STREAM_7d643eb105d85b5c");
                roomInfo.streamList.add("WWJ_ZEGO_STREAM_7d643eb105d85b5c_2");
                mRoomList.add(roomInfo);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mRoomList.size() == 0) {
                                    mTvHint.setVisibility(View.VISIBLE);
                                } else {
                                    mTvHint.setVisibility(View.INVISIBLE);
                                }
                                mRoomListAdapter.setRoomList(mRoomList);
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                });
            }
        });

        // 第一次获取房间信息
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                RoomController.getInstance().getRoomList();
            }
        });
    }
}
