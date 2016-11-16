package com.mobgen.halo.android.testing;


import android.database.Cursor;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Creates a cursor with some mocked data.
 */
public class MockCursor {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Cursor mMockedCursor;

        private Builder() {
            mMockedCursor = mock(Cursor.class);
            doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                    if (mMockedCursor.isClosed()) {
                        throw new RuntimeException("Illegal cursor state");
                    }
                    when(mMockedCursor.isClosed()).thenReturn(true);
                    return null;
                }
            }).when(mMockedCursor).close();
        }

        public Builder on(String columnName, int value) {
            int hash = columnName.hashCode();
            when(mMockedCursor.getColumnIndex(columnName)).thenReturn(hash);
            when(mMockedCursor.getInt(hash)).thenReturn(value);
            return this;
        }

        public Builder on(String columnName, String value) {
            int hash = columnName.hashCode();
            when(mMockedCursor.getColumnIndex(columnName)).thenReturn(hash);
            when(mMockedCursor.getString(hash)).thenReturn(value);
            return this;
        }

        public Builder on(String columnName, Date value) {
            int hash = columnName.hashCode();
            when(mMockedCursor.getColumnIndex(columnName)).thenReturn(hash);
            when(mMockedCursor.getLong(hash)).thenReturn(value.getTime());
            return this;
        }

        public Builder on(String columnName, boolean value) {
            int hash = columnName.hashCode();
            when(mMockedCursor.getColumnIndex(columnName)).thenReturn(hash);
            when(mMockedCursor.getLong(hash)).thenReturn(value ? 1L : 0);
            return this;
        }

        public Builder on(String columnName, byte[] value) {
            int hash = columnName.hashCode();
            when(mMockedCursor.getColumnIndex(columnName)).thenReturn(hash);
            when(mMockedCursor.getBlob(hash)).thenReturn(value);
            return this;
        }

        public Builder on(String columnName, double value) {
            int hash = columnName.hashCode();
            when(mMockedCursor.getColumnIndex(columnName)).thenReturn(hash);
            when(mMockedCursor.getDouble(hash)).thenReturn(value);
            return this;
        }

        public Builder on(String columnName, float value) {
            int hash = columnName.hashCode();
            when(mMockedCursor.getColumnIndex(columnName)).thenReturn(hash);
            when(mMockedCursor.getFloat(hash)).thenReturn(value);
            return this;
        }

        public Builder on(String columnName, long value) {
            int hash = columnName.hashCode();
            when(mMockedCursor.getColumnIndex(columnName)).thenReturn(hash);
            when(mMockedCursor.getLong(hash)).thenReturn(value);
            return this;
        }

        public Builder on(String columnName, short value) {
            int hash = columnName.hashCode();
            when(mMockedCursor.getColumnIndex(columnName)).thenReturn(hash);
            when(mMockedCursor.getShort(hash)).thenReturn(value);
            return this;
        }

        public Builder count(int count) {
            when(mMockedCursor.getCount()).thenReturn(count);
            return this;
        }

        public Builder replicate(final int times) {
            if (times > 0) {
                when(mMockedCursor.moveToFirst()).thenReturn(true);
                when(mMockedCursor.moveToLast()).thenReturn(true);
            }
            count(times);
            when(mMockedCursor.moveToNext()).then(new Answer<Boolean>() {
                int mTimes = times;

                @Override
                public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                    return --mTimes != 0;
                }
            });
            return this;
        }

        public Builder isNull(String columnName, boolean isNull) {
            int hash = columnName.hashCode();
            when(mMockedCursor.getColumnIndex(columnName)).thenReturn(hash);
            when(mMockedCursor.isNull(hash)).thenReturn(isNull);
            return this;
        }

        public Cursor build() {
            return mMockedCursor;
        }
    }
}
