package com.mobgen.halo.android.content.generated;


import android.support.annotation.NonNull;
import android.util.Log;

import com.mobgen.halo.android.app.generated.GeneratedDatabaseFromModel;
import com.mobgen.halo.android.app.generated.HaloContentQueryApi;
import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.annotations.HaloSearchable;
import com.mobgen.halo.android.content.mock.dummy.DummyItem;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.search.ContentSearchLocalDatasource;
import com.mobgen.halo.android.content.search.ContentSearchRemoteDatasource;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import static com.mobgen.halo.android.content.mock.instrumentation.HaloMock.givenACustomHalo;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloMock.givenADefaultHalo;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloMock.givenASingleThreadedWithParserConfig;
import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SuppressWarnings("unchecked")
public class HaloGeneratedContentTest extends HaloRobolectricTest {

    private Halo mHalo;
    private CallbackFlag mCallbackFlag;
    private Date now;
    private ContentSearchRemoteDatasource mRemoteDatasource;
    private ContentSearchLocalDatasource mLocalDatasource;
    private Parser.Factory mParserFactory;

    @Override
    public void onStart() throws Exception {
        mParserFactory = mock(Parser.Factory.class);
        mRemoteDatasource = mock(ContentSearchRemoteDatasource.class);
        mLocalDatasource = mock(ContentSearchLocalDatasource.class);
        mHalo = givenACustomHalo(givenASingleThreadedWithParserConfig("", mParserFactory));
        HaloContentApi.with(mHalo, null, new GeneratedDatabaseFromModel());
        now = new Date();
        mCallbackFlag = newCallbackFlag();
    }

    @Override
    public void onDestroy() throws Exception {
        mHalo.uninstall();
    }

    @Test
    public void thatGenerateAClassWithEveryQueryAnnotation() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.mobgen.halo.android.app.generated.HaloContentQueryApi");
        assertThat(clazz).isNotNull();
    }

    @Test
    public void thatExistAClassRelatedAsVersionControlToContentTables() throws ClassNotFoundException {
        DummyItem dummyItem = new DummyItem("foo",1,true, new Date());
        HaloSearchable annotation = dummyItem.getClass().getAnnotation(HaloSearchable.class);
        if(annotation!=null){
            Class<?> clazz = Class.forName("com.mobgen.halo.android.app.generated.HaloTable$$ContentVersion");
            assertThat(clazz).isNotNull();
        }
    }

    @Test
    public void thatExistAClassRelatedToTheModelWhenHaveASearchableAnnotation() throws ClassNotFoundException {
        DummyItem dummyItem = new DummyItem("foo",1,true, new Date());
        HaloSearchable annotation = dummyItem.getClass().getAnnotation(HaloSearchable.class);
        if(annotation!=null){
            Class<?> clazz = Class.forName("com.mobgen.halo.android.app.generated.HaloTable$$DummyItem");
            assertThat(clazz).isNotNull();
        }
    }

    @Test
    public void thatExistAMethodRelatedToQueryAnnotation() throws ClassNotFoundException {
        String queryName = "getData";
        Method[] methodNames = HaloContentQueryApi.class.getMethods();
        Boolean existMethod = false;
        for(int j=0;j<methodNames.length;j++) {
            if(methodNames[j].getName().equals(queryName)){
                existMethod = true;
            }
        }
        assertThat(existMethod).isTrue();
    }

    @Test
    public void thatQueryMethodReturnCorrectType() throws ClassNotFoundException {
        String queryName = "getData";
        Method[] methodNames = HaloContentQueryApi.class.getMethods();
        Boolean existMethod = false;
        for(int j=0;j<methodNames.length;j++) {
            if(methodNames[j].getName().equals(queryName)){
                assertThat(methodNames[j].getReturnType().getName()).isEqualTo("com.mobgen.halo.android.content.selectors.HaloContentSelectorFactory");
            }
        }
    }

    @Test
    public void thatQueryMethodHasCorrectNumberOfParams() throws ClassNotFoundException {
        String queryName = "getData";
        Method[] methodNames = HaloContentQueryApi.class.getMethods();
        for(int j=0;j<methodNames.length;j++) {
            if(methodNames[j].getName().equals(queryName)){
                assertThat(methodNames[j].getParameterTypes().length).isEqualTo(1);
            }
        }
    }


    @Test
    public void thatCanUseTheQueryFromCodegen() throws ClassNotFoundException {
        HaloContentQueryApi.with(mHalo)
                .getData("foo")
                .asContent(DummyItem.class)
                .execute(new CallbackV2<List<DummyItem>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<List<DummyItem>> result) {
                      //  assertThat(result.data().size()).isEqualTo(1);
                        mCallbackFlag.flagExecuted();
                    }
                });
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }


    @Test
    public void thatWeCanUseAnotherQuery() throws ClassNotFoundException {
        HaloContentQueryApi.with(mHalo)
                .insertData("foo",1,true, now)
                .asContent()
                .execute(new CallbackV2<Paginated<DummyItem>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<Paginated<DummyItem>> result) {
                        mCallbackFlag.flagExecuted();
                    }
                });
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

}
