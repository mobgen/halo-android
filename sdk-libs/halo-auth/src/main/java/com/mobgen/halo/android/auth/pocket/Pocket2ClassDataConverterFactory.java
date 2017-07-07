package com.mobgen.halo.android.auth.pocket;

import android.support.annotation.NonNull;

/**
 * Created by f.souto.gonzalez on 21/06/2017.
 */
/**
 * @hide Factory that provides generic model parsed as a model type.
 */
public class Pocket2ClassDataConverterFactory implements SelectorPocket2DataClass.Factory {

    @NonNull
    @Override
    public <T> SelectorPocket2DataClass<T> createResultData(@NonNull PocketDataProvider dataProvider, @NonNull Class<T> clazz) {
        return new SelectorPocket2DataClass<T>(
                dataProvider,
                new Pocket2DataClassConverter<T>(clazz));
    }

    @NonNull
    @Override
    public SelectorPocket2ReferenceContainer createResultReference(@NonNull PocketDataProvider dataProvider) {
        return new SelectorPocket2ReferenceContainer(
                dataProvider,
                new Pocket2ReferenceContainerConverter());
    }
}
