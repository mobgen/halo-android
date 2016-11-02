package com.mobgen.halo.android.sdk.core.internal.parser;

import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.sdk.mock.dummy.DummyDateItem;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class LoganSquareParserTest extends HaloRobolectricTest {

    private Parser.Factory mFactory;

    @Override
    public void onStart() throws Exception {
        super.onStart();
        mFactory = LoganSquareParserFactory.create();
    }

    @Test
    public void thatParsingANonNullDateReturnsThatDate() throws IOException {
        String parsingString = "{\"date\": 1476944784790}";
        DummyDateItem dateItem = (DummyDateItem) mFactory.deserialize(DummyDateItem.class).convert(new ByteArrayInputStream(parsingString.getBytes("UTF-8")));
        assertThat(dateItem.getDate()).isNotNull();
        assertThat(dateItem.getDate().getTime()).isEqualTo(1476944784790L);
    }

    @Test
    public void thatParsingANullDateReturnsNull() throws IOException {
        String parsingString = "{\"date\": null}";
        DummyDateItem dateItem = (DummyDateItem) mFactory.deserialize(DummyDateItem.class).convert(new ByteArrayInputStream(parsingString.getBytes("UTF-8")));
        assertThat(dateItem.getDate()).isNull();
    }
}
