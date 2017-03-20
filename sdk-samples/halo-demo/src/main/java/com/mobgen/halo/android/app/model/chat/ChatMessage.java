package com.mobgen.halo.android.app.model.chat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.auth.models.Register;
import com.mobgen.halo.android.content.annotations.HaloConstructor;
import com.mobgen.halo.android.content.annotations.HaloQueries;
import com.mobgen.halo.android.content.annotations.HaloQuery;
import com.mobgen.halo.android.content.annotations.HaloSearchable;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.client.response.Parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by mobgenimac on 14/3/17.
 */

@JsonObject
@HaloSearchable(version = 7 , tableName = "ChatMessage")
@HaloQueries(queries = {@HaloQuery(name = "getMessages" ,  query = "SELECT * from ChatMessage where alias = @{alias:String} AND isMultiple = @{isMultiple:Boolean} ORDER BY createdate ASC"),
                        @HaloQuery(name = "getMessagesMultipleRoom" ,  query = "SELECT * from ChatMessage where isMultiple = @{isMultiple:Boolean} ORDER BY createdate ASC"),
                        @HaloQuery(name = "insertMessage" , query = "INSERT INTO ChatMessage(alias,username,message,createdate,isMultiple,isFromSender) VALUES (@{alias:String},@{username:String},@{message:String},@{createdate:Date},@{isMultiple:Boolean},@{isFromSender:Boolean})")})
public class ChatMessage {
    @JsonField(name = "username")
    String mUserName;

    @JsonField(name = "alias")
    String mAlias;

    @JsonField(name = "message")
    String mMessage;

    @JsonField(name = "createDate")
    Date mCreationDate;

    Boolean mIsMultiple;

    Boolean mIsFromSender;

    public ChatMessage(){

    }

    @HaloConstructor(columnNames = {"alias","username","message","createdate","isMultiple","isFromSender"})
    public ChatMessage(@NonNull String alias,@NonNull String username, @NonNull String message, @NonNull Date creationDate, Boolean isMultiple, Boolean isFromSender){
        mAlias = alias;
        mUserName = username;
        mMessage = message;
        mCreationDate = creationDate;
        mIsMultiple = isMultiple;
        mIsFromSender = isFromSender;
    }

    public String getAlias() {
        return mAlias;
    }

    public void setAlias(String alias) {
        this.mAlias = alias;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public Date getCreationDate() {
        return mCreationDate;
    }

    public void setCreationDate(Date mCreationDate) {
        this.mCreationDate = mCreationDate;
    }

    public Boolean getIsMultiple() {
        return mIsMultiple;
    }

    public void setIsMultiple(Boolean mIsMultiple) {
        this.mIsMultiple = mIsMultiple;
    }

    public Boolean getIsFromSender() {
        return mIsFromSender;
    }

    public void setIsFromSender(Boolean mIsFromSender) {
        this.mIsFromSender = mIsFromSender;
    }


    /**
     * Parses the a chat message
     *
     * @param chatMessage   The chat message as string.
     * @param parser The parser.
     * @return The chat parsed or an empty chat if the string passed is null.
     * @throws HaloParsingException Error parsing the item.
     */
    @Nullable
    public static ChatMessage deserialize(@Nullable String chatMessage, @NonNull Parser.Factory parser) throws HaloParsingException {
        if (chatMessage != null) {
            try {
                return ((Parser<InputStream, ChatMessage>) parser.deserialize(ChatMessage.class)).convert(new ByteArrayInputStream(chatMessage.getBytes()));
            } catch (IOException e) {
                throw new HaloParsingException("Error while deserializing the chat message", e);
            }
        }
        return null;
    }
}
