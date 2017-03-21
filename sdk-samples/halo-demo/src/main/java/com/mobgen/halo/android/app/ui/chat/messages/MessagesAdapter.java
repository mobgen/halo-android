package com.mobgen.halo.android.app.ui.chat.messages;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.chat.ChatMessage;
import com.mobgen.halo.android.app.ui.DataStatusRecyclerAdapter;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends DataStatusRecyclerAdapter<MessagesViewHolder> {

    private Context mContext;
    private MessagesCallback mCallback;
    private List<ChatMessage> mChatMessage;
    private boolean mIsMultiple;

    public interface MessagesCallback {
        void onMessageSelected(ChatMessage chatMessage, MessagesViewHolder viewHolder);
    }

    public MessagesAdapter(Context context, MessagesCallback callback, boolean isMultiple) {
        super(context);
        mContext = context;
        mCallback = callback;
        mIsMultiple = isMultiple;
    }

    public void setChatMessage(HaloResultV2<List<ChatMessage>> chatMessages) {
        mChatMessage = chatMessages.data();
        setStatus(chatMessages.status());
    }

    public void addNewMessage(ChatMessage chatMessages) {
        mChatMessage.add(chatMessages);
    }

    @Override
    public MessagesViewHolder onCreateOverViewholder(ViewGroup parent, int viewType) {
        return new MessagesViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_chat_message, parent, false), viewType);
    }

    @Override
    public void onBindOverViewHolder(final MessagesViewHolder holder, int position) {
        final ChatMessage chatMessage = mChatMessage.get(position);
        // if message is mine then align to right
        if (chatMessage.getIsFromSender()) {
            holder.mContainerLayout.setBackgroundResource(R.drawable.in_msg);
            holder.mParentLayout.setGravity(Gravity.LEFT);
        } else {
            holder.mContainerLayout.setBackgroundResource(R.drawable.out_msg);
            holder.mParentLayout.setGravity(Gravity.RIGHT);
        }
        if(mIsMultiple){
            holder.mName.setVisibility(View.VISIBLE);
            holder.mName.setText(chatMessage.getUserName());
        }
        holder.mDate.setText(getMinifyDate(chatMessage.getCreationDate()));
        holder.mMessage.setText(chatMessage.getMessage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onMessageSelected(chatMessage, holder);
                }
            }
        });
    }

    @Override
    public int getOverItemCount() {
        if (mChatMessage == null) {
            return 0;
        }
        return mChatMessage.size();
    }

    private String getMinifyDate(Date date){
        Calendar msgTime = Calendar.getInstance();
        msgTime.setTimeInMillis(date.getTime());
        Calendar now = Calendar.getInstance();
        String timeFormatString = "h:mm aa";
        String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";

        if (now.get(Calendar.DATE) == msgTime.get(Calendar.DATE) ) {
            return mContext.getString(R.string.chat_today) + " " + DateFormat.format(timeFormatString, msgTime);
        } else if (now.get(Calendar.DATE) - msgTime.get(Calendar.DATE) == 1  ){
            return mContext.getString(R.string.chat_yesterday) + " " + DateFormat.format(timeFormatString, msgTime);
        } else if (now.get(Calendar.YEAR) == msgTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, msgTime).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy, h:mm aa", msgTime).toString();
        }
    }


}
