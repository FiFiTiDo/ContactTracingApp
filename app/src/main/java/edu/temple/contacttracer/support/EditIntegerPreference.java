package edu.temple.contacttracer.support;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;

import edu.temple.contacttracer.R;

public class EditIntegerPreference extends EditTextPreference {
    private OnNumberChangedListener onNumberChangedListener;
    private int minValue;
    private int maxValue;
    public EditIntegerPreference(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);

        minValue = attrs.getAttributeIntValue(null, "minValue", 0);
        maxValue = attrs.getAttributeIntValue(null, "maxValue", 100);

        setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED));
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

            onNumberChangedListener.onNumberChanged(preference, intVal);
            return true;
        });
    }

    public void setOnNumberChangedListener(OnNumberChangedListener listener) {
        onNumberChangedListener = listener;
    }

    public interface OnNumberChangedListener {
        void onNumberChanged(Preference preference, int newValue);
    }
}
