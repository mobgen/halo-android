package com.mobgen.halo.android.framework.mock.instrumentation;

import android.content.Context;

import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.mock.parser.ParserFactoryInstrument;
import com.mobgen.halo.android.framework.network.client.HaloNetClient;
import com.mobgen.halo.android.framework.network.client.body.HaloBodyFactory;
import com.mobgen.halo.android.framework.network.client.body.HaloMediaType;
import com.mobgen.halo.android.framework.network.client.endpoint.HaloEndpoint;
import com.mobgen.halo.android.framework.network.client.endpoint.HaloEndpointCluster;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.robolectric.RuntimeEnvironment;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import okhttp3.OkHttpClient;

import static com.mobgen.halo.android.framework.mock.fixtures.ServerFixtures.GET_TEST_ITEM;
import static org.mockito.Mockito.mock;

public class HaloNetInstrument {

    public static HaloNetClient givenNetClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder().build().newBuilder();
        HaloEndpoint endpoint = new HaloEndpoint("1","HaloNetClientTest","sha256/1234");
        HaloEndpointCluster endpointCluster = new HaloEndpointCluster(endpoint);
        endpointCluster.buildCertificatePinner();
        endpointCluster.registerEndpoint(endpointCluster.getEndpoint("1"));

        return new HaloNetClient(RuntimeEnvironment.application, builder, endpointCluster);
    }

    public static HaloNetClient givenNetClientWithoutPinning(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder().build().newBuilder();
        HaloEndpoint endpoint = new HaloEndpoint("1","HaloNetClientTest");
        HaloEndpointCluster endpointCluster = new HaloEndpointCluster(endpoint);
        endpointCluster.registerEndpoint(endpointCluster.getEndpoint("1"));

        return new HaloNetClient(RuntimeEnvironment.application, builder, endpointCluster);
    }

    public static HaloRequest givenAGetRequestWithParams(HaloNetworkApi networkApi){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("myParamName","myParamValue");
        return HaloRequest.builder(networkApi)
                .url("1","getSampleData",params)
                .method(HaloRequestMethod.GET)
                .tag("myCustomTag")
                .header("myHeader","myHeaderValue")
                .build();
    }

    public static HaloRequest givenAGetRequestWithParamsAndSession(HaloNetworkApi networkApi){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("myParamName","myParamValue");
        HaloSessionInstrument haloSessionInstrument = new HaloSessionInstrument();
        return HaloRequest.builder(networkApi)
                .url("1","getSampleData",params,null)
                .tag("myCustomTag")
                .header("myHeader","myHeaderValue")
                .session(haloSessionInstrument)
                .build();
    }

    public static HaloRequest givenAGetRequest(HaloNetworkApi networkApi){
        return HaloRequest.builder(networkApi)
                .url("1","getSampleData")
                .method(HaloRequestMethod.GET)
                .build();
    }

    public static HaloRequest givenAGetRequestTyped(HaloNetworkApi networkApi) {
        return HaloRequest.builder(networkApi)
                .url("1","getSampleDataTyped")
                .method(HaloRequestMethod.GET)
                .responseParser(ParserFactoryInstrument.create())
                .build();
    }

    public static HaloRequest givenAPostRequestWithFormBody(HaloNetworkApi networkApi) {
        return HaloRequest.builder(networkApi)
                .url("1","sendFormBody")
                .method(HaloRequestMethod.POST)
                .responseParser(ParserFactoryInstrument.create())
                .body(HaloBodyFactory.formBody().build())
                .build();
    }

    public static HaloRequest givenAPostRequestWithFileBody(HaloNetworkApi networkApi,Context mContext) throws IOException {
        URL pathURL = mContext.getClass().getClassLoader().getResource(GET_TEST_ITEM);
        String pathFile =  pathURL.toString();
        pathFile = pathFile.replace("file:","");
        File fileToUpload = new File(pathFile);

        return HaloRequest.builder(networkApi)
                .url("1","sendFileBody")
                .method(HaloRequestMethod.POST)
                .responseParser(ParserFactoryInstrument.create())
                .body(HaloBodyFactory.fileBody(HaloMediaType.MULTIPART_MIXED,fileToUpload))
                .build();
    }

    public static HaloRequest givenAPostRequestWithStringBody(HaloNetworkApi networkApi) {
        return HaloRequest.builder(networkApi)
                .url("1","sendStringBody")
                .method(HaloRequestMethod.POST)
                .responseParser(ParserFactoryInstrument.create())
                .body(HaloBodyFactory.stringBody(HaloMediaType.TEXT_PLAIN,"StringBody"))
                .build();
    }

    public static HaloRequest givenAPostRequestWithJSONObjectBody(HaloNetworkApi networkApi) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("nameData","valueData");
        return HaloRequest.builder(networkApi)
                .url("1","sendJSONBody")
                .method(HaloRequestMethod.POST)
                .responseParser(ParserFactoryInstrument.create())
                .body(HaloBodyFactory.jsonObjectBody(json))
                .build();
    }

    public static HaloRequest givenAPostRequestWithJSONArrayBody(HaloNetworkApi networkApi) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("nameData","valueData");
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(json);
        return HaloRequest.builder(networkApi)
                .url("1","sendJArrayBody")
                .method(HaloRequestMethod.POST)
                .responseParser(ParserFactoryInstrument.create())
                .body(HaloBodyFactory.jsonObjectBody(jsonArray))
                .build();
    }

    public static HaloRequest givenAGetRequestWithParamsAndSessionAndBody(HaloNetworkApi networkApi) throws JSONException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("myParamName","myParamValue");
        HaloSessionInstrument haloSessionInstrument = new HaloSessionInstrument();
        JSONObject json = new JSONObject();
        json.put("nameData","valueData");
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(json);
        return HaloRequest.builder(networkApi)
                .url("1","getSampleData",params,null)
                .method(HaloRequestMethod.POST)
                .tag("myCustomTag")
                .header("myHeader","myHeaderValue")
                .responseParser(ParserFactoryInstrument.create())
                .body(HaloBodyFactory.jsonObjectBody(jsonArray))
                .session(haloSessionInstrument)
                .build();
    }
}
