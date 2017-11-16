package com.zego.wawaji_client;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zego.wawaji.R;

import java.util.List;

/**
 * Copyright © 2017 Zego. All rights reserved.
 */

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.ViewHolder> {

    private LayoutInflater mLayoutInflater;

    private List<Room> mRoomList;

    private OnItemClickListener mItemClickListener;

    public RoomListAdapter(Context context, List<Room> roomList) {
        mLayoutInflater = LayoutInflater.from(context);
        mRoomList = roomList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_room_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvRoomName.setText(mRoomList.get(position).roomName);
    }

    @Override
    public int getItemCount() {
        return mRoomList == null ? 0 : mRoomList.size();
    }

    public void setItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvRoomName;

        public ViewHolder(final View itemView) {
            super(itemView);

            tvRoomName = itemView.findViewById(R.id.tv_room_name);
            itemView.findViewById(R.id.cv_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onClick(itemView, getAdapterPosition());
                    }
                }
            });
        }
    }
}
