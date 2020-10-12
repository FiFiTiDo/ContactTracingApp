package edu.temple.contacttracer;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Date;

import edu.temple.contacttracer.support.DateUtils;
import edu.temple.contacttracer.support.interfaces.GlobalStateManager;
import edu.temple.contacttracer.support.interfaces.MainPageButtonListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainPageFragment extends Fragment {
    private MainPageButtonListener listener = null;
    private GlobalStateManager global;

    public MainPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainPage.
     */
    public static MainPageFragment newInstance() {
        return new MainPageFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        global = (GlobalStateManager) context.getApplicationContext();

        if (context instanceof MainPageButtonListener) {
            this.listener = (MainPageButtonListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnClickTrackingButtonListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_page, container, false);
        root.findViewById(R.id.startTrackingButton).setOnClickListener(view -> listener.onStartTracking());
        root.findViewById(R.id.stopTrackingButton).setOnClickListener(view -> listener.onStopTracking());
        root.findViewById(R.id.reportButton).setOnClickListener(view -> {
            Calendar today = Calendar.getInstance();
            Calendar fourteenDays = Calendar.getInstance();

            DateUtils.trimCalendar(today);
            DateUtils.trimCalendar(fourteenDays);
            fourteenDays.add(Calendar.DAY_OF_MONTH, -14);

            DatePickerDialog dialog = new DatePickerDialog(getContext(), (datePicker, year, monthOfYear, dayOfMonth) -> {
                Calendar report = Calendar.getInstance();
                report.set(Calendar.YEAR, year);
                report.set(Calendar.MONTH, monthOfYear);
                report.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Log.d("Test", "Date selected: " + report.getTime().toString());
                new Thread(() ->  global.getApiManager().sendReport(global.getDb().locationDao().getRecent(), report.getTime())).start();
            }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));

            dialog.getDatePicker().setMinDate(fourteenDays.getTimeInMillis());
            dialog.getDatePicker().setMaxDate(today.getTimeInMillis());
            dialog.setMessage(getString(R.string.report_dialog_title));
            dialog.show();
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings_item) {
            listener.onOpenSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}