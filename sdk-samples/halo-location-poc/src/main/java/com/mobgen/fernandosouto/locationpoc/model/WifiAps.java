package com.mobgen.fernandosouto.locationpoc.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.content.models.BatchDeletedInstance;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.response.Parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by f.souto.gonzalez on 29/05/2017.
 */
@JsonObject
public class WifiAps {
    @JsonField( name = "macAddress")
    String BSSID;
    @JsonField( name = "distance")
    double distance;
    @JsonField( name = "roomname")
    String roomName;
    @JsonField( name = "wifiname")
    String SSID;
    double difference;

    public WifiAps(){}

    public WifiAps(String bssid, String ssid, double distance, String roomName){
        this.BSSID = bssid;
        this.distance = distance;
        this.roomName = roomName;
        this.SSID = ssid;
        this.difference = 0;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setDifference(double diff){
        difference = diff;
    }

    public double getDifference(){
        return difference;
    }

    public static String serialize(@NonNull List<WifiAps> haloContentInstance, @NonNull Parser.Factory parser) throws HaloParsingException {
        AssertionUtils.notNull(haloContentInstance, "haloContentInstance");
        AssertionUtils.notNull(parser, "parser");
        try {
            return ((Parser<List<WifiAps>, String>) parser.serialize(HaloContentInstance.class)).convert(haloContentInstance);
        } catch (IOException e) {
            throw new HaloParsingException("Error while serializing the HaloContentInstance", e);
        }
    }

    @Nullable
    public static List<WifiAps> deserialize(@Nullable String batchDeletions, @NonNull Parser.Factory parser) throws HaloParsingException {
        if (batchDeletions != null) {
            try {
                return ((Parser<InputStream, List<WifiAps>>) parser.deserialize(WifiAps.class)).convert(new ByteArrayInputStream(batchDeletions.getBytes()));
            } catch (IOException e) {
                throw new HaloParsingException("Error while deserializing the BatchDeletedInstance", e);
            }
        }
        return null;
    }

}
