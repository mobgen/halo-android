package com.mobgen.halo.android.content.mock.instrumentation;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.mock.dummy.DummyItem;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.search.SearchQueryBuilderFactory;
import com.mobgen.halo.android.framework.network.client.HaloNetClient;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloLocale;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloSegmentationTag;
import com.mobgen.halo.android.testing.CallbackFlag;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class SearchInstruments {

    public static SearchQuery givenTheSimplestQuery() {
        return SearchQuery.builder()
                .moduleIds("sampleId")
                .build();
    }

    public static SearchQuery givenANotPaginatedQuery() {
        return SearchQuery.builder()
                .moduleIds("sampleId")
                .onePage(true)
                .build();
    }

    public static SearchQuery givenAComplexQuery() {
        return SearchQueryBuilderFactory.getExpiredItems("sampleId", "sample")
                .build();
    }

    public static SearchQuery givenASearchLikePatternQuery() {
        return SearchQueryBuilderFactory.getPublishedItemsByName("sampleId", "sample", "searchString")
                .build();
    }

    public static SearchQuery givenTimedCacheQuery(long waitingMillis) {
        return SearchQuery.builder()
                .moduleIds("sampleId")
                .ttl(TimeUnit.MILLISECONDS, waitingMillis)
                .build();
    }

    public static SearchQuery givenAFullQuery() {
        return SearchQuery.builder()
                .moduleIds("module1", "module2")
                .instanceIds("instance1", "instance2")
                .pickFields("field1", "field2")
                .tags(new HaloSegmentationTag("platform", "android"))
                .populateAll()
                .beginSearch()
                .eq("field1", "value1")
                .and()
                .eq("field2", "value2")
                .end()
                .beginMetaSearch()
                .eq("field1", "value1")
                .and()
                .eq("field2", "value2")
                .end()
                .locale(HaloLocale.ENGLISH_BELGIUM)
                .pagination(1, 10)
                .ttl(TimeUnit.HOURS, 1)
                .build();
    }

    public static <T> CallbackV2<T> givenCallbackWithErrorType(final CallbackFlag flag, Class<T> classType, final Class<? extends Exception> errorType) {
        return new CallbackV2<T>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<T> result) {
                flag.flagExecuted();
                assertThat(result.data()).isNull();
                assertThat(result.status().isError()).isTrue();
                assertThat(result.status().exception()).isInstanceOf(errorType);
            }
        };
    }

    public static CallbackV2<Paginated<HaloContentInstance>> givenCallbackContentSuccessData(final CallbackFlag flag, final boolean isFresh) {
        return new CallbackV2<Paginated<HaloContentInstance>>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<Paginated<HaloContentInstance>> result) {
                flag.flagExecuted();
                assertThat(result.data()).isNotNull();
                assertThat(result.data().getLimit()).isEqualTo(result.data().data().size());
                assertThat(result.data().data()).extracting("values").isNotEmpty();
                assertThat(result.status().isError()).isFalse();
                if (isFresh) {
                    assertThat(result.status().isFresh()).isTrue();
                } else {
                    assertThat(result.status().isLocal()).isTrue();
                }
            }
        };
    }

    public static CallbackV2<List<DummyItem>> givenCallbackContentParsedSuccessData(final CallbackFlag flag, final boolean isFresh, final boolean hasError) {
        return new CallbackV2<List<DummyItem>>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<List<DummyItem>> result) {
                flag.flagExecuted();
                assertThat(result.data()).isNotNull();
                assertThat(result.data()).isNotEmpty();
                assertThat(result.data()).hasOnlyElementsOfType(DummyItem.class);
                assertThat(result.data()).extracting("foo").contains("bar").doesNotContainNull();
                if (hasError) {
                    assertThat(result.status().isError()).isTrue();
                } else {
                    assertThat(result.status().isError()).isFalse();
                }
                if (isFresh) {
                    assertThat(result.status().isFresh()).isTrue();
                } else {
                    assertThat(result.status().isLocal()).isTrue();
                }
            }
        };
    }

    public static CallbackV2<List<DummyItem>> givenCallbackContentParsedEmptyDataLocal(final CallbackFlag flag) {
        return new CallbackV2<List<DummyItem>>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<List<DummyItem>> result) {
                flag.flagExecuted();
                assertThat(result.data()).isNotNull();
                assertThat(result.data()).isEmpty();
                assertThat(result.status().isError()).isFalse();
                assertThat(result.status().isLocal()).isTrue();
            }
        };
    }

    public static CallbackV2<Paginated<HaloContentInstance>> givenCallbackThatChecksDataIsInconsistent(final CallbackFlag flag) {
        return new CallbackV2<Paginated<HaloContentInstance>>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<Paginated<HaloContentInstance>> result) {
                flag.flagExecuted();
                assertThat(result.data()).isNull();
                assertThat(result.status().isError()).isTrue();
                assertThat(result.status().isInconsistent()).isTrue();
            }
        };
    }

    public static OkHttpClient.Builder givenAOkClientWithCustomInterceptor(HaloNetClient netClient, final String cacheTime) {
        return netClient.ok().newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Headers headers = request.headers();
                        if (headers.size() > 0) {
                            assertThat(headers.get("to-cache")).isEqualTo(cacheTime);
                        }
                        return chain.proceed(request);
                    }
                });
    }

    public static SearchQuery givenASearchWithServerCache() {
        return SearchQueryBuilderFactory.getPublishedItemsByName("sampleId", "sample", "searchString")
                .serverCache(212)
                .build();
    }

    public static SearchQuery givenASearchWithoutServerCache() {
        return SearchQueryBuilderFactory.getPublishedItemsByName("sampleId", "sample", "searchString")
                .build();
    }
}
