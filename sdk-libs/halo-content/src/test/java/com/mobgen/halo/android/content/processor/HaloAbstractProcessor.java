package com.mobgen.halo.android.content.processor;

import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.search.ContentSearchLocalDatasource;
import com.mobgen.halo.android.content.search.ContentSearchRemoteDatasource;
import com.mobgen.halo.android.content.search.ContentSearchRepository;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetParseException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;

import static com.mobgen.halo.android.content.mock.instrumentation.HaloMock.givenACustomHalo;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloMock.givenASingleThreadedWithParserConfig;
import static com.mobgen.halo.android.content.mock.instrumentation.SearchInstruments.givenCallbackThatChecksDataIsInconsistent;
import static com.mobgen.halo.android.content.mock.instrumentation.SearchInstruments.givenTheSimplestQuery;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class HaloAbstractProcessor extends HaloRobolectricTest {


    @Override
    public void onStart() throws Exception {

    }

    @Override
    public void onDestroy() throws Exception {

    }

    @Test
    public void thatGenerateAClassWithEveryQueryAnnotation() throws HaloNetException {
        File source = new File("com.mobgen.halo.android.app.generated.HaloContentQueryApi");

        assertThat(source.exists()).isTrue();
    }


}
