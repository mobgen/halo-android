package com.mobgen.halo.android.app.ui.chat.messages;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.DataStatusRecyclerAdapter;

public class MessagesViewHolder extends DataStatusRecyclerAdapter.StatusViewHolder {

    public TextView mDate;
    public TextView mName;
    public TextView mMessage;
    public LinearLayout mParentLayout;
    public LinearLayout mContainerLayout;

    public MessagesViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
        mParentLayout = (LinearLayout) itemView.findViewById(R.id.ll_parent);
        mContainerLayout = (LinearLayout) itemView.findViewById(R.id.ll_container);
        mDate = (TextView) itemView.findViewById(R.id.tv_date);
        mMessage = (TextView) itemView.findViewById(R.id.tv_title);
        mName = (TextView) itemView.findViewById(R.id.tv_name);
    }


    public TextView getMessage() {
        return mMessage;
    }

    public TextView getDate(){ return mDate;}

    public TextView getName(){ return mName;}
}