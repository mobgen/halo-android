package com.mobgen.halo.android.app.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.utils.StatusInterceptor;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;

/**
 * Recycler view adapter helper that contains the status of the data. It will always be displayed in the top
 * of the adapter.
 */
public abstract class DataStatusRecyclerAdapter<T extends DataStatusRecyclerAdapter.StatusViewHolder> extends RecyclerView.Adapter<T> {

    private static final int STATUS_VIEW_TYPE = 10;

    private HaloStatus mStatus;
    private Context mContext;

    public DataStatusRecyclerAdapter(Context context) {
        mContext = context;
    }

    public void setStatus(HaloStatus status) {
        mStatus = status;
    }

    public HaloStatus getStatus() {
        return mStatus;
    }

    public boolean hasStatus() {
        return mStatus != null && !mStatus.isFresh();
    }

    public Context getContext() {
        return mContext;
    }

    public boolean isStatusPosition(int position) {
        return getItemViewType(position) == STATUS_VIEW_TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final T onCreateViewHolder(ViewGroup parent, int viewType) {
        T viewHolder;
        if (viewType == STATUS_VIEW_TYPE) {
            viewHolder = (T) new StatusViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_data_status, parent, false), viewType);
        } else {
            viewHolder = onCreateOverViewholder(parent, viewType);
        }
        return viewHolder;
    }

    @Override
    public final void onBindViewHolder(T holder, int position) {
        if (isStatusPosition(position)) {
            StatusInterceptor.intercept(mStatus, holder);
        } else {
            onBindOverViewHolder(holder, position - (hasStatus() ? 1 : 0));
        }
    }

    @Override
    public final int getItemViewType(int position) {
        return position == 0 && hasStatus() ? STATUS_VIEW_TYPE : getOverItemViewType(position - (hasStatus() ? 1 : 0));
    }

    public abstract T onCreateOverViewholder(ViewGroup parent, int viewType);

    public abstract void onBindOverViewHolder(T holder, int position);

    public int getOverItemViewType(int position) {
        return 0;
    }

    public abstract int getOverItemCount();

    @Override
    public final int getItemCount() {
        return getOverItemCount() + (hasStatus() ? 1 : 0);
    }

    public static class StatusViewHolder extends RecyclerView.ViewHolder {

        private TextView mStatus;
        private View mBackground;

        public StatusViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == STATUS_VIEW_TYPE) {
                mBackground = itemView;
                mStatus = (TextView) itemView.findViewById(R.id.tv_data_status);
            }
        }

        public TextView getStatusText() {
            return mStatus;
        }

        public View getBackground() {
            return mBackground;
        }
    }

}
