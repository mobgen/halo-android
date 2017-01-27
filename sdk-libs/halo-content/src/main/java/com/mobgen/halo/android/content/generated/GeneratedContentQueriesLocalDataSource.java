package com.mobgen.halo.android.content.generated;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.spec.HaloContentContract;
import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.client.body.HaloBodyFactory;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.storage.database.HaloDataLite;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Create;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;

import org.json.JSONObject;

import java.util.Date;


public class GeneratedContentQueriesLocalDataSource {

    private HaloDataLite mDataLite;


    public GeneratedContentQueriesLocalDataSource(@NonNull HaloFramework haloFramework) {
        mDataLite = haloFramework.storage(HaloContentContract.HALO_CONTENT_STORAGE).db();
    }


    @NonNull
    public Cursor perfomQuery(@NonNull String query,@NonNull Object[] bindArgs) throws HaloNetException, HaloParsingException {
        //convert obects to string to perfom queries
        int length = bindArgs.length;
        String[] bindstringArgs = new String[length-1];
        for(int i=0;i<length-1;i++){
            if(bindArgs[i]==null){
                bindstringArgs[i] = "";
            } else if(bindArgs[i] instanceof String) {
                bindstringArgs[i] = (String)bindArgs[i];
            } else if(bindArgs[i] instanceof Integer) {
                bindstringArgs[i] = bindArgs[i].toString();
            } else if(bindArgs[i] instanceof Date) {
                bindstringArgs[i] = String.valueOf(((Date)bindArgs[i]).getTime());
            }
        }
        Cursor result = mDataLite.getDatabase().rawQuery(query,bindstringArgs);
        if(result!=null)result.moveToFirst();
        return result;
    }

}
