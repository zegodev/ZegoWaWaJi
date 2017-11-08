package com.zego.wawaji_client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zego.wawaji.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Room> mRoomList = null;
    private RecyclerView mRecyclerView = null;

    static final private String ROOM1_ID = "WWJ_ZEGO_12345_54321";
    static final private String ROOM2_ID = "WWJ_ZEGO_12345_54322";
    static final private String ROOM3_ID = "WWJ_ZEGO_12345_54323";

    static final private String ROOM1_STREAM1_ID = "WWJ_ZEGO_STREAM_12345_54321";
    static final private String ROOM1_STREAM2_ID = "WWJ_ZEGO_STREAM_12345_54321_2";

    static final private String ROOM2_STREAM1_ID = "WWJ_ZEGO_STREAM_12345_54322";
    static final private String ROOM2_STREAM2_ID = "WWJ_ZEGO_STREAM_12345_54322_2";

    static final private String ROOM3_STREAM1_ID = "WWJ_ZEGO_STREAM_12345_54323";
    static final private String ROOM3_STREAM2_ID = "WWJ_ZEGO_STREAM_12345_54323_2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("即构抓娃娃");
        setContentView(R.layout.activity_main);

        initRoomInfo();
        initViews();
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        RoomListAdapter adapter = new RoomListAdapter(this, mRoomList);
        mRecyclerView.setAdapter(adapter);

        adapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                PlayActivity.actionStart(MainActivity.this, mRoomList.get(position));
            }
        });
    }

    private void initRoomInfo()
    {
        if (mRoomList == null)
        {
            mRoomList = new ArrayList<>();

            Room room1 = new Room();
            room1.roomName = "娃娃机1";
            room1.roomID = ROOM1_ID;
            room1.streamList.add(ROOM1_STREAM1_ID);
            room1.streamList.add(ROOM1_STREAM2_ID);
            room1.publishStreamID = "zego_wawaji_room1_publish-" + PreferenceUtil.getInstance().getUserID();
            mRoomList.add(room1);

            Room room2 = new Room();
            room2.roomName = "娃娃机2";
            room2.roomID = ROOM2_ID;
            room2.streamList.add(ROOM2_STREAM1_ID);
            room2.streamList.add(ROOM2_STREAM2_ID);
            room2.publishStreamID = "zego_wawaji_room2_publish-" + PreferenceUtil.getInstance().getUserID();
            mRoomList.add(room2);

            Room room3 = new Room();
            room3.roomName = "娃娃机3";
            room3.roomID = ROOM3_ID;
            room3.streamList.add(ROOM3_STREAM1_ID);
            room3.streamList.add(ROOM3_STREAM2_ID);
            room3.publishStreamID = "zego_wawaji_room3_publish-" + PreferenceUtil.getInstance().getUserID();
            mRoomList.add(room3);
        }
    }
}
