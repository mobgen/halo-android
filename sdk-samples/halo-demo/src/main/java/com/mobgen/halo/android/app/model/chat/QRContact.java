package com.mobgen.halo.android.app.model.chat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.content.annotations.HaloConstructor;
import com.mobgen.halo.android.content.annotations.HaloField;
import com.mobgen.halo.android.content.annotations.HaloQueries;
import com.mobgen.halo.android.content.annotations.HaloQuery;
import com.mobgen.halo.android.content.annotations.HaloSearchable;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.client.response.Parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mobgenimac on 14/3/17.
 */
@JsonObject
@HaloSearchable(version = 11 , tableName = "QRContact")
@HaloQueries(queries = {@HaloQuery(name = "getContactImage" ,  query = "SELECT * from QRContact where Alias = @{mAlias:String}"),
                        @HaloQuery(name = "getUserName" ,  query = "SELECT * from QRContact where Alias = @{mAlias:String}"),
                        @HaloQuery(name = "getContacts" ,  query = "SELECT * from QRContact where Alias != @{mAlias:String} AND Alias!=@{mMultiple:String}"),
                        @HaloQuery(name = "getConversations" ,  query = "SELECT * from QRContact where Alias != @{mAlias:String} ORDER BY Name ASC"),
                        @HaloQuery(name = "insertContact", query = "INSERT OR REPLACE INTO QRContact(Alias,Name,Image) VALUES (@{mAlias:String},@{mName:String},@{mImage:String})"),
                        @HaloQuery(name = "deleteContact", query = "DELETE FROM QRContact where Alias = @{mAlias:String}")})
public class QRContact {
    @HaloField(index = true, columnName = "Alias")
    @JsonField(name = "Alias")
    String mAlias;

    @JsonField(name = "Name")
    String mName;

    @JsonField(name = "Image")
    String mImage;

    public QRContact(){

    }

    @HaloConstructor(columnNames = {"Alias","Name","Image"})
    public QRContact(@NonNull String alias, @NonNull String name, @NonNull String image){
        mImage = image;
        mName = name;
        mAlias = alias;
    }

    public String getAlias() {
        return mAlias;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setAlias(String mAlias) {
        this.mAlias = mAlias;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    /**
     * Parses the qrcontact.
     *
     * @param qrContact   The qr contact as string.
     * @param parser The parser.
     * @return The qr contact or an empty qr contact if the string passed is null.
     * @throws HaloParsingException Error parsing the item.
     */
    @Nullable
    public static QRContact deserialize(@Nullable String qrContact, @NonNull Parser.Factory parser) throws HaloParsingException {
        if (qrContact != null) {
            try {
                return ((Parser<InputStream, QRContact>) parser.deserialize(QRContact.class)).convert(new ByteArrayInputStream(qrContact.getBytes()));
            } catch (IOException e) {
                throw new HaloParsingException("Error while deserializing the qr contact", e);
            }
        }
        return null;
    }
}
