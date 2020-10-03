package edu.temple.contacttracer.support;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;

import edu.temple.contacttracer.R;

/**
 * An extension on the default EditTextPreference to allow only numerical inputs, also allows
 * for setting a minimum and maximum values via an attribute. Created mainly to avoid code
 * duplication as it is used for both time and distance.
 */
public class EditIntegerPreference extends EditTextPreference {
    private OnNumberChangedListener onNumberChangedListener;
    private int minValue;
    private int maxValue;

    public EditIntegerPreference(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);

        minValue = attrs.getAttributeIntValue(null, "minValue", 0);
        maxValue = attrs.getAttributeIntValue(null, "maxValue", 100);

        // Set EditText field to number
        setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED));

        // Check for preference change to validate minimum and maximum
        setOnPreferenceChangeListener((preference, newValue) -> {
            int intVal = Integer.parseInt((String) newValue);

            if (intVal < minValue) {
                Toast.makeText(ctx, ctx.getString(R.string.too_small, minValue), Toast.LENGTH_SHORT).show();
                return false;
            }

            if (intVal > maxValue) {
                Toast.makeText(ctx, ctx.getString(R.string.too_large, maxValue), Toast.LENGTH_SHORT).show();
                return false;
            }

            // Alert listener of changed number value
            onNumberChangedListener.onNumberChanged(preference, intVal);
            return true;
        });
    }

    /**
     * Update the OnNumberChangedListener
     *
     * @param listener The new listener
     */
    public void setOnNumberChangedListener(OnNumberChangedListener listener) {
        onNumberChangedListener = listener;
    }

    /**
     * Listen for updates of the number value of the preference
     */
    public interface OnNumberChangedListener {
        void onNumberChanged(Preference preference, int newValue);
    }
}
