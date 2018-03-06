package com.zego.zegowawaji_server.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zego.zegowawaji_server.R;

import java.util.List;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 26/10/2017.
 */

public class LogListAdapter extends RecyclerView.Adapter<LogListAdapter.MyViewHolder> {
    private List<String> mData;

    private LayoutInflater mLayoutInflater;

    public LogListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public LogListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mLayoutInflater.inflate(R.layout.vt_widget_log_item, parent, false));
    }

    @Override
    public void onBindViewHolder(LogListAdapter.MyViewHolder holder, int position) {
        if (mData != null && position >= 0 && position < mData.size()) {
            holder.tv.setText(mData.get(position));

            holder.tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            holder.tv.requestFocus();
        } else {
            holder.tv.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void setData(List<String> data) {
        mData = data;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv;

        public MyViewHolder(View view) {
            super(view);
            tv = view.findViewById(R.id.vt_text_view);
        }
    }
}
