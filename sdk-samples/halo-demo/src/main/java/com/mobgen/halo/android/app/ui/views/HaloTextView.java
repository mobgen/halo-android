package com.mobgen.halo.android.app.ui.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Text view that allows the use of the font lab medium inside the app.
 */
public class HaloTextView extends TextView {

    /**
     * The typeface of the font cached to avoid parsing it again and again.
     */
    private static Typeface mTypeface;

    /**
     * Constructor of the view.
     *
     * @param context The context.
     */
    public HaloTextView(final Context context) {
        this(context, null);
    }

    /**
     * Constructor of the view via xml.
     *
     * @param context The context./
     * @param attrs   The attributes.
     */
    public HaloTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor of the view via xml with style.
     *
     * @param context  The context.
     * @param attrs    The attributes.
     * @param defStyle The style of the view.
     */
    public HaloTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/Lab-Medium.ttf");
        }
        setTypeface(mTypeface);
    }
}