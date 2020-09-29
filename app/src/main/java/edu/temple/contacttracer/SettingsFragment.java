package edu.temple.contacttracer;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import edu.temple.contacttracer.support.EditIntegerPreference;
import edu.temple.contacttracer.support.interfaces.SettingsListener;

public class SettingsFragment extends PreferenceFragmentCompat {
    private SettingsListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof SettingsListener) {
            listener = (SettingsListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement GenerateIdListener");
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        EditIntegerPreference tracingPref = getPreferenceManager().findPreference("tracing_distance");
        if (tracingPref != null) {
            tracingPref.setOnNumberChangedListener((preference, newValue) -> listener.onDistanceUpdate(newValue));
        }

        Preference regeneratePref = getPreferenceManager().findPreference("regenerate_uuid");
        if (regeneratePref != null) {
            regeneratePref.setOnPreferenceClickListener(preference -> {
                listener.onGenerateId();
                return true;
            });
        }
    }
}