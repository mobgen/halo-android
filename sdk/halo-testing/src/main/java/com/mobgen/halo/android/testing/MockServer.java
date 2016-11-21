package com.mobgen.halo.android.testing;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.BufferedSource;
import okio.Okio;

public class MockServer {
    private MockWebServer mMockWebServer;

    private MockServer(){
        mMockWebServer = new MockWebServer();
    }

    public static MockServer create(){
        return new MockServer();
    }

    public MockServer enqueue(int code){
        MockResponse response = new MockResponse().setResponseCode(code);
        enqueue(response);
        return this;
    }

    public MockServer enqueue(int code, String body){
        MockResponse response = new MockResponse()
                .setResponseCode(code)
                .setBody(body);
        enqueue(response);
        return this;
    }

    public MockServer enqueueFile(String fileName) throws IOException {
        enqueueFile(200, fileName);
        return this;
    }

    public MockServer enqueueFile(int code, String fileName) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
        BufferedSource buffer = Okio.buffer(Okio.source(in));
        enqueue(200, buffer.readUtf8());
        return this;
    }

    public MockServer enqueue(MockResponse response){
        mMockWebServer.enqueue(response);
        return this;
    }

    public String start() throws IOException {
        mMockWebServer.start();
        String url = mMockWebServer.url("/").toString();
        return url.substring(0, url.length() - 1);
    }


    public void shutdown() throws IOException {
        mMockWebServer.shutdown();
    }
}
