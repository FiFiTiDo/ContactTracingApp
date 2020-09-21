package net.fifitido.contact;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.fifitido.contact.listeners.MainPageButtonListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainPageFragment extends Fragment {
    private static final String LISTENER_KEY = "click_listener";
    private MainPageButtonListener listener = null;

    public MainPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

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