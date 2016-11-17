package com.codyy.erpsportal.commons.models.entities.configs;

import android.os.Environment;


import com.codyy.erpsportal.commons.utils.Constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationDefaults {
	private final Map<String, Object> defaultValues;
    private final Map<String, Object> resetValues;
    
    public ConfigurationDefaults() {
        defaultValues = new HashMap<>();
        resetValues = new HashMap<>();
        load();
    }
    
    public Map<String, Object> getDefaultValues() {
        return Collections.unmodifiableMap(defaultValues);
    }

    public Map<String, Object> getResetValues() {
        return Collections.unmodifiableMap(resetValues);
    }
    
    private void load() {
        defaultValues.put(Constants.PREF_KEY_STORAGE_PATH, Environment.getExternalStorageDirectory().getAbsolutePath()); // /mnt/sdcard
        defaultValues.put(Constants.PREF_KEY_STORAGE_PATH_SUB, "classroom"); // elrt
    }
    
}
