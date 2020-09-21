package net.fifitido.contact;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import net.fifitido.contact.listeners.SettingsListener;

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

        EditTextPreference tracingPref = getPreferenceManager().findPreference("tracing_distance");
        if (tracingPref != null) {
            tracingPref.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
            });

            tracingPref.setOnPreferenceChangeListener((preference, newValue) -> {
                int intVal = Integer.parseInt((String) newValue);
                if (intVal < 1) {
                    Toast.makeText(getActivity(), getString(R.string.too_short_distance), Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (intVal > 10) {
                    Toast.makeText(getActivity(), getString(R.string.too_far_distance), Toast.LENGTH_SHORT).show();
                    return false;
                }

                return true;
            });
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