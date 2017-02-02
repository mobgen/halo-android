package com.mobgen.halo.android.content.generated;

import android.database.Cursor;
import android.database.SQLException;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.spec.HaloContentContract;
import com.mobgen.halo.android.content.utils.HaloContentHelper;
import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.client.body.HaloBodyFactory;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.storage.database.HaloDataLite;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Create;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageParseException;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;


public class GeneratedContentQueriesLocalDataSource {

    private HaloDataLite mDataLite;


    public GeneratedContentQueriesLocalDataSource(@NonNull HaloFramework haloFramework) {
        mDataLite = haloFramework.storage(HaloContentContract.HALO_CONTENT_STORAGE).db();
    }

    //TODO Convert HaloContentInstance to model

    @NonNull
    public List<HaloContentInstance> perfomQuery(@NonNull String query,@NonNull Object[] bindArgs) throws SQLException {
        //convert obects to string to perfom queries
        int length = bindArgs.length;
        String[] bindstringArgs = new String[length];
        for(int i=0;i<length;i++){
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
        Cursor rawResult = mDataLite.getDatabase().rawQuery(query,bindstringArgs);
        List<HaloContentInstance> result = null;
        try {
            if(rawResult!=null) {
                result = HaloContentHelper.createList(rawResult, true);
            }
        } catch (HaloStorageParseException e) {

        }
        return result;
    }

}
