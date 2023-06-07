package com.example.hshmobile;

import android.content.Context;
import android.content.SharedPreferences;

public class CredentialManager {
    private static final String PREFS_NAME = "PasswordPrefs";
    private static final String SALT_KEY = "salt";
    private static final String HASH_KEY = "hash";

    private static CredentialManager instance;
    private SharedPreferences sharedPreferences;

    private CredentialManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized CredentialManager getInstance(Context context) {
        if (instance == null) {
            instance = new CredentialManager(context);
        }
        return instance;
    }

    public void saveCredentials(String salt, String hash) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SALT_KEY, salt);
        editor.putString(HASH_KEY, hash);
        editor.apply();
    }

    public String getSalt() {
        return sharedPreferences.getString(SALT_KEY, "");
    }

    public String getHash() {
        return sharedPreferences.getString(HASH_KEY, "");
    }
}

