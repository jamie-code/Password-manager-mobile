package com.example.hshmobile;

import android.content.Context;
import android.content.SharedPreferences;

public class CredentialManager {
    private static final String PREFS_NAME = "B_PasswordManager";
    private static final String VAULT_KEY = "vaultKey";
    private static final String EMAIL = "email";
    private static final String SESSION_ID = "sessionId";

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

    public void saveCredentials(String vaultKey, String email, String sessionId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(VAULT_KEY, vaultKey);
        editor.putString(EMAIL, email);
        editor.putString(SESSION_ID, sessionId);
        editor.apply();
    }

    public String getVaultKey() {
        return sharedPreferences.getString(VAULT_KEY, "");
    }

    public String getEmail() {
        return sharedPreferences.getString(EMAIL, "");
    }

    public String getSessionId() {
        return sharedPreferences.getString(SESSION_ID, "");
    }
}
