package com.codyy.erpsportal.commons.models.entities.configs;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

import com.codyy.erpsportal.commons.exception.CoreRuntimeException;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ByteUtils;
import com.codyy.erpsportal.commons.utils.StringUtils;

import java.util.Map;
import java.util.Map.Entry;

public class ConfigurationManager {

    private final SharedPreferences preferences;
    private final Editor editor;

    private final ConfigurationDefaults defaults;

    private static ConfigurationManager instance;

    public synchronized static void create(Application context) {
        if (instance != null) {
            return;
        }
        instance = new ConfigurationManager(context);
    }

    public static ConfigurationManager instance() {
        if (instance == null) {
            throw new CoreRuntimeException("ConfigurationManager not created");
        }
        return instance;
    }

    private ConfigurationManager(Application context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();

        defaults = new ConfigurationDefaults();

        initPreferences();
    }

    public String getString(String key) {
        return preferences.getString(key, null);
    }

    public void setString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    public void setInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public long getLong(String key) {
        return preferences.getLong(key, 0);
    }

    public void setLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public byte[] getByteArray(String key) {
        String str = getString(key);

        if (StringUtils.isNullOrEmpty(str)) {
            return null;
        }

        return ByteUtils.decodeHex(str);
    }

    public void setByteArray(String key, byte[] value) {
        setString(key, ByteUtils.encodeHex(value));
    }

    public void resetToDefaults() {
        resetToDefaults(defaults.getResetValues());
    }
    
   /* public boolean getNoProgramFlag() {
    	return getBoolean(Constants.PREF_KEY_STR_NO_PROGRAM);
    }

    public void setNoProgramFlag(boolean value) {
    	setBoolean(Constants.PREF_KEY_STR_NO_PROGRAM, value);
    }

    public boolean vibrateOnFinishedDownload() {
        return getBoolean(Constants.PREF_KEY_GUI_VIBRATE_ON_FINISHED_DOWNLOAD);
    }

    public int maxConcurrentUploads() {
        return getInt(Constants.PREF_KEY_NETWORK_MAX_CONCURRENT_UPLOADS);
    }

    public boolean showTransfersOnDownloadStart() {
        return getBoolean(Constants.PREF_KEY_GUI_SHOW_TRANSFERS_ON_DOWNLOAD_START);
    }

    public boolean showShareIndication() {
        return getBoolean(Constants.PREF_KEY_GUI_SHOW_SHARE_INDICATION);
    }*/

    public void registerOnPreferenceChange(OnSharedPreferenceChangeListener listener) {
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnPreferenceChange(OnSharedPreferenceChangeListener listener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    /**
     * 每次应用起来都初始化
     */
    private void initPreferences() {
    	
        for (Entry<String, Object> entry : defaults.getDefaultValues().entrySet()) {
            if (entry.getValue() instanceof String) {
                initStringPreference(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof Integer) {
                initIntPreference(entry.getKey(), (Integer) entry.getValue());
            } else if (entry.getValue() instanceof Long) {
                initLongPreference(entry.getKey(), (Long) entry.getValue());
            } else if (entry.getValue() instanceof Boolean) {
                initBooleanPreference(entry.getKey(), (Boolean) entry.getValue());
            } else if (entry.getValue() instanceof byte[]) {
                initByteArrayPreference(entry.getKey(), (byte[]) entry.getValue());
            }
        }

        //there are some configuration values that need to be reset every time to a desired value
        resetToDefaults(defaults.getResetValues());
    }

    private void initStringPreference(String prefKeyName, String defaultValue) {
        if (!preferences.contains(prefKeyName)) {
            setString(prefKeyName, defaultValue);
        }
    }

    private void initByteArrayPreference(String prefKeyName, byte[] defaultValue) {
        if (!preferences.contains(prefKeyName)) {
            setByteArray(prefKeyName, defaultValue);
        }
    }

    private void initBooleanPreference(String prefKeyName, boolean defaultValue) {
        if (!preferences.contains(prefKeyName)) {
            setBoolean(prefKeyName, defaultValue);
        }
    }

    private void initIntPreference(String prefKeyName, int defaultValue) {
        if (!preferences.contains(prefKeyName)) {
            setInt(prefKeyName, defaultValue);
        }
    }

    private void initLongPreference(String prefKeyName, long defaultValue) {
        if (!preferences.contains(prefKeyName)) {
            setLong(prefKeyName, defaultValue);
        }
    }

    private void resetToDefaults(Map<String, Object> map) {
    	
        for (Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof String) {
                setString(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof Integer) {
                setInt(entry.getKey(), (Integer) entry.getValue());
            } else if (entry.getValue() instanceof Long) {
                setLong(entry.getKey(), (Long) entry.getValue());
            } else if (entry.getValue() instanceof Boolean) {
            	Cog.i("ConfigurationDefaults", entry.getKey() + "=" + entry.getValue());
                setBoolean(entry.getKey(), (Boolean) entry.getValue());
            } else if (entry.getValue() instanceof byte[]) {
                setByteArray(entry.getKey(), (byte[]) entry.getValue());
            }
        }
    }
}
