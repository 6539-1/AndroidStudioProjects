package com.example.administrator.jsip;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.example.administrator.jsip.R;


public class SettingsActivity extends PreferenceActivity {
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);

    }
}