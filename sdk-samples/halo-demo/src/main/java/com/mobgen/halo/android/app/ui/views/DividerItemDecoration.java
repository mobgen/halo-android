package com.mobgen.halo.android.app.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.framework.common.utils.HaloUtils;

/**
 * Divider view for recycler views.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * The divider for drawables.
     */
    private Drawable mDivider;

    /**
     * Determines if the divider should be shown in the last position.
     */
    private boolean mDrawLastDivider;

    /**
     * Constructor for the divider decorator.
     *
     * @param context The context for this decorator.
     */
    public DividerItemDecoration(Context context) {
        this(context, false);
    }

    /**
     * Constructor for the divider decorator.
     *
     * @param context         The context.
     * @param drawLastDivider Determines if the divider should be shown for the last item.
     */
    public DividerItemDecoration(Context context, boolean drawLastDivider) {
        mDivider = HaloUtils.getDrawable(context, R.drawable.recycler_line_divider);
        mDrawLastDivider = drawLastDivider;
    }

    /**
     * Sets the drawable view for the divider.
     *
     * @param drawable The drawable view.
     */
    public void setDividerDrawable(Drawable drawable) {
        mDivider = drawable;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = mDrawLastDivider ? parent.getChildCount() : parent.getChildCount() - 1;

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            if (child.getMeasuredHeight() > 0) {
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}