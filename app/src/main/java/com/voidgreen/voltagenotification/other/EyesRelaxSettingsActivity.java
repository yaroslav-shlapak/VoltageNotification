package com.voidgreen.voltagenotification.other;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;

import com.voidgreen.eyesrelax.preferences.NumberPickerRelaxPreference;
import com.voidgreen.eyesrelax.preferences.NumberPickerWorkPreference;

/**
 * Created by y.shlapak on Jun 25, 2015.
 */
public class EyesRelaxSettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        PreferenceManager.setDefaultValues(EyesRelaxSettingsActivity.this, R.xml.preferences, false);
        initSummary(getPreferenceScreen());

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePrefSummary(findPreference(key));
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    private void updatePrefSummary(Preference p) {
        if (p instanceof NumberPickerRelaxPreference) {
            NumberPickerRelaxPreference preference = (NumberPickerRelaxPreference) p;
            p.setSummary(preference.VALUES[preference.getValue()] + " seconds");
        }
        if (p instanceof NumberPickerWorkPreference) {
            NumberPickerWorkPreference preference = (NumberPickerWorkPreference) p;
            p.setSummary(preference.VALUES[preference.getValue()] + " minutes");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


/*    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }*/
}
