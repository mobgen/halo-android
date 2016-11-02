package com.mobgen.halo.android.presenter;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

/**
 * Empty presenter that does nothing but can be useful to test while doing things in the app.
 */
@Keep
public class EmptyPresenter extends AbstractHaloPresenter<HaloViewTranslator> {

    /**
     * Cosntructor of the presenter.
     *
     * @param viewTranslator The view translator created.
     */
    public EmptyPresenter(@NonNull HaloViewTranslator viewTranslator) {
        super(viewTranslator);
    }

    @Override
    public void onInitialized() {
        //Intended to be overriden if needed
    }
}
