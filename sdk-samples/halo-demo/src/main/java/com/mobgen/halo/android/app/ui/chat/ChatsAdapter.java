package com.mobgen.halo.android.app.ui.chat;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.chat.QRContact;
import com.mobgen.halo.android.app.ui.DataStatusRecyclerAdapter;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class ChatsAdapter extends DataStatusRecyclerAdapter<ChatRoomViewHolder> {

    private Context mContext;
    private ChatRoomsCallback mCallback;
    private List<QRContact> mChatRooms;

    public interface ChatRoomsCallback {
        void onChatRoomTap(QRContact qrContact, ChatRoomViewHolder viewHolder, Boolean isFromImage);
        void onChatRoomSelected(QRContact qrContact);
    }

    public ChatsAdapter(Context context, ChatRoomsCallback callback) {
        super(context);
        mContext = context;
        mCallback = callback;
    }

    public void deteleItem(QRContact qrContact) {
        if(mChatRooms.contains(qrContact)){
            mChatRooms.remove(mChatRooms.indexOf(qrContact));
            //delete multiple channel you must scan new contacts
            if(mChatRooms.size()==1){
                mChatRooms.remove(0);
            }
            notifyDataSetChanged();
        }
    }

    public void setQRContact(HaloResultV2<List<QRContact>> qrContacts) {
        mChatRooms = qrContacts.data();
        setStatus(qrContacts.status());
    }

    @Override
    public ChatRoomViewHolder onCreateOverViewholder(ViewGroup parent, int viewType) {
        return new ChatRoomViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_chat_room, parent, false), viewType);
    }

    @Override
    public void onBindOverViewHolder(final ChatRoomViewHolder holder, int position) {
        final QRContact qrContact = mChatRooms.get(position);
        holder.mUserName.setText(qrContact.getName());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mCallback != null) {
                    mCallback.onChatRoomSelected(qrContact);
                }
                return true;
            }
        });

        if (!TextUtils.isEmpty(qrContact.getImage())) {
            Picasso.with(mContext).load(new File(qrContact.getImage())).into(holder.mThumbnail);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onChatRoomTap(qrContact, holder,false);
                }
            }
        });

        holder.mThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onChatRoomTap(qrContact, holder,true);
                }
            }
        });
    }

    @Override
    public int getOverItemCount() {
        if (mChatRooms == null) {
            return 0;
        }
        return mChatRooms.size();
    }


}
