package com.mobgen.halo.android.content.generated;

import android.database.Cursor;
import android.database.SQLException;
import android.support.annotation.BoolRes;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.content.models.GeneratedContent;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.spec.HaloContentContract;
import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.storage.database.HaloDataLite;
import com.mobgen.halo.android.sdk.api.Halo;

import java.util.Date;

/**
 * The data source to perfom queries against local database.
 */
@Keep
public class GeneratedContentQueriesLocalDataSource {
    /**
     *  Helper to perfom operation on databse
     */
    private HaloDataLite mDataLite;

    /**
     * The constructor of the local data source.
     * @param haloFramework The framewrok.
     */
    public GeneratedContentQueriesLocalDataSource(@NonNull HaloFramework haloFramework) {
        mDataLite = haloFramework.storage(HaloContentContract.HALO_CONTENT_STORAGE).db();
    }

    /**
     * Perfom the query against dabase from annotated code.
     *
     * @param query the query to perfom.
     * @param bindArgs the args to the query.
     * @return A cursor raw response from databse
     * @throws SQLException
     */
    @Nullable
    public Cursor perfomQuery(@NonNull String query,@NonNull Object[] bindArgs) throws SQLException {
        //convert obects to string to perfom raw queries
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
            } else if(bindArgs[i] instanceof Boolean) {
                bindstringArgs[i] = String.valueOf(bindArgs[i]);
            } else {
                try {
                    bindstringArgs[i] = GeneratedContent.serialize(bindArgs[i], Halo.instance().framework().parser());
                } catch (HaloParsingException e) {
                    bindstringArgs[i] = "";
                }
            }
        }
        Cursor rawResult = mDataLite.getDatabase().rawQuery(query,bindstringArgs);
        if(rawResult!=null)rawResult.moveToFirst();
        return rawResult;
    }

}
