package com.mobgen.halo.android.framework.storage.preference;


import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloPreferenceStorageTest extends HaloRobolectricTest {

    private HaloPreferencesStorage mHaloPreferenceStorage;
    private String mPrefID="halo-pref-id";

    @Before
    public void initialize() {
       mHaloPreferenceStorage = new  HaloPreferencesStorage(RuntimeEnvironment.application, mPrefID);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void thatPreferenceIdIsOk(){
        assertThat(mHaloPreferenceStorage.getPreferenceId()).isEqualTo(mPrefID);
    }


    @Test
    public void thatPreferencesNotSaveWithoutCommit(){
        HaloPreferencesStorage.HaloPreferencesStorageEditor editor = mHaloPreferenceStorage.edit();
        editor.putString("testString","This is it");
        assertThat(mHaloPreferenceStorage.getString("testString","halo-default")).isEqualTo("halo-default");
    }

    @Test
    public void thatCanGetAndSetAStringFromPrefereces(){
        HaloPreferencesStorage.HaloPreferencesStorageEditor editor = mHaloPreferenceStorage.edit();
        editor.putString("testString","This is it");
        editor.commit();
        assertThat(mHaloPreferenceStorage.getString("testString","halo-default")).isEqualTo("This is it");
    }

    @Test
    public void thatCanRemoveAEntryFromPrefereces(){
        HaloPreferencesStorage.HaloPreferencesStorageEditor editor = mHaloPreferenceStorage.edit();
        editor.putString("testString","This is it");
        editor.commit();
        editor.remove("testString");
        editor.apply();
        assertThat(mHaloPreferenceStorage.getString("testString","halo-default")).isEqualTo("halo-default");
    }

    @Test
    public void thatCanClearPrefereces(){
        HaloPreferencesStorage.HaloPreferencesStorageEditor editor = mHaloPreferenceStorage.edit();
        editor.putString("testString","This is it");
        editor.commit();
        editor.clear();
        editor.apply();
        assertThat(mHaloPreferenceStorage.getString("testString","halo-default")).isEqualTo("halo-default");
    }

    @Test
    public void thatCanGetAndSetAIntFromPrefereces(){
        HaloPreferencesStorage.HaloPreferencesStorageEditor editor = mHaloPreferenceStorage.edit();
        editor.putInt("testInt",1034);
        editor.commit();
        assertThat(mHaloPreferenceStorage.getInteger("testInt",-1)).isEqualTo(1034);
    }

    @Test
    public void thatCanGetAndSetALongFromPrefereces(){
        HaloPreferencesStorage.HaloPreferencesStorageEditor editor = mHaloPreferenceStorage.edit();
        editor.putLong("testInt",1034L);
        editor.commit();
        assertThat(mHaloPreferenceStorage.getLong("testInt",1L)).isEqualTo(1034L);
    }

    @Test
    public void thatCanGetAndSetAFloatFromPrefereces(){
        HaloPreferencesStorage.HaloPreferencesStorageEditor editor = mHaloPreferenceStorage.edit();
        editor.putFloat("testInt",103.4f);
        editor.commit();
        assertThat(mHaloPreferenceStorage.getFloat("testInt",1.0f)).isEqualTo(103.4f);
    }

    @Test
    public void thatCanGetAndSetABooleanFromPrefereces(){
        HaloPreferencesStorage.HaloPreferencesStorageEditor editor = mHaloPreferenceStorage.edit();
        editor.putBoolean("testBool",true);
        editor.commit();
        assertThat(mHaloPreferenceStorage.getBoolean("testBool",false)).isEqualTo(true);
    }

    @Test
    public void thatCanGetAndSetAJSONObjectFromPreferences() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nameData","valueData");
        HaloPreferencesStorage.HaloPreferencesStorageEditor editor = mHaloPreferenceStorage.edit();
        editor.putJson("testJSONObject",jsonObject);
        editor.commit();
        assertThat(mHaloPreferenceStorage.getJsonObject("testJSONObject",new JSONObject()).get("nameData")).isEqualTo(jsonObject.get("nameData"));
    }

    @Test
    public void thatCanGetAndSetAJSONArrayFromPreferences() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nameData","valueData");
        jsonArray.put(jsonObject);
        HaloPreferencesStorage.HaloPreferencesStorageEditor editor = mHaloPreferenceStorage.edit();
        editor.putJsonArray("testJSONObject",jsonArray);
        editor.commit();
        assertThat(mHaloPreferenceStorage.getJsonArray("testJSONObject",new JSONArray()).length()).isEqualTo(1);
        assertThat(mHaloPreferenceStorage.getJsonArray("testJSONObject",new JSONArray()).getJSONObject(0).get("nameData")).isEqualTo(jsonObject.get("nameData"));
    }

    @Test
    public void thatCanGetAndSetAStringSetFromPreferences(){
        String[] words = {"a", "b", "c", "d", "e"};
        Set<String> values = new HashSet<String>(Arrays.asList(words));
        HaloPreferencesStorage.HaloPreferencesStorageEditor editor = mHaloPreferenceStorage.edit();
        editor.putStringSet("testStringSet",values);
        editor.commit();
        assertThat(mHaloPreferenceStorage.getStringSet("testStringSet",null).size()).isEqualTo(5);
        assertThat(mHaloPreferenceStorage.getStringSet("testStringSet",null).contains("a")).isTrue();
    }

    @Test
    public void thatCanRetrieveDefaultValues() throws JSONException {
        assertThat(mHaloPreferenceStorage.getString("testString","halo-default")).isEqualTo("halo-default");
        assertThat(mHaloPreferenceStorage.getBoolean("testBool",false)).isEqualTo(false);
        assertThat(mHaloPreferenceStorage.getInteger("testInt",1034)).isEqualTo(1034);
        assertThat(mHaloPreferenceStorage.getLong("testInt",1034L)).isEqualTo(1034L);
        assertThat(mHaloPreferenceStorage.getFloat("testInt",103.4f)).isEqualTo(103.4f);
        assertThat(mHaloPreferenceStorage.getJsonObject("testJSONObject",null)).isNull();
        assertThat(mHaloPreferenceStorage.getJsonArray("testJSONObject",null)).isNull();
        assertThat(mHaloPreferenceStorage.getStringSet("testStringSet",null)).isNull();
    }

}
