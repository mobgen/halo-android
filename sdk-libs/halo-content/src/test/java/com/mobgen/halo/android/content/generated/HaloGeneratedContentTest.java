package com.mobgen.halo.android.content.generated;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.app.generated.GeneratedDatabaseFromModel;
import com.mobgen.halo.android.app.generated.HaloContentQueryApi;
import com.mobgen.halo.android.app.generated.HaloTable$$DummyItem;
import com.mobgen.halo.android.app.generated.HaloTable$$DummyObject;
import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.annotations.HaloSearchable;
import com.mobgen.halo.android.content.mock.dummy.DummyItem;
import com.mobgen.halo.android.content.mock.dummy.DummyObject;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.spec.HaloContentContract;
import com.mobgen.halo.android.content.utils.HaloContentHelper;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Create;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageParseException;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockCursor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import static com.mobgen.halo.android.content.mock.instrumentation.HaloMock.givenACustomHalo;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloMock.givenASingleThreadedWithParserConfig;
import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(RobolectricTestRunner.class)
public class HaloGeneratedContentTest extends HaloRobolectricTest {

    private Halo mHalo;
    private CallbackFlag mCallbackFlag;
    private Date now;
    private HaloContentApi contentApi;
    private Parser.Factory mParserFactory;
    private SQLiteDatabase database;

    @Before
    public void setUp() throws Exception {
        mParserFactory = mock(Parser.Factory.class);
        mHalo = givenACustomHalo(givenASingleThreadedWithParserConfig("", mParserFactory));
        contentApi = HaloContentApi.with(mHalo, null, new GeneratedDatabaseFromModel());
        database = mHalo.framework().storage(HaloContentContract.HALO_CONTENT_STORAGE).db().getDatabase();
        database.isOpen();
        now = new Date();
        mCallbackFlag = newCallbackFlag();
    }

    @After
    public void tearDown() throws Exception {
        mHalo.framework().storage(HaloContentContract.HALO_CONTENT_STORAGE).db().getDatabase().close();
        mHalo.uninstall();
        mHalo = null;
    }

    @Test
    public void thatGenerateAClassWithEveryQueryAnnotation() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.mobgen.halo.android.app.generated.HaloContentQueryApi");
        assertThat(clazz).isNotNull();
    }

    @Test
    public void thatExistAClassRelatedAsVersionControlToContentTables() throws ClassNotFoundException {
        DummyItem dummyItem = new DummyItem("foo",1,true, new Date(), new DummyObject("field"));
        HaloSearchable annotation = dummyItem.getClass().getAnnotation(HaloSearchable.class);
        if(annotation!=null){
            Class<?> clazz = Class.forName("com.mobgen.halo.android.app.generated.HaloTable$$ContentVersion");
            assertThat(clazz).isNotNull();
        }
    }

    @Test
    public void thatExistAClassRelatedToTheModelWhenHaveASearchableAnnotation() throws ClassNotFoundException {
        DummyItem dummyItem = new DummyItem("foo",1,true, new Date(), new DummyObject("field"));
        HaloSearchable annotation = dummyItem.getClass().getAnnotation(HaloSearchable.class);
        if(annotation!=null){
            Class<?> clazz = Class.forName("com.mobgen.halo.android.app.generated.HaloTable$$DummyItem");
            assertThat(clazz).isNotNull();
        }
    }

    @Test
    public void thatHaloTableCreationIsCorrect() throws ClassNotFoundException {
        int numberOfHaloTableFields= HaloTable$$DummyItem.class.getDeclaredFields().length;
        assertThat(numberOfHaloTableFields).isEqualTo(6);
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
    public void thatCanExecuteTheSelectQueryFromCodegen() throws ClassNotFoundException {
        HaloContentQueryApi.with(mHalo)
                .getData("foo")
                .asContent(DummyItem.class)
                .execute(new CallbackV2<List<DummyItem>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<List<DummyItem>> result) {
                        mCallbackFlag.flagExecuted();
                    }
                });
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }


    @Test
    public void thatWeCanExecuteTheInsertQueryFromCodegen() throws ClassNotFoundException {
        HaloContentQueryApi.with(mHalo)
                .insertData("foo",1,true, now, new DummyObject("field"))
                .asContent()
                .execute(new CallbackV2<Paginated<DummyItem>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<Paginated<DummyItem>> result) {
                        mCallbackFlag.flagExecuted();
                    }
                });
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanConvertCursorToAnyModelAsList() throws HaloStorageParseException {
        Cursor cursor = initMockCursors();
        List<DummyObject> elements = HaloContentHelper.createList(cursor,DummyObject.class);
        assertThat(elements.size()).isEqualTo(1);
        assertThat(elements.get(0).field).isEqualTo("field");
    }

    private Cursor initMockCursors() {
        String FAKE_COLUMN_NAME ="field";
        String FAKE_STRING = "field";

        Cursor cursor = mock(Cursor.class);
        // Set non empty cursor.
        when(cursor.moveToFirst()).thenReturn(true);
        when(cursor.getColumnNames()).thenReturn(new String[]{FAKE_COLUMN_NAME,"GC_ID"});
        //zero index
        when(cursor.getColumnName(0)).thenReturn(FAKE_COLUMN_NAME);
        when(cursor.getType(0)).thenReturn(Cursor.FIELD_TYPE_STRING);
        when(cursor.getColumnIndex(FAKE_COLUMN_NAME))
                .thenReturn(0);
        when(cursor.getString(0))
                .thenReturn(FAKE_STRING);
        //first index
        when(cursor.getColumnName(1)).thenReturn("GC_ID");
        when(cursor.getType(1)).thenReturn(Cursor.FIELD_TYPE_INTEGER);
        when(cursor.getColumnIndex("GC_ID"))
                .thenReturn(1);
        when(cursor.getInt(1))
                .thenReturn(10);

        return cursor;
    }

}
