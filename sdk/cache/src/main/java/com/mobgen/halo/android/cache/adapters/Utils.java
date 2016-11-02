package com.mobgen.halo.android.cache.adapters;

import android.support.annotation.NonNull;
import android.util.Base64;

import com.mobgen.halo.android.cache.CacheException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class Utils {

    private static final int BUFFER_SIZE = 32;

    private Utils() {
        //Do not allow instances
    }

    public static String compress(@NonNull String data, boolean shouldCompress) throws CacheException {
        String result = data;
        if (shouldCompress) {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream(data.length());
                GZIPOutputStream gos = new GZIPOutputStream(os);
                gos.write(data.getBytes("UTF-8"));
                gos.close();
                byte[] compressed = os.toByteArray();
                os.close();
                result = Base64.encodeToString(compressed, Base64.DEFAULT);
            } catch (IOException e) {
                throw new CacheException(e, "Error while compressing an item");
            }
        }
        return result;
    }

    public static String decompress(String compressed, boolean shouldCompress) throws CacheException {
        String result = compressed;
        if (shouldCompress) {
            try {
                ByteArrayInputStream is = new ByteArrayInputStream(Base64.decode(compressed.getBytes(), Base64.DEFAULT));
                GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
                StringBuilder string = new StringBuilder();
                byte[] data = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = gis.read(data)) != -1) {
                    string.append(new String(data, 0, bytesRead));
                }
                gis.close();
                is.close();
                result = string.toString();
            } catch (IOException e) {
                throw new CacheException(e, "Error while decompressing one item");
            }
        }
        return result;
    }
}
